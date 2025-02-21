package org.example.cy_shop.service.chatting;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.chat.MessageRequest;
import org.example.cy_shop.dto.response.chat.MessageResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface IMessageService {
    ApiResponse<Page<MessageResponse>> findAll(Long idAccReceiver, Pageable pageable);
    ApiResponse<MessageResponse> save(MessageRequest request, SimpMessageHeaderAccessor headerAccessor);
}
