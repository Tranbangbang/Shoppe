package org.example.cy_shop.mapper.product_mapper;

import org.example.cy_shop.dto.request.product.CategoryRequest;
import org.example.cy_shop.dto.response.product.CategoryResponse;
import org.example.cy_shop.entity.product.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse convertToResponse(CategoryEntity categoryEntity){
        return CategoryResponse.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .level(categoryEntity.getLevel())
                .image(categoryEntity.getImage())
                .idParent(categoryEntity.getIdParent())

                .createBy(categoryEntity.getCreateBy())
                .modifierBy(categoryEntity.getModifierBy())
                .createDate(categoryEntity.getCreateDate())
                .modifierDate(categoryEntity.getModifierDate())
                .build();
    }

    public CategoryEntity convertToEntity(CategoryRequest categoryRequest){
        return CategoryEntity.builder()
                .id(categoryRequest.getId())
                .name(categoryRequest.getName())
                .level(categoryRequest.getLevel())
                .image(categoryRequest.getImage())
                .idParent(categoryRequest.getIdParent())

                .build();
    }
}
