package org.example.cy_shop.configuration.jwtConfig;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.example.cy_shop.configuration.AccountDetailsImpl;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.UserRole;
import org.example.cy_shop.enums.EnumAccountVerifyStatus;
import org.example.cy_shop.enums.EnumTokenType;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.utils.DateConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
    @Autowired
    private IAccountRepository accountRepository;

    @Value("${jwt.SECRET_ACCESS_TOKEN_KEY}")
    private String JWT_SECRET_ACCESS_TOKEN;

    @Value("${jwt.JWT_EXPIRATION_ACCESS_TOKEN}")
    private int JWT_EXPIRATION_ACCESS_TOKEN;

    @Value("${jwt.SECRET_REFRESH_TOKEN_KEY}")
    private String JWT_SECRET_REFRESH_TOKEN;

    @Value("${jwt.JWT_EXPIRATION_REFRESH_TOKEN}")
    private int JWT_EXPIRATION_REFRESH_TOKEN;

    @Getter
    @Value("${jwt.SECRET_FORGOT_PASSWORD_TOKEN_KEY}")
    private String JWT_SECRET_FORGOT_PASSWORD_TOKEN;

    @Getter
    @Value("${jwt.JWT_EXPIRATION_FORGOT_PASSWORD_TOKEN}")
    private int JWT_EXPIRATION_FORGOT_PASSWORD_TOKEN;

    public JwtProvider(IAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generateToken(AccountDetailsImpl customDetailService) {
        return this.generateTokenByUsername(customDetailService.getUsername());
    }

    public String generateTokenByUsername(String Username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_ACCESS_TOKEN);
        Account account = accountRepository.findByEmail(Username).get();

        Set<UserRole> userRoles = account.getUserRoles();
        List<String> roles = userRoles.stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(Long.toString(account.getId()))
                .claim("userid", account.getUser().getId())
                .claim("email", account.getEmail())
                .claim("username", account.getUser().getUsername())
                .claim("roles", roles)
                .claim("token_type", EnumTokenType.AccessToken.getValue())
                .claim("account_status", EnumAccountVerifyStatus.Verified.getValue())
                .setExpiration(expiryDate)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_ACCESS_TOKEN)
                .compact();
    }



    public String generateRefreshTokenByUsername(LocalDateTime iat, String Username) {

        Date iatDate = DateConversion.convertLocalDateTimeToDate(iat);

        Date expiryDate = new Date(iatDate.getTime() + JWT_EXPIRATION_REFRESH_TOKEN);

        Account account = accountRepository.findByEmail(Username).get();
        return Jwts.builder()
                .setSubject(Long.toString(account.getId()))
                .claim("userid", account.getUser().getId())
                .claim("email", account.getEmail())
                .claim("username", account.getUser().getUsername())
                .claim("token_type", EnumTokenType.RefreshToken.getValue())
                .claim("account_status", EnumAccountVerifyStatus.Verified.getValue())
                .setExpiration(expiryDate)
                .setIssuedAt(iatDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_REFRESH_TOKEN)
                .compact();
    }

    public String generateForgotPasswordTokenByUsername(String Username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_FORGOT_PASSWORD_TOKEN);
        Account account = accountRepository.findByUser_Username(Username).get();
        return Jwts.builder()
                .setSubject(Long.toString(account.getId()))
                .claim("userid", account.getUser().getId())
                .claim("email", account.getEmail())
                .claim("username", account.getUser().getUsername())
                .claim("token_type", EnumTokenType.ForgotPasswordToken.getValue())
                .claim("account_status", EnumAccountVerifyStatus.Verified.getValue())
                .setExpiration(expiryDate)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_FORGOT_PASSWORD_TOKEN)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET_ACCESS_TOKEN)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET_ACCESS_TOKEN)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }

    public <T> T getKeyByValueFromJWT(String jwtSecretKey, String key, String token, Class<T> clazz) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(key, clazz);
    }

    public String getUsernameContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public String getEmailContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            AccountDetailsImpl accountDetails = (AccountDetailsImpl) authentication.getPrincipal();
            return accountDetails.getEmail();
        }
        return null;
    }


    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET_ACCESS_TOKEN)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

}