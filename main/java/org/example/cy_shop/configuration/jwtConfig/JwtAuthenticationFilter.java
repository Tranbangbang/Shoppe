package org.example.cy_shop.configuration.jwtConfig;


import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.cy_shop.configuration.AccountDetailsImpl;
import org.example.cy_shop.enums.EnumAccountVerifyStatus;
import org.example.cy_shop.enums.EnumTokenType;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AccountServiceImpl accountService;
    @Autowired
    IAccountRepository accountRepository;


    @Value("${jwt.SECRET_ACCESS_TOKEN_KEY}")
    private String JWT_SECRET_ACCESS_TOKEN;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = getJwtFromRequest(request);
            if (jwtToken != null && this.validateToken(JWT_SECRET_ACCESS_TOKEN, jwtToken)) {
                String username = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "username", jwtToken, String.class);
                String email = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "email", jwtToken, String.class);
                Long userId = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "userid", jwtToken, Long.class);
                int tokenType = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "token_type", jwtToken, Integer.class);
                int accountStatus = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "account_status", jwtToken, Integer.class);
                AccountDetailsImpl accountDetails = (AccountDetailsImpl) accountService.loadUserByUsername(email);
                if (tokenType == EnumTokenType.AccessToken.getValue() && accountStatus == EnumAccountVerifyStatus.Verified.getValue() &&
                        accountDetails != null) {
                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(accountDetails, null, accountDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        filterChain.doFilter(request, response);
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validateToken(String jwtTokenSecret, String token) {
        try {
            Jwts.parser().setSigningKey(jwtTokenSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}