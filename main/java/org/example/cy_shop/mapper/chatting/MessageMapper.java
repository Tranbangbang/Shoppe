package org.example.cy_shop.mapper.chatting;

import org.example.cy_shop.configuration.jwtConfig.JwtAuthenticationFilter;
import org.example.cy_shop.dto.request.chat.MessageRequest;
import org.example.cy_shop.dto.response.chat.MessageResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.example.cy_shop.entity.chat.MessageEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.chatting.IChatRoomRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.chatting.IChattingService;
import org.example.cy_shop.service.chatting.ISocketTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    IChatRoomRepository chatRoomRepository;
    @Autowired
    IAccountService accountService;
    @Autowired
    IChattingService chattingService;
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    ISocketTokenService socketTokenService;
    @Autowired
    IShopRepository shopRepository;

    public MessageResponse convertToResponse(MessageEntity entity){
        Long idRoomchat = null;
        if(entity != null && entity.getChatRoom() != null)
            idRoomchat = entity.getChatRoom().getId();

        MessageResponse messageResponse =  MessageResponse.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .typeMessage(entity.getTypeMessage())
                .idAccSend(entity.getAccount().getId())
                .idRoomchat(idRoomchat)

                .timeSend(entity.getCreateDate().toString())

                .build();
        return messageResponse;
    }

    //---convert ra entity nhưng chưa có account
    //---chưa có thông tin chat room
    public MessageEntity convertToEntity(MessageRequest request) {

        Account account = new Account();
        ChatRoomEntity chatRoomEntity = new ChatRoomEntity();

        if(request == null || request.getTypeMessage() == null)
            throw new AppException(ErrorCode.MESSAGE_REQUEST_INVALID);

        //***Tạo message để return
        MessageEntity result = MessageEntity.builder()
                .account(account)
                .chatRoom(chatRoomEntity)
                .typeMessage(request.getTypeMessage())
                .content(request.getContent())
                .build();

        return result;
    }

}

//AccountMapper