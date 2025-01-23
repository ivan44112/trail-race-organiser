package com.intellexi.raqs;

import com.intellexi.raqs.service.websocket.WebSocketSubscriberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaceApplicationQueryService implements CommandLineRunner {
    private final WebSocketSubscriberService webSocketSubscriberService;

    public RaceApplicationQueryService(WebSocketSubscriberService webSocketSubscriberService) {
        this.webSocketSubscriberService = webSocketSubscriberService;
    }

    public static void main(String[] args) {
        SpringApplication.run(RaceApplicationQueryService.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            Thread.sleep(10000);
            webSocketSubscriberService.connectAndSubscribe();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
