package com.coingaming.guessnumber.service;

import reactor.core.publisher.Flux;

public interface RandomGeneratorService {

    Flux<Integer> getRandomFlux();
}
