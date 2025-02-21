package org.example.cy_shop.service.impl.chatting;

import jakarta.transaction.Transactional;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.chat.ChatRoomResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.chatting.ChatRoomMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.chatting.IChatRoomRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.chatting.IChattingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ChattingService implements IChattingService {
    @Autowired
    IChatRoomRepository chatRoomRepository;
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    IAccountService accountService;
    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Override
    public ChatRoomEntity findChatRoom(Long idAcc1, Long idAcc2) {
        try {
            ChatRoomEntity res =  chatRoomRepository.findByAccID(idAcc1, idAcc2);
            return res;
        }catch (Exception e){
            System.out.println("Khong the tim thay chat room theo id acc(chat service): " + e);
            return null;
        }
    }

    @Override
    public ApiResponse<Long> create(Long idAcc1, Long idAcc2) {
        Account acc1 = accountRepository.findById(idAcc1).orElse(null);
        if(acc1 == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        Account acc2 = accountRepository.findById(idAcc2).orElse(null);
        if(acc2 == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);

        ChatRoomEntity chatResult = findChatRoom(idAcc1, idAcc2);

        if(chatResult != null)
            return new ApiResponse<>(200, "Phòng chat đã tồn tại", chatResult.getId());

        //---lưu chat room
        ChatRoomEntity chatRoomEntity = new ChatRoomEntity();
        List<Account> accountList = new ArrayList<>();
        accountList.add(acc1);
        accountList.add(acc2);
        chatRoomEntity.setAccount(accountList);

        ChatRoomEntity chatSave = chatRoomRepository.save(chatRoomEntity);

        if(chatSave == null)
            throw new AppException(ErrorCode.CANNOT_SAVE_CHAT);


        //---lưu account
        acc1.getChatRoom().add(chatSave);
        acc2.getChatRoom().add(chatSave);

        Account accSave1 = accountRepository.save(acc1);
        Account accSave2 = accountRepository.save(acc2);

        if(accSave1 == null || accSave2 == null)
            throw new AppException(ErrorCode.CANNOT_UPDATE_ACCOUNT);

        return new ApiResponse<>(200, "Tạo room chat thành công", chatSave.getId());
    }

    @Override
    public ApiResponse<List<ChatRoomResponse>> getAllMyChat() {
        Account account = accountService.getMyAccount();
        if(account == null)
            throw new AppException(ErrorCode.USER_NOT_LOGIN);

        List<ChatRoomEntity> chatList = chatRoomRepository.findAllMyChat(account.getId());

//        System.out.println("size = " + chatList.size());
        var listChat =  chatList.stream().map(it -> chatRoomMapper.convertToResponse(it)).toList();
        return new ApiResponse<>(200, "Danh sách chat room", listChat);
    }
}
