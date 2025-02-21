package org.example.cy_shop.controller.order.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.cart.CartKeyRequest;
import org.example.cy_shop.dto.request.cart.CartKeysRequest;
import org.example.cy_shop.dto.request.cart.CartRequest;
import org.example.cy_shop.dto.request.cart.update.CartUpdateRequest;
import org.example.cy_shop.dto.response.cart.CartResponse;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.entity.order.CartEntity;
import org.example.cy_shop.enums.StatusCartEnum;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.service.order.ICartService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ORDER_02. CART")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/client_cart")
public class ClientCartController {
    @Autowired
    ICartService cartService;

    //---thêm vào giỏ hàng
    @Operation(
            summary = "Thêm vào giỏ hàng",
            description = "**Thêm sản phẩm vào giỏ hàng**"
    )
    @PostMapping("/add")
    public ApiResponse<Long> addToCart(@RequestBody CartRequest cartRequest){
        return cartService.add(cartRequest);
    }

    @Operation(
            summary = "Xóa giỏ hàng",
            description = "**Xóa item khỏi giỏ hàng**"
    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP', 'ROLE_USER')")
    @DeleteMapping("/delete_by_id")
    public ApiResponse<String> deleteById(@RequestBody CartKeyRequest key){
        return cartService.deleteById(key.getIdCart());
    }

    //---xóa nhiều
    @Operation(
            summary = "Xóa nhiều giỏ hàng",
            description = "**Xóa nhiều item khỏi giỏ hàng**"
    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP', 'ROLE_USER')")
    @DeleteMapping("/delete_many_by_id")
    public ApiResponse<String> deleteMany(@RequestBody CartKeysRequest keys){
        try {
            for (var it: keys.getKeys()){
                cartService.deleteById(it.getIdCart());
            }
            return new ApiResponse<>(200, "Xóa giỏ hàng thành công", null);
        }catch (Exception e){
            return new ApiResponse<>(400, "Xóa giỏ hàng thất bại", e.getMessage());
        }
    }

    //---lấy ra giỏ hàng
    @Operation(
            summary = "Lấy ra giỏ hàng (user)",
            description = "**Lấy ra danh sách giỏ hàng**"
    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP', 'ROLE_USER')")
    @GetMapping("/get_all_cart")
    public ApiResponse<Page<CartResponse>> getAll(@RequestParam(value = "page", required = false) Integer page,
                                                  @RequestParam(value = "limit", required = false ) Integer limit){
        if(page == null)
            page = 1;
        if(limit == null)
            limit = Const.RECORD_OF_TABLE_PAGE;
//        BaseEntity
        Pageable pageable = PageRequest.of(page-1, limit, Sort.by(Sort.Order.desc("createDate")));

        return cartService.findAllCustom(pageable);
    }


    //----update cart-------
    @Operation(
            summary = "Sửa giỏ hàng",
            description = "**Sửa thông tin giỏ hàng (khi user đổi thông tin số lượng và biến thể)**"
    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP', 'ROLE_USER')")
    @PutMapping("/update_cart")
    public ApiResponse<Long> updateCart(@RequestBody CartUpdateRequest request){
        return  cartService.updateCart(request);
    }


}

//StatusOrderEnum
