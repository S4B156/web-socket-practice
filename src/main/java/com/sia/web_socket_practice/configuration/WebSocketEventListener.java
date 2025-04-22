package com.sia.web_socket_practice.configuration;

import com.sia.web_socket_practice.dto.ChatMessage;
import com.sia.web_socket_practice.dto.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@Slf4j
public class WebSocketEventListener {
    private final SimpMessageSendingOperations message;

    public WebSocketEventListener(SimpMessageSendingOperations message) {
        this.message = message;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent sessionConnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());
        Principal user = headerAccessor.getUser();
        if (user != null) {
            String username = user.getName();
            headerAccessor.getSessionAttributes().put("username", username);
            log.info("User connected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .sender(username)
                    .type(Status.JOIN)
                    .build();
            message.convertAndSend("/topic/public", chatMessage);
        } else {
            log.warn("No authenticated user found for WebSocket connection");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .sender(username)
                    .type(Status.LEAVE)
                    .build();
            message.convertAndSend("/topic/public", chatMessage);
        } else {
            log.warn("No username found in session attributes for disconnect event");
        }
    }
}
