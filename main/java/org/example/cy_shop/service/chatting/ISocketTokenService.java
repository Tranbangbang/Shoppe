package org.example.cy_shop.service.chatting;

import org.example.cy_shop.entity.Account;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface ISocketTokenService {
    String getToken(SimpMessageHeaderAccessor headerAccessor);
    String getEmailFromToken(SimpMessageHeaderAccessor headerAccessor);
    Account getAccountFromToken(SimpMessageHeaderAccessor headerAccessor);
}
