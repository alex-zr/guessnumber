package com.coingaming.guessnumber.service;

public interface GameLogicService {

    String checkResult(String sessionId, Integer randomValue);

    void putUserValue(String sessionId, Integer guessedValue);
}
