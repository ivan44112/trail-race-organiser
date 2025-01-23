package com.intellexi.racs.service.websocket;

import com.intellexi.racs.dto.EventMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

@Service
@Slf4j
public class WebSocketSubscriberService {

    @Value("${websocket.url}")
    private String websocketUrl;

    @Value("${websocket.topic}")
    private String subscriptionTopic;

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
                        return EventMessageResponse.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        EventMessageResponse messageEvent = (EventMessageResponse) payload;
                        log.info("Received message event: {}", messageEvent);
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
