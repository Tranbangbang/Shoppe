package org.example.cy_shop.service.chatting;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.chat.ChatRoomResponse;
import org.example.cy_shop.dto.response.chat.MessageResponse;
import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.example.cy_shop.entity.chat.MessageEntity;

import java.util.List;

public interface IChattingService {
    ChatRoomEntity findChatRoom (Long idAcc1, Long idAcc2);

    ApiResponse<Long> create(Long acc1, Long acc2);

    ApiResponse<List<ChatRoomResponse>> getAllMyChat();

}
