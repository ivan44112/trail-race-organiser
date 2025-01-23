package com.intellexi.raqs.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellexi.raqs.service.dto.EventMessageRequest;
import com.intellexi.raqs.service.dto.RaceApplicationDto;
import com.intellexi.raqs.service.dto.RaceDto;
import com.intellexi.raqs.service.race.RaceEntityService;
import com.intellexi.raqs.service.raceapplication.RaceApplicationEntityService;
import com.intellexi.raqs.utils.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;

@Service
@Slf4j
public class WebSocketSubscriberService {

    @Value("${websocket.url}")
    private String websocketUrl;

    @Value("${websocket.topic}")
    private String subscriptionTopic;

    private final RaceEntityService raceEntityService;
    private final RaceApplicationEntityService raceApplicationEntityService;

    public WebSocketSubscriberService(RaceEntityService raceEntityService, RaceApplicationEntityService raceApplicationEntityService) {
        this.raceEntityService = raceEntityService;
        this.raceApplicationEntityService = raceApplicationEntityService;
    }

    public void connectAndSubscribe() {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new org.springframework.messaging.converter.MappingJackson2MessageConverter());

        stompClient.connectAsync(websocketUrl, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("Connected to WebSocket server at {}", websocketUrl);

                session.subscribe(subscriptionTopic, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return EventMessageRequest.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        EventMessageRequest<?> messageEvent = (EventMessageRequest<?>) payload;
                        log.info("Received message event: {}", messageEvent);

                        Object eventPayload = messageEvent.getPayload();
                        log.info("Received payload type: {}", eventPayload.getClass().getName());

                        if (eventPayload instanceof Map<?, ?> payloadMap) {
                            if (messageEvent.getSubscriber() == Subscriber.RACE) {
                                RaceDto raceDto = new ObjectMapper().convertValue(payloadMap, RaceDto.class);
                                messageEvent = new EventMessageRequest<>(messageEvent.getOperationType(), raceDto, Subscriber.RACE);
                                raceEntityService.receiveAndProcessEvent(messageEvent);
                            } else {
                                RaceApplicationDto raceApplicationDto = new ObjectMapper().convertValue(payloadMap, RaceApplicationDto.class);
                                messageEvent = new EventMessageRequest<>(messageEvent.getOperationType(), raceApplicationDto, Subscriber.RACE_APPLICATION);
                                raceApplicationEntityService.receiveAndProcessEvent(messageEvent);
                            }
                        } else if (eventPayload instanceof String id) {
                            if (messageEvent.getSubscriber() == Subscriber.RACE) {
                                messageEvent = new EventMessageRequest<>(messageEvent.getOperationType(), id, Subscriber.RACE);
                                raceEntityService.receiveAndProcessEvent(messageEvent);
                            } else {
                                messageEvent = new EventMessageRequest<>(messageEvent.getOperationType(), id, Subscriber.RACE_APPLICATION);
                                raceApplicationEntityService.receiveAndProcessEvent(messageEvent);
                            }
                        } else {
                            log.info("Unexpected instance: {}", eventPayload);
                        }
                    }
                });
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                log.error("WebSocket exception: {}", exception.getMessage(), exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("WebSocket transport error: {}", exception.getMessage(), exception);
            }
        });

        log.info("WebSocket Client started and attempting to connect to {}", websocketUrl);
    }
}

