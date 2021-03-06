package net.badowl.imot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyEndpoint {
    private final static Logger LOG = LoggerFactory.getLogger(PropertyScraper.class);

    @Autowired
    private PropertyScraper scraper;

    @PostMapping(path = "/scrape")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void scrape() throws Exception {
        scraper.scrapeAll().exceptionally(this::handleAsyncScrapeError);
    }

    @PostMapping(path = "/scrape-notify")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void scrapeAndNotify() throws Exception {
        scraper.scrapeAndNotify().exceptionally(this::handleAsyncScrapeError);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void errorHandler(Exception ex) {
        LOG.error("Global exception handler", ex);
    }

    private Void handleAsyncScrapeError(final Throwable th) {
        LOG.error("Async scraper error: ", th);
        return null;
    }
}
