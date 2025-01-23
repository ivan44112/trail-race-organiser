package com.intellexi.racs;

import com.intellexi.racs.service.websocket.WebSocketSubscriberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaceApplicationCommandService implements CommandLineRunner {
    private final WebSocketSubscriberService webSocketSubscriberService;

    public RaceApplicationCommandService(WebSocketSubscriberService webSocketSubscriberService) {
        this.webSocketSubscriberService = webSocketSubscriberService;
    }

    public static void main(String[] args) {
        SpringApplication.run(RaceApplicationCommandService.class, args);
    }

    @Override
    public void run(String... args) {
        webSocketSubscriberService.connectAndSubscribe();
    }
}
