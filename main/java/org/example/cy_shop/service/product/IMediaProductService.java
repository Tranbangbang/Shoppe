package org.example.cy_shop.service.product;

import org.example.cy_shop.dto.request.product.add.MediaProductRequest;
import org.example.cy_shop.dto.response.product.MediaProductResponse;

import java.util.List;

public interface IMediaProductService {
    List<MediaProductResponse> findAll();
    List<MediaProductResponse>findByProductId(Long productId);

    //lấy srrc ảnh cover theo idProduct
    String getCoverImageProduct(Long idProduct);

    List<String> getSrcOfTypeMedia(List<MediaProductResponse> mediaProductResponse, String source);

    Boolean save(MediaProductRequest request);
    Boolean delete(Long id);
}
