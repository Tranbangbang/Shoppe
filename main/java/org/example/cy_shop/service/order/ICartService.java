package org.example.cy_shop.service.order;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.cart.CartRequest;
import org.example.cy_shop.dto.request.cart.OptionCartRequest;
import org.example.cy_shop.dto.request.cart.update.CartUpdateRequest;
import org.example.cy_shop.dto.response.cart.CartResponse;
import org.example.cy_shop.service.product.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICartService {
    //1. ----tìm kiếm
    ApiResponse<Page<CartResponse>> findAllCustom(Pageable pageable);

    String getImage(List<OptionCartRequest> option, Long idProduct);

    Long getQuantityByManyOption(String op1, String op2, String vl1, String vl2, Long idPrd);


    //---thêm
    ApiResponse<Long> add(CartRequest cartRequest);

    //3. -----chỉnh sửa
    //--3.1. Chỉnh sửa khi sản phẩm thay đổi stock
    ApiResponse<String> updateCartByProduct(Long idProduct);

    //---3.2.chỉnh sửa khi sản phẩm thay đổi biến thể và số lượng
    ApiResponse<Long> updateCart(CartUpdateRequest request);

    //----xóa
    ApiResponse<String> deleteById(Long id);

    //---convert
    List<OptionCartRequest>convertStringToOption(String str);
    String convertOptionToString(List<OptionCartRequest> option);

}

//IProductService