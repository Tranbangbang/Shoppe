package org.example.cy_shop.service.impl.product;

import org.example.cy_shop.dto.request.product.add.MediaProductRequest;
import org.example.cy_shop.dto.response.product.MediaProductResponse;
import org.example.cy_shop.entity.product.MediaProductEntity;
import org.example.cy_shop.enums.TypeMediaEnum;
import org.example.cy_shop.mapper.product_mapper.MediaProductMapper;
import org.example.cy_shop.repository.product_repository.IMediaProductRepository;
import org.example.cy_shop.service.product.IMediaProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MediaProductService implements IMediaProductService {
    @Autowired
    IMediaProductRepository mediaProductRepository;
    @Autowired
    MediaProductMapper mediaProductMapper;
    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public List<MediaProductResponse> findAll() {
        try {
            return mediaProductRepository.findAll().stream()
                    .map(it -> mediaProductMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Lỗi không thể find all image: " + e);
            return null;
        }
    }

    @Override
    public List<MediaProductResponse> findByProductId(Long productId) {
        try {
            List<MediaProductEntity> mediaProductEntity = mediaProductRepository.findByProductId(productId);
            return mediaProductEntity.stream().map(it -> mediaProductMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Loi khi find all media by product id (media product service): " + e);
            return null;
        }
    }

    @Override
    public String getCoverImageProduct(Long idProduct) {
        try {
            List<MediaProductEntity> mediaProductEntities = mediaProductRepository.findByProductId(idProduct);
            for(var it: mediaProductEntities){
                if(it.getTypeMedia().equalsIgnoreCase(TypeMediaEnum.COVER_IMAGE.name()))
                    return it.getSourceMedia();
            }
            return null;
        }catch (Exception e){
            System.out.println("Khong tim thay anh cover của san pham (media product service): + e");
            return null;
        }
    }

    @Override
    public List<String> getSrcOfTypeMedia(List<MediaProductResponse> mediaProductResponseList, String typeSource) {
        try {
            List<String> result = new ArrayList<>();
            for (var it: mediaProductResponseList){
                if(it.getTypeMedia().equalsIgnoreCase(typeSource))
                    result.add(it.getSourceMedia());
            }
            return result;

        }catch (Exception e){
            System.out.println("Loi khi tim kiem src media product(media producr service): " + e);
            return  null;
        }
    }


    @Override
    public Boolean save(MediaProductRequest request) {
        try {
            MediaProductEntity mediaProductEntity = mediaProductRepository.save(mediaProductMapper.convertToEntity(request));

            return mediaProductEntity != null && mediaProductEntity.getId() > 0;
        }catch (Exception e){
            System.out.println("Khong the luu doi tuong media request (media product service): " + e);
            return false;
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            mediaProductRepository.deleteById(id);
            return true;
        }catch (Exception e){
            System.out.println("Loi khi xoa media product request (media producr service): " + e);
            return false;
        }
    }
}
