package com.coingaming.guessnumber.handler;

import com.coingaming.guessnumber.messages.ResultMessage;
import com.coingaming.guessnumber.service.GameLogicService;
import com.coingaming.guessnumber.service.RandomGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class NumberWebSocketHandler implements WebSocketHandler {

    private RandomGeneratorService randomGeneratorService;
    private GameLogicService gameLogicService;

    public NumberWebSocketHandler(RandomGeneratorService randomGeneratorService, GameLogicService gameLogicService) {
        this.randomGeneratorService = randomGeneratorService;
        this.gameLogicService = gameLogicService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        return session.send(
                randomGeneratorService.getRandomFlux()
                        .map(randomValue -> gameLogicService.checkResult(session.getId(), randomValue))
                        .map(String::toString)
                        .map(session::textMessage)
        ).and(session.send(
                Mono.just(ResultMessage.START.getMessage())
                .map(session::textMessage))
        .and(session.receive()
                                .map(mess -> {
                                    String text = mess.getPayloadAsText();
                                    int guessedNumber = Integer.parseInt(text);
                                    gameLogicService.putUserValue(session.getId(), guessedNumber);
                                    return text;
                                })
                                .doOnNext(msg -> log.info(msg + ", " + session.getId()))
                ));
    }

}
