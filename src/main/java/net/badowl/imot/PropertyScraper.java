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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyScraper {
    private final static Logger LOG = LoggerFactory.getLogger(PropertyScraper.class);

    private static final String SELECTOR_TYPE =
            "table[style*=\"margin-top:10px\"] > tbody > tr > " +
                    "td[height=\"235\"] > span:nth-of-type(1)";
    private static final String SELECTOR_NEIGHBOURHOOD =
            "table[style*=\"margin-top:10px\"] > tbody > tr > td[height=\"235\"] > " +
                    "span:nth-of-type(2)";
    private static final String SELECTOR_PRICE = "span#cena";
    private static final String SELECTOR_DESCRIPTION = "div#description_div";
    private static final String SELECTOR_SIZE =
            "table[style*=\"margin-top:10px\"] > tbody > tr > " +
                    "td[height=\"235\"] > table:nth-of-type(2) > tbody > tr:nth-of-type(1) > " +
                    "td:nth-of-type(2) > b";
    private static final String SELECTOR_BUILD_TYPE =
            "table[style*=\"margin-top:10px\"] > tbody > tr > " +
                    "td[height=\"235\"] > table:nth-of-type(2) > tbody > tr:nth-of-type(5) > " +
                    "td:nth-of-type(2) > b";
    private static final String SELECTOR_FLOOR =
            "table[style*=\"margin-top:10px\"] > tbody > tr > " +
                    "td[height=\"235\"] > table:nth-of-type(2) > tbody > tr:nth-of-type(2) > " +
                    "td:nth-of-type(2) > b";
    private static final String SELECTOR_SELLER_PHONE = "img[src='../images/picturess/phone" +
            "-ico.gif'] + span:nth-of-type(1)";
    private static final String SELECTOR_SELLER_NAME_1 =
            "form[action='/pcgi/imot.cgi'] > table:nth-of-type(6) > tbody > tr > td:nth-of-type" +
                    "(2) b";
    private static final String SELECTOR_SELLER_NAME_2 =
            "form[action='/pcgi/imot.cgi'] > table:nth-of-type(7) > tbody > tr > td:nth-of-type" +
                    "(2) b";

    @Autowired
    private PropertyRepo propertyRepo;

    @Autowired
    private AreaRepo areaRepo;

    @Autowired
    private SendGridEmailSender emailSender;

    @Transactional
    @Async
    public void scrapeAll() throws Exception {
        LOG.info("Logging started...");
        final String initialUrl = "https://imoti-sofia.imot.bg/pcgi/imot" +
                ".cgi?act=11&f1=1&f2=1&f3=3&f4=%E3%F0%E0%E4%20%D1%EE%F4%E8%FF&f5=";
        final List<String> areas = areaRepo.list();
        Collections.sort(areas);
        for (String area : areas) {
            LOG.info("Scraping area: " + area);
            scrape(area, initialUrl);
            Thread.sleep(3000);
        }
        LOG.info("Scraping done.");
    }

    private void scrape(String area, String initialUrl) throws IOException {
        final Document document = Jsoup.connect(initialUrl).get();
        String areaHref = document.select("a:containsOwn(" + area + ")").attr("href");
        if (StringUtils.isEmpty(areaHref)) {
            return;
        }
        final String fullAreaHref = fromPartialUrl(areaHref);
        final Document areaPage = Jsoup.connect(fullAreaHref).get();
        final int pagesNum = pagesCount(areaPage);

        final List<String> allUrls = new ArrayList<>();
        for (int currPage = 0; currPage < pagesNum; currPage++) {
            final String numberedAreaPageHref = fullAreaHref + "&f6=" + (currPage + 1);
            final Document numberedAreaPage = Jsoup.connect(numberedAreaPageHref).get();
            final List<String> urls = numberedAreaPage.select("a.lnk2")
                    .stream()
                    .map(link -> fromPartialUrl(link.attr("href")))
                    .collect(Collectors.toList());
            allUrls.addAll(urls);
        }
        final List<Property> properties =
                allUrls.stream().map(url -> this.toProperty(url, area))
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
        final String type = doc.select(SELECTOR_TYPE).text();
        final String neighbourhood = doc.select(SELECTOR_NEIGHBOURHOOD).text();
        final String price = doc.select(SELECTOR_PRICE).text();
        final String description = doc.select(SELECTOR_DESCRIPTION).text();
        final String size = doc.select(SELECTOR_SIZE).text();
        final String buildType = doc.select(SELECTOR_BUILD_TYPE).text();
        final String floor = doc.select(SELECTOR_FLOOR).text();
        final String sellerPhone = doc.select(SELECTOR_SELLER_PHONE).text();
        final String sellerName1 = doc.select(SELECTOR_SELLER_NAME_1).text();
        final String sellerName2 = doc.select(SELECTOR_SELLER_NAME_2).text();

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
                .sellerName(firstOrSecond(sellerName1, sellerName2))
                .build();
    }

    private Document getDocSafe(Connection connection) {
        try {
            return connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String firstOrSecond(final String value1, final String value2) {
        return !StringUtils.isEmpty(value1) ? value1 : value2;
    }

    private String fromPartialUrl(String partialUrl) {
        return "https:" + partialUrl;
    }

    @Async
    @Transactional(readOnly = true)
    public void sendNotifications() throws IOException {
        final List<PropertyEmailData> data = propertyRepo.findAllForNotification();
        emailSender.send(data);
    }
}
