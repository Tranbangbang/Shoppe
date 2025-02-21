package org.example.cy_shop.dto.response.shop.shop_static;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.cy_shop.dto.response.voucher.UserVoucherResponse;
import org.example.cy_shop.service.impl.SearchServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopInforResponse {
    private Long id;
    private String shopName;
    private String avatar;
    private LocalDateTime dateParticipation;
    private Long countProduct;
    private Long countFeedback;
    private Long cntSeller;
    private List<CategoryInfoResponse> category;

    List<UserVoucherResponse> userVoucher;
}

//SearchServiceImpl