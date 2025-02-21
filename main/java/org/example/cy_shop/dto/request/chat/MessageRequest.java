package org.example.cy_shop.dto.request.chat;

import lombok.*;
import org.example.cy_shop.entity.chat.MessageEntity;
import org.example.cy_shop.enums.chat.TypeMessageEnum;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {
    private String content;
    private MultipartFile media;
    private TypeMessageEnum typeMessage;
    private Long idAccReciver;
}
