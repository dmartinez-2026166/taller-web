package com.diegomartinez.taller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TallerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TallerWebApplication.class, args);
    }
}
