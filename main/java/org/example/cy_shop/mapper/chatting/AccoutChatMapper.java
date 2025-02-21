package org.example.cy_shop.mapper.chatting;

import org.example.cy_shop.dto.response.chat.AccountChatResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.repository.IShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccoutChatMapper {
    @Autowired
    IShopRepository shopRepository;

    public AccountChatResponse convertToResponse(Account account){
        Shop shop = shopRepository.findById(account.getId()).orElse(null);

        String userName = null, avatar = null, shopName = null;
        if(shop != null)
            shopName = shop.getShopName();

        if(account != null && account.getUser() != null){
            userName = account.getUser().getUsername();
            avatar = account.getUser().getAvatar();
        }

        return AccountChatResponse.builder()
                .idAccount(account.getId())
                .userName(userName)
                .shopName(shopName)
                .avatar(avatar)
                .build();
    }
}
