package com.example.seesaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SeesawApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeesawApplication.class, args);
    }

}
