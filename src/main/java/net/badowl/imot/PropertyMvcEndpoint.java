package net.badowl.imot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PropertyMvcEndpoint {

    @Autowired
    private PropertyScraper scraper;

    @GetMapping(path = "/")
    public String scrape(final Model model) {
        final List<PropertyEmailData> data = scraper.getAllToDisplay();
        model.addAttribute("properties", data);
        return "properties";
    }
}
