package com.lbc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    protected Application() {
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
