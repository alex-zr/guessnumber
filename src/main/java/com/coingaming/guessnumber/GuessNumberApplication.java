package com.coingaming.guessnumber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GuessNumberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuessNumberApplication.class, args);
    }
}
