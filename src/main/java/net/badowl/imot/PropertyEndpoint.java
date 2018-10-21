package net.badowl.imot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyEndpoint {

    @Autowired
    private PropertyScraper scraper;

    @PostMapping(path = "/scrape")
    public void scrape() throws Exception {
        scraper.scrapeAll();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void errorHandler() {
    }
}
