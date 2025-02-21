package org.example.cy_shop.service.impl;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.banner.PromotionalBannerRequest;
import org.example.cy_shop.dto.request.banner.UpdateBannerRequest;
import org.example.cy_shop.dto.request.banner.UpdatePromotionalBannerRequest;
import org.example.cy_shop.dto.response.banner.PromotionalBannerResponse;
import org.example.cy_shop.entity.Banner;
import org.example.cy_shop.entity.PromotionalBanner;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.PromotionalBannerMapper;
import org.example.cy_shop.repository.IPromotionalBannerRepository;
import org.example.cy_shop.service.IPromotionalBannerService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionalBannerServiceImpl implements IPromotionalBannerService {
    @Autowired
    private IPromotionalBannerRepository promotionalBannerRepository;
    @Autowired
    private PromotionalBannerMapper promotionalBannerMapper;
    @Override
    public ApiResponse<String> createBanner(PromotionalBannerRequest bannerRequest) {
        try{
            if (bannerRequest == null) {
                throw new RuntimeException("Banner request cannot be null.");
            }
            PromotionalBanner banner = promotionalBannerMapper.toEntity(bannerRequest);
            if (bannerRequest.getImage() != null) {
                String imageUrl = saveImageFile(bannerRequest.getImage());
                banner.setImageUrl(imageUrl);
            }
            banner.setCreatedAt(UtilsFunction.getVietNameTimeNow());
            banner.setUpdatedAt(UtilsFunction.getVietNameTimeNow());
            promotionalBannerRepository.save(banner);
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Banner created successfully.")
                    .build();
        }catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Error creating banner: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<PromotionalBannerResponse>> getAllBanners() {
        try {
            List<PromotionalBanner> banners = promotionalBannerRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
            List<PromotionalBannerResponse> bannerResponses = promotionalBannerMapper.toResponseList(banners);

            return ApiResponse.<List<PromotionalBannerResponse>>builder()
                    .code(200)
                    .data(bannerResponses)
                    .message("List of banners fetched successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<PromotionalBannerResponse>>builder()
                    .code(500)
                    .message("Error fetching banners: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<PromotionalBannerResponse> getBannerById(Long id) {
        try {
            Optional<PromotionalBanner> bannerOpt = promotionalBannerRepository.findById(id);

            if (bannerOpt.isEmpty()) {
                throw new AppException(ErrorCode.BANNER_NOT_FOUND);
            }
            PromotionalBannerResponse bannerResponse = promotionalBannerMapper.toResponse(bannerOpt.get());
            return ApiResponse.<PromotionalBannerResponse>builder()
                    .code(200)
                    .data(bannerResponse)
                    .message("Banner retrieved successfully.")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<PromotionalBannerResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PromotionalBannerResponse>builder()
                    .code(500)
                    .message("Error retrieving banner: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<String> updateBanner(Long id, UpdatePromotionalBannerRequest updateBannerRequest) {
        try {
            Optional<PromotionalBanner> bannerOpt = promotionalBannerRepository.findById(id);

            if (bannerOpt.isEmpty()) {
                throw new AppException(ErrorCode.BANNER_NOT_FOUND);
            }

            PromotionalBanner existingBanner = bannerOpt.get();
            PromotionalBanner updatedBanner = promotionalBannerMapper.toEntity(updateBannerRequest);
            existingBanner.setName(updatedBanner.getName());
            existingBanner.setImageUrl(updatedBanner.getImageUrl());
            existingBanner.setUpdatedAt(UtilsFunction.getVietNameTimeNow());

            promotionalBannerRepository.save(existingBanner);

            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Banner updated successfully.")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Error updating banner: " + e.getMessage())
                    .build();
        }
    }


    @Override
    public ApiResponse<String> deleteBanner(Long id) {
        try {
            Optional<PromotionalBanner> bannerOpt = promotionalBannerRepository.findById(id);

            if (bannerOpt.isEmpty()) {
                throw new AppException(ErrorCode.BANNER_NOT_FOUND);
            }
            promotionalBannerRepository.deleteById(id);
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("Banner deleted successfully.")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Error deleting banner: " + e.getMessage())
                    .build();
        }
    }
    private String saveImageFile(MultipartFile file) {
        String imageDirectory = Const.FOLDER_MEDIA_AVATAR;
        String imageName = UtilsFunction.saveMediaFile(file, imageDirectory);
        if (imageName != null) {
            return Const.PREFIX_URL_IMAGE_BANNER + imageName;
        } else {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}
