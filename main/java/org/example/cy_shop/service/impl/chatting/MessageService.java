package org.example.cy_shop.service.impl.chatting;
import jakarta.transaction.Transactional;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.chat.MessageRequest;
import org.example.cy_shop.dto.request.notification.NotificationRequest;
import org.example.cy_shop.dto.response.chat.MessageResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Notification;
import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.example.cy_shop.entity.chat.MessageEntity;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.example.cy_shop.enums.EnumTypeStatus;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.NotificationMapper;
import org.example.cy_shop.mapper.chatting.MessageMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.INotificationRepository;
import org.example.cy_shop.repository.chatting.IChatRoomRepository;
import org.example.cy_shop.repository.chatting.IMessageRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.chatting.IChattingService;
import org.example.cy_shop.service.chatting.IMessageService;
import org.example.cy_shop.service.chatting.ISocketTokenService;
import org.example.cy_shop.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class MessageService implements IMessageService {
    @Autowired
    IMessageRepository messageRepository;
    @Autowired
    MessageMapper messageMapper;
    @Autowired
    ISocketTokenService socketTokenService;
    @Autowired
    IChattingService chattingService;
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    IAccountService accountService;
    @Autowired
    IChatRoomRepository chatRoomRepository;

    @Autowired
    NotificationMapper notificationMapper;
    @Autowired
    INotificationRepository notificationRepository;
    @Autowired
    NotificationService notificationService;

    @Override
    public ApiResponse<Page<MessageResponse>> findAll(Long idAccReceiver, Pageable pageable) {
        Account accountSend = accountService.getMyAccount();
        if (accountSend == null)
            throw new AppException(ErrorCode.USER_NOT_LOGIN);

        Account accReceiver = accountRepository.findById(idAccReceiver).orElse(null);
        if (accReceiver == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);

        ChatRoomEntity chatRoomEntity = chattingService.findChatRoom(accountSend.getId(), accReceiver.getId());
        if (chatRoomEntity == null)
            return new ApiResponse<>(200, "Phòng chat chưa tồn tại", null);

        Page<MessageEntity> mess = messageRepository.findAll(chatRoomEntity.getId(), pageable);
        Page<MessageResponse> result = mess.map(it -> messageMapper.convertToResponse(it));
        return new ApiResponse<>(200, "Danh sách tin nhắn", result);
    }

//    @Override
//    public ApiResponse<MessageResponse> save(MessageRequest request, Account account, ChatRoomEntity chatRoom) {
//        return null;
//    }

    @Override
    public ApiResponse<MessageResponse> save(MessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        // Lấy thông tin tài khoản từ token
        Account account = socketTokenService.getAccountFromToken(headerAccessor);

        // Lấy thông tin chat room và account
        ChatRoomEntity chatRoom = chattingService.findChatRoom(account.getId(), request.getIdAccReciver());

        // Nếu chưa có chat room thì tạo mới
        if (chatRoom == null) {
            var chatRoomAPI = chattingService.create(account.getId(), request.getIdAccReciver());
            if (chatRoomAPI.getCode() != 200)
                throw new AppException(ErrorCode.CANNOT_SAVE_CHAT);

            chatRoom = chatRoomRepository.findById(chatRoomAPI.getData()).orElse(null);
            if (chatRoom == null)
                throw new AppException(ErrorCode.CANNOT_SAVE_CHAT);
        }

        // Chuyển đổi MessageRequest thành MessageEntity
        MessageEntity messEn = messageMapper.convertToEntity(request);
        messEn.setAccount(account);
        messEn.setChatRoom(chatRoom);

        // Lưu tin nhắn
        MessageEntity messSave = messageRepository.save(messEn);
        if (messSave != null && messSave.getId() != null) {
            // Gửi thông báo đến người nhận
            Optional<Account> receiver = accountRepository.findById(request.getIdAccReciver());
            if (receiver.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            String notificationContent = "Bạn có tin nhắn mới từ " + account.getUsername();
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setAccId(receiver.get().getId());
            notificationRequest.setActorId(account.getId());
            notificationRequest.setUsername(account.getUsername());
            notificationRequest.setActorName(account.getUsername());
            notificationRequest.setType(EnumTypeStatus.denounce);
            notificationRequest.setContent(notificationContent);
            notificationRequest.setStatus(EnumNotificationStatus.unseen);
            Notification notification = notificationMapper.toEntity(notificationRequest);
            notificationRepository.save(notification);
            notificationService.senNotification(receiver.get().getId().toString(), notification);
            return new ApiResponse<>(200, "Lưu tin nhắn thành công", messageMapper.convertToResponse(messSave));
        }

        throw new AppException(ErrorCode.CANNOT_SAVE_MESSAGE);
    }
    }