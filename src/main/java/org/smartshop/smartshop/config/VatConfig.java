package org.smartshop.smartshop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "app.vat")
@Getter
@Setter
public class VatConfig {

    private BigDecimal rate;
    private String description;

}