package org.example.cy_shop.service.impl.chatting;

import jakarta.transaction.Transactional;
import org.example.cy_shop.configuration.jwtConfig.JwtAuthenticationFilter;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.chatting.ISocketTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SocketTokenService implements ISocketTokenService {
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    IAccountService accountService;
    @Autowired
    IAccountRepository accountRepository;

    @Value("${jwt.SECRET_ACCESS_TOKEN_KEY}")
    private String JWT_SECRET_ACCESS_TOKEN;

    @Override
    public String getToken(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) headerAccessor.getMessageHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

        String token = null;
        if (nativeHeaders != null && nativeHeaders.containsKey("Authorization")) {
            token = nativeHeaders.get("Authorization").get(0);

            // Cắt bỏ phần "Bearer " nếu nó tồn tại
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);  // Bỏ phần "Bearer "
            }
        }

        return token;
    }

    @Override
    public String getEmailFromToken(SimpMessageHeaderAccessor headerAccessor) {
        String token = getToken(headerAccessor);

        if(jwtAuthenticationFilter.validateToken(JWT_SECRET_ACCESS_TOKEN, token) == false) {
            System.out.println("Token khong hop le (socket token service)");
            throw new AppException(ErrorCode.USER_NOT_LOGIN);
        }

        try {
            System.out.println("email = " + jwtProvider.getEmailFromJWT(token));
            return jwtProvider.getEmailFromJWT(token);
        }catch (Exception e){
            System.out.println("Loi lay email (socket token service): " + e);
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public Account getAccountFromToken(SimpMessageHeaderAccessor headerAccessor) {
        String email = getEmailFromToken(headerAccessor);
        Account account = accountRepository.findByEmail(email).orElse(null);
        if(account == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        return account;
    }
}
