package org.example.cy_shop.dto.response.chat;

import lombok.*;
import org.example.cy_shop.enums.chat.TypeMessageEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String content;
    private TypeMessageEnum typeMessage;
    private Long idAccSend;
    private Long idRoomchat;
//    private Long idReceiver;

    private String timeSend;
}
