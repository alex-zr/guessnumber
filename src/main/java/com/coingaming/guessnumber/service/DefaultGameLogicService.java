package com.coingaming.guessnumber.service;

import com.coingaming.guessnumber.messages.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DefaultGameLogicService implements GameLogicService {

    private ConcurrentHashMap<String, Integer> guesses = new ConcurrentHashMap<>();

    @Override
    public String checkResult(String sessionId, Integer randomValue) {
        Integer userNumber = guesses.get(sessionId);
        if (userNumber == null) {
            return ResultMessage.ENTER.getMessage();
        }

        guesses.remove(sessionId);
        if (randomValue.equals(userNumber)) {
            return ResultMessage.WIN.getMessage();
        }
        return ResultMessage.LOSE.getMessage();
    }

    @Override
    public void putUserValue(String sessionId, Integer guessedValue) {
        guesses.put(sessionId, guessedValue);
    }

}
