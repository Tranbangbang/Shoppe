package org.example.cy_shop.controller.client.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.chat.MessageRequest;
import org.example.cy_shop.dto.response.chat.ChatRoomResponse;
import org.example.cy_shop.dto.response.chat.MessageResponse;
import org.example.cy_shop.mapper.chatting.MessageMapper;
import org.example.cy_shop.service.chatting.IChattingService;
import org.example.cy_shop.service.chatting.IMessageService;
import org.example.cy_shop.service.chatting.ISocketTokenService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "CHATTING")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/chat")
public class ClientChattingController {
    @Autowired
    IChattingService chattingService;
    @Autowired
    IMessageService messageService;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    MessageMapper messageMapper;
    @Autowired
    ISocketTokenService socketTokenService;

    //---getAll message
    @Operation(
            summary = "Lấy ra đoạn chat theo room_id",
            description = "**Cần đăng nhập để lấy đoạn chat**"
    )
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SHOP', 'ROLE_ADMIN')")
    @GetMapping("/get_message")
    public ApiResponse<Page<MessageResponse>> getAll(@RequestParam(value = "page", required = false) Integer page,
                                                     @RequestParam(value = "limit", required = false) Integer limit,
                                                     @RequestParam(value = "idAccSend", required = true) Long idAccSend) {
        if (page == null)
            page = 1;
        if (limit == null)
            limit = 20;

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Order.desc("createDate")));
        return messageService.findAll(idAccSend, pageable);
    }

    @Operation(
            summary = "Lấy ra tất cả room chat"
    )
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SHOP', 'ROLE_ADMIN')")
    @GetMapping("/get_room_chat")
    public ApiResponse<List<ChatRoomResponse>> getAllRoomChat(){
        return chattingService.getAllMyChat();
    }

    //---chat real time
//    @MessageMapping("/chat")
//    public void sendMessage(MessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
//        var messResAPI = messageService.save(request, headerAccessor);
//
//        messagingTemplate.convertAndSend("/topic/messages", messResAPI);
//    }

    @MessageMapping("/chat")
    public void sendMessage(MessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        var messResAPI = messageService.save(request, headerAccessor);

        String idAccReceiver = request.getIdAccReciver().toString();
        messagingTemplate.convertAndSend("/user/" + idAccReceiver + "/queue/messages", messResAPI);
    }
}


