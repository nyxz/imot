package net.badowl.imot;

import net.badowl.imot.email.SendGridEmailSender;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PropertyScraper {
    private final static Logger LOG = LoggerFactory.getLogger(PropertyScraper.class);

    private static final String SELECTOR_DETAILS_WRAPPER = "form[name=search] > table.newAdImages + div ";
    private static final String SELECTOR_TYPE = SELECTOR_DETAILS_WRAPPER + "> strong:containsOwn(Продава)";
    private static final String SELECTOR_NEIGHBOURHOOD = SELECTOR_DETAILS_WRAPPER + "> span";
    private static final String SELECTOR_PRICE = "span#cena";
    private static final String SELECTOR_DESCRIPTION = "div#description_div";
    private static final String SELECTOR_SIZE =
            SELECTOR_DETAILS_WRAPPER + "> ul.imotData > li:containsOwn(Квадратура) + li";
    private static final String SELECTOR_FLOOR =
            SELECTOR_DETAILS_WRAPPER + "> ul.imotData > li:containsOwn(Етаж) + li";
    private static final String SELECTOR_BUILD_TYPE =
            SELECTOR_DETAILS_WRAPPER + "> ul.imotData > li:containsOwn(Строителство) + li";
    private static final String SELECTOR_SELLER_PHONE =
            "img[src='../images/picturess/phone-ico.gif'] + span:nth-of-type(1)";
    private static final String SELECTOR_SELLER_NAME = "div[style='float:left; margin-right:20px'] > b";

    @Autowired
    private PropertyRepo propertyRepo;

    @Autowired
    private AreaRepo areaRepo;

    @Autowired
    private SendGridEmailSender emailSender;

    @Async
    @Transactional
    public CompletableFuture<Void> scrapeAndNotify() throws Exception {
        return scrapeAll().thenCompose(x -> sendNotifications());
    }

    @Transactional
    @Async
    public CompletableFuture<Void> scrapeAll() throws Exception {
        LOG.info("Logging started...");
        final String initialUrl =
                "https://imoti-sofia.imot.bg/pcgi/imot.cgi?act=11&f1=1&f2=1&f3=3&f4=%E3%F0%E0%E4%20%D1%EE%F4%E8%FF&f5=";
        final List<String> areas = areaRepo.list();
        Collections.sort(areas);
        for (String area : areas) {
            LOG.info("Scraping area: " + area);
            scrape(area, initialUrl);
            Thread.sleep(3000);
        }
        LOG.info("Scraping done.");
        return CompletableFuture.completedFuture(null);
    }

    private void scrape(String area, String initialUrl) throws IOException {
        final Document document = Jsoup.connect(initialUrl).get();
        String areaHref = document.select("a.qLinks12:matchesOwn(" + area + ")").attr("href");
        if (StringUtils.isEmpty(areaHref)) {
            return;
        }
        final String fullAreaHref = fromPartialUrl(areaHref);
        final Document areaPage = Jsoup.connect(fullAreaHref).get();
        final int pagesNum = pagesCount(areaPage);

        final List<String> allUrls = new ArrayList<>();
        for (int currPage = 0; currPage < pagesNum; currPage++) {
            final String numberedAreaPageHref =
                    UriComponentsBuilder.fromUriString(fullAreaHref)
                            .queryParam("f6", (currPage + 1))
                            .build()
                            .toUri()
                            .toString();
            final Document numberedAreaPage = Jsoup.connect(numberedAreaPageHref).get();
            final List<String> urls = numberedAreaPage.select("a.lnk2").stream()
                    .map(link -> fromPartialUrl(link.attr("href"))).collect(Collectors.toList());
            allUrls.addAll(urls);
        }
        final List<Property> properties = allUrls.stream().map(url -> this.toProperty(url, area))
                .collect(Collectors.toList());
        propertyRepo.insert(properties);
    }

    private int pagesCount(Document areaPage) {
        final String pagesNumText = areaPage.select("span.pageNumbersInfo").text();
        final String[] pagesTextSplit = pagesNumText.split("\\s");
        final String pagesCountText = pagesTextSplit[pagesTextSplit.length - 1];
        if (StringUtils.isEmpty(pagesCountText)) {
            return 1;
        } else {
            return Integer.parseInt(pagesCountText);
        }
    }

    private Property toProperty(String url, String area) {
        final Document doc = getDocSafe(Jsoup.connect(url));
        final String type = doc.select(SELECTOR_TYPE).text().replace("Продава ", "");
        final String neighbourhood = doc.select(SELECTOR_NEIGHBOURHOOD).get(0).text();
        final String price = doc.select(SELECTOR_PRICE).text();
        final String description = doc.select(SELECTOR_DESCRIPTION).text();
        final String size = doc.select(SELECTOR_SIZE).text();
        final String buildType = doc.select(SELECTOR_BUILD_TYPE).text();
        final String floor = doc.select(SELECTOR_FLOOR).text();
        final String sellerPhone = doc.select(SELECTOR_SELLER_PHONE).text();
        final String sellerName = doc.select(SELECTOR_SELLER_NAME).text();

        return Property.builder()
                .url(url)
                .type(type)
                .area(area)
                .address(neighbourhood)
                .rawPrice(price)
                .price(PropertyUtil.toPrice(price))
                .description(description)
                .size(PropertyUtil.toSize(size))
                .buildType(buildType)
                .buildYear(PropertyUtil.toBuildYear(buildType, description))
                .rawFloor(floor)
                .floor(PropertyUtil.toFloor(floor))
                .totalFloors(PropertyUtil.toTotalFloors(floor))
                .sellerPhone(sellerPhone)
                .sellerName(sellerName)
                .providerWebsite("imot.bg")
                .build();
    }

    private Document getDocSafe(Connection connection) {
        try {
            return connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String fromPartialUrl(String partialUrl) {
        return "https:" + partialUrl;
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Void> sendNotifications() {
        try {
            emailSender.send(getAllToDisplay());
            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<PropertyEmailData> getAllToDisplay() {
        return propertyRepo.findAllToDisplay();
    }
}
