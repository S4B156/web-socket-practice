package com.sia.web_socket_practice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {
    private String content;
    private String sender;
    private Status type;
}
