package net.badowl.imot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ImotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImotApplication.class, args);
    }
}
