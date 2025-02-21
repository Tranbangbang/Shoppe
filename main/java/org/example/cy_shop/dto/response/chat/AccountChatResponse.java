package org.example.cy_shop.dto.response.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountChatResponse {
    private Long idAccount;
    private String userName;
    private String shopName;
    private String avatar;
}
