package net.badowl.imot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.util.Optional;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ImotApplication {

    @Autowired
    private PropertyScraper scraper;

    public static void main(String[] args) {
        SpringApplication.run(ImotApplication.class, args);
    }

    @Bean
    public DataSource dataSource() {
        final String databaseUrl = Optional.ofNullable(System.getenv("JDBC_DATABASE_URL"))
                .orElse("jdbc:postgresql://localhost:5432/imot");
        return DataSourceBuilder.create()
                .url(databaseUrl)
                .build();
    }

    @Scheduled(cron = "${scraper.cron}")
    public void scheduleFixedDelayTask() throws Exception {
        scraper.scrapeAll();
        scraper.sendNotifications();
    }

}
