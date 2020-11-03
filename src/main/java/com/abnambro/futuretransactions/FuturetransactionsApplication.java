package com.abnambro.futuretransactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FuturetransactionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuturetransactionsApplication.class, args);
    }

}
