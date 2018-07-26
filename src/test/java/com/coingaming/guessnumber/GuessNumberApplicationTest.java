package com.coingaming.guessnumber;

import com.coingaming.guessnumber.messages.ResultMessage;
import com.coingaming.guessnumber.service.RandomGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration
@Slf4j
public class GuessNumberApplicationTest {

    private final int NUM_CLIENTS = 2;
    private final int MAX_EVENTS = 5;
    private final int WAIT_TIME = 10;

    @Value("${waitTime.secs}")
    private int waitTime;

    public static final int RANDOM_VALUE = 4321;

    @Value("http://localhost:8080/ws/number")
    private String uriString;

    @MockBean
    private RandomGeneratorService randomGeneratorService;

    @Before
    public void setUp() {
        when(randomGeneratorService.getRandomFlux()).thenReturn(
                Flux.interval(Duration.ofSeconds(WAIT_TIME))
                        .map(pulse -> RANDOM_VALUE)
        );
    }

    @Test
    public void testWinAll() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(NUM_CLIENTS);
        Flux.merge(
                Flux.range(0, NUM_CLIENTS)
                        .subscribeOn(Schedulers.single())
                        .map(n -> getConnect(RANDOM_VALUE, ResultMessage.WIN.getMessage())
                                .doOnTerminate(latch::countDown))
                        .parallel()
        )
                .subscribe();

        latch.await(waitTime + 5, TimeUnit.SECONDS);
    }

    @Test
    public void testLoseAll() throws InterruptedException {
        final int wrongValue = RANDOM_VALUE + 1;
        final CountDownLatch latch = new CountDownLatch(NUM_CLIENTS);
        Flux.merge(
                Flux.range(0, NUM_CLIENTS)
                        .subscribeOn(Schedulers.single())
                        .map(n -> getConnect(wrongValue, ResultMessage.LOSE.getMessage())
                                .doOnTerminate(latch::countDown))
                        .parallel()
        )
                .subscribe();

        latch.await(waitTime + 5, TimeUnit.SECONDS);
    }

    private Mono<Void> getConnect(int sendNumber, String expectedResult) {
        URI uri = getUri();
        return new ReactorNettyWebSocketClient().execute(uri,
                session -> session
                        .send(
                                Flux.just(sendNumber)
                                        .map(Object::toString)
                                        .map(session::textMessage)
                        ).and(
                                session.receive()
                                        .map(WebSocketMessage::getPayloadAsText)
                                        .take(MAX_EVENTS)
                                        .doOnNext(txt -> {
                                            if (isNotPureResult(txt)) {
                                                assertEquals(expectedResult, txt);
                                            }
                                            log.info(session.getId() + ".IN: " + txt);
                                        })
                                        .doOnSubscribe(subscriber -> log.info(session.getId() + ".OPEN"))
                                        .doFinally(signalType -> {
                                            session.close();
                                            log.info(session.getId() + ".CLOSE");
                                        })
                                        .then())

        );
    }

    private boolean isNotPureResult(String txt) {
        return ResultMessage.ENTER.getMessage().equals(txt) && ResultMessage.START.getMessage().equals(txt);
    }

    private URI getUri() {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            log.error("Can't resolve uri", e);
        }
        return null;
    }

}
