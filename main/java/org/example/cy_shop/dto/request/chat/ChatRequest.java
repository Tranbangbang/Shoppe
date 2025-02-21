package org.example.cy_shop.dto.request.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {
    private Long idAccount;
    private String message;
    private Long idChatRoom;
}
