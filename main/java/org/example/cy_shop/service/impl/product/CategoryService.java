package org.example.cy_shop.service.impl.product;

import org.example.cy_shop.controller.seller.TestSellerController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.CategoryRequest;
import org.example.cy_shop.dto.response.product.CategoryResponse;
import org.example.cy_shop.entity.product.CategoryEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.product_mapper.CategoryMapper;
import org.example.cy_shop.repository.product_repository.ICategoryRepository;
import org.example.cy_shop.service.product.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    ICategoryRepository categoryRepository;
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> findAll() {
        try {
            return categoryRepository.findAll().stream().map(it -> categoryMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Lỗi khi tìm kiếm category (category service): " + e);
            return null;
        }
    }

    @Override
    public List<CategoryResponse> findAllParent() {
        try {
            return  categoryRepository.findByLevel(1).stream().map(it -> categoryMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public List<CategoryResponse> findAllChildren(Long idParent) {
        try {
            return categoryRepository.findByIdParent(idParent).stream().map(it -> categoryMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Loi tim kiem category con(category service)");
            return null;
        }
    }

    @Override
    public CategoryResponse findById(Long id) {
        try {
            CategoryEntity categoryEntity =  categoryRepository.findById(id).orElse(null);
            if(categoryEntity == null)
                return null;
            return categoryMapper.convertToResponse(categoryEntity);
        }catch (Exception e){
            System.out.println("Loi tim cate theo id(category service): " + e);
            return null;
        }
    }

    @Override
    public CategoryResponse findParentBySubId(Long id) {
        try {
            CategoryEntity categoryEntity =  categoryRepository.findById(id).orElse(null);
            if(categoryEntity == null)
                return null;

            CategoryEntity parent = categoryRepository.findById(categoryEntity.getIdParent()).orElse(null);

            if(parent == null)
                return null;

            return categoryMapper.convertToResponse(parent);
        }catch (Exception e){
            System.out.println("Loi tim cate theo id(category service): " + e);
            return null;
        }
    }

    @Override
    public ApiResponse<CategoryEntity> save(CategoryRequest categoryRequest) {
        categoryRequest.preProcessing();

//        CategoryEntity categoryEntityFind = categoryRepository.findByName(categoryRequest.getName());
//            if(categoryEntityFind != null)
//                throw new AppException(ErrorCode.CATEGORY_EXITS);

        CategoryEntity categoryEntity = categoryRepository.save(categoryMapper.convertToEntity(categoryRequest));
            return ApiResponse.<CategoryEntity>builder()
                    .code(200)
                    .message("Thêm sản phẩm thành công")
                    .data(categoryEntity)
                    .build();
    }

    @Override
    public ApiResponse<CategoryEntity> edit(CategoryRequest categoryRequest) {
        categoryRequest.preProcessing();

        CategoryEntity categoryEntity = categoryRepository.findById(categoryRequest.getId()).orElse(null);
        if(categoryEntity == null)
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);

        CategoryEntity categoryEntityFind = categoryRepository.findByName(categoryRequest.getName());
        if(categoryEntityFind != null)
            throw new AppException(ErrorCode.CATEGORY_EXITS);

        categoryEntity.setName(categoryRequest.getName());
        categoryRepository.save(categoryEntity);
        return ApiResponse.<CategoryEntity>builder()
                .code(200)
                .message("Cập nhật category thành công")
                .data(categoryEntity)
                .build();
    }

    @Override
    public Boolean testCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id).orElse(null);
        if(category == null)
            return false;

        List<Long> ids = new ArrayList<>();
        if(category.getLevel() == 2)
            ids.add(category.getId());

        if(category.getLevel() == 1){
            List<CategoryEntity> categoryList = categoryRepository.findByIdParent(category.getId());
            for(var el: categoryList){
                ids.add(el.getId());
            }
        }

        System.out.println("size: " + ids.size());
        for(var it: ids){
            System.out.println(it + ", ");
        }
        return true;
    }

}

//TestSellerController
