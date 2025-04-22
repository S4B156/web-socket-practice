package com.sia.web_socket_practice.controller;

import com.sia.web_socket_practice.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage chatMessage(@Payload ChatMessage chatMessage){
        return chatMessage;
    }
}
