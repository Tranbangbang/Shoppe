package org.example.cy_shop.service.product;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.CategoryRequest;
import org.example.cy_shop.dto.response.product.CategoryResponse;
import org.example.cy_shop.entity.product.CategoryEntity;

import java.util.List;

public interface ICategoryService {
    List<CategoryResponse> findAll();
    List<CategoryResponse> findAllParent();
    List<CategoryResponse>findAllChildren(Long idParent);
    CategoryResponse findById(Long id);
    CategoryResponse findParentBySubId(Long id);

    ApiResponse<CategoryEntity>save(CategoryRequest categoryRequest);
    ApiResponse<CategoryEntity>edit(CategoryRequest categoryRequest);

    Boolean testCategory(Long id);
}
