package org.example.cy_shop.dto.response.chat;

import lombok.*;
import org.example.cy_shop.enums.chat.TypeMessageEnum;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomResponse {
    private Long id;
    private List<AccountChatResponse> accounts;

//    private Page<MessageResponse> messages;
    private MessageResponse lastMessage;
}
