package com.coingaming.guessnumber.service;

import com.coingaming.guessnumber.messages.ResultMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameLogicTest {

    public static final String sessionId = "22";
    public static final Integer guessedValue = 2;
    public static final Integer wrongValue = 3;

    private GameLogicService gameLogicService;

    @Before
    public void setUp() {
        gameLogicService = new DefaultGameLogicService();
    }

    @Test
    public void testWin() throws InterruptedException {
        gameLogicService.putUserValue(sessionId, guessedValue);
        assertEquals(ResultMessage.WIN.getMessage(), gameLogicService.checkResult(sessionId, guessedValue));
    }

    @Test
    public void testWinAndEnter() throws InterruptedException {
        gameLogicService.putUserValue(sessionId, guessedValue);
        assertEquals(ResultMessage.WIN.getMessage(), gameLogicService.checkResult(sessionId, guessedValue));
        assertEquals(ResultMessage.ENTER.getMessage(), gameLogicService.checkResult(sessionId, guessedValue));
    }

    @Test
    public void testLose() throws InterruptedException {
        gameLogicService.putUserValue(sessionId, guessedValue);
        assertEquals(ResultMessage.LOSE.getMessage(), gameLogicService.checkResult(sessionId, wrongValue));
    }

    @Test
    public void testEnter() throws InterruptedException {
        assertEquals(ResultMessage.ENTER.getMessage(), gameLogicService.checkResult(sessionId, wrongValue));
    }

}
