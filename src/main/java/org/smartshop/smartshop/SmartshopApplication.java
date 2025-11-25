package org.smartshop.smartshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmartshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartshopApplication.class, args);
    }

}
