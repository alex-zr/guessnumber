package com.coingaming.guessnumber.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Service
@Slf4j
public class DefaultRandomGeneratorService implements RandomGeneratorService {

    @Value("${waitTime.secs}")
    private int waitTime;

    @Value("${number.max.value}")
    private int numberMaxValue;

    private Random rnd = new Random();

    @Override
    public Flux<Integer> getRandomFlux() {
        return Flux.interval(Duration.ofSeconds(waitTime))
                .map(pulse -> rnd.nextInt(numberMaxValue) + 1);
    }
}
