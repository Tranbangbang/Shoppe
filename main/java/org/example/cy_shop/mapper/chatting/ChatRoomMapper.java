package org.example.cy_shop.mapper.chatting;

import org.example.cy_shop.dto.response.chat.AccountChatResponse;
import org.example.cy_shop.dto.response.chat.ChatRoomResponse;
import org.example.cy_shop.dto.response.chat.MessageResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.example.cy_shop.repository.chatting.IMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatRoomMapper {
    @Autowired
    AccoutChatMapper accoutChatMapper;
    @Autowired
    MessageMapper messageMapper;
    @Autowired
    IMessageRepository messageRepository;

    public ChatRoomResponse convertToResponse(ChatRoomEntity entity){

        List<AccountChatResponse> accounts = new ArrayList<>();
        MessageResponse messages = new MessageResponse();

        if(entity != null && entity.getAccount() != null){
            accounts = entity.getAccount().stream().map(it -> accoutChatMapper.convertToResponse(it)).toList();
        }

        if(entity != null){
            var mess = messageRepository.findLastMessageByRoom(entity.getId());
            if(mess != null && mess.size() != 0)
                messages = messageMapper.convertToResponse(mess.get(0));
        }

        return ChatRoomResponse.builder()
                .id(entity.getId())
                .accounts(accounts)
                .lastMessage(messages)
                .build();
    }
}
