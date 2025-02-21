package org.example.cy_shop.service.impl;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.banner.BannerRequest;
import org.example.cy_shop.dto.request.banner.UpdateBannerRequest;
import org.example.cy_shop.dto.response.banner.BannerResponse;
import org.example.cy_shop.entity.Banner;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.BannerMapper;
import org.example.cy_shop.repository.IBannerRepository;
import org.example.cy_shop.service.IBannerService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class BannerService implements IBannerService {

    @Autowired
    private IBannerRepository bannerRepository;
    @Autowired
    private BannerMapper bannerMapper;
    @Override
    public ApiResponse<String> createBanner(BannerRequest bannerRequest) {
        try{
            if (bannerRequest == null) {
                throw new RuntimeException("Banner request cannot be null.");
            }

            Banner existingBanner = bannerRepository.findByOrderNumber(bannerRequest.getOrderNumber());
            if (existingBanner != null) {
                throw new AppException(ErrorCode.DUPLICATE_ORDER_NUMBER);
            }

            Banner banner = bannerMapper.toEntity(bannerRequest);

            if (bannerRequest.getImage() != null) {
                String imageUrl = saveImageFile(bannerRequest.getImage());
                banner.setImageUrl(imageUrl);
            }

            banner.setCreatedAt(UtilsFunction.getVietNameTimeNow());
            banner.setUpdatedAt(UtilsFunction.getVietNameTimeNow());
            bannerRepository.save(banner);

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
    public ApiResponse<List<Banner>> getAllBanners() {
        try {
            List<Banner> banners = bannerRepository.findAllByOrderByOrderNumberAsc();
            return ApiResponse.<List<Banner>>builder()
                    .code(200)
                    .data(banners)
                    .message("List of banners fetched successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<Banner>>builder()
                    .code(500)
                    .message("Error fetching banners: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<BannerResponse> getBannerById(Long id) {
        try{
            Banner banner = bannerRepository.getBannerById(id);
            if(banner ==null){
                throw new AppException(ErrorCode.BANNER_NOT_FOUND);
            }
            BannerResponse bannerResponse = bannerMapper.toResponse(banner);
            return ApiResponse.<BannerResponse>builder()
                    .code(200)
                    .data(bannerResponse)
                    .message("Banners fetched successfully.")
                    .build();
        }catch (AppException e) {
            return ApiResponse.<BannerResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<BannerResponse>builder()
                    .code(500)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<String> updateBanner(Long id, UpdateBannerRequest updateBannerRequest) {
       try{
           if (id == null || updateBannerRequest == null) {
               throw new AppException(ErrorCode.INVALID_INPUT);
           }
           Banner existingBanner = bannerRepository.findById(id)
                   .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));
           if (updateBannerRequest.getName() != null) {
               existingBanner.setName(updateBannerRequest.getName());
           }

           if (updateBannerRequest.getImage() != null && !updateBannerRequest.getImage().isEmpty()) {
               String imageUrl = updateBannerRequest.getImage();
               existingBanner.setImageUrl(imageUrl);
           }

           Integer currentOrderNumber = existingBanner.getOrderNumber();
           Integer newOrderNumber = updateBannerRequest.getNewOrderNumber();
           if (newOrderNumber != null && !currentOrderNumber.equals(newOrderNumber)) {
               swapOrderNumbers(currentOrderNumber, newOrderNumber);
               existingBanner.setOrderNumber(newOrderNumber);
           }

           existingBanner.setUpdatedAt(UtilsFunction.getVietNameTimeNow());
           bannerRepository.save(existingBanner);
           return ApiResponse.<String>builder()
                   .code(200)
                   .message("Banner updated successfully.")
                   .build();
       }catch (AppException e) {
           return ApiResponse.<String>builder()
                   .code(e.getErrorCode().getCode())
                   .message(e.getMessage())
                   .build();
       } catch (Exception e) {
           return ApiResponse.<String>builder()
                   .code(500)
                   .message("Error update banner: " + e.getMessage())
                   .build();
       }
    }

    private void swapOrderNumbers(Integer currentOrderNumber, Integer newOrderNumber) {
        if (!currentOrderNumber.equals(newOrderNumber)) {
            Banner targetBanner = bannerRepository.findByOrderNumber(newOrderNumber);
            if (targetBanner == null) {
                throw new AppException(ErrorCode.BANNER_NOT_FOUND);
            }
            Banner currentBanner = bannerRepository.findByOrderNumber(currentOrderNumber);
            if (currentBanner == null) {
                throw new AppException(ErrorCode.BANNER_NOT_FOUND);
            }
            targetBanner.setOrderNumber(currentOrderNumber);
            currentBanner.setOrderNumber(newOrderNumber);
            bannerRepository.save(targetBanner);
            bannerRepository.save(currentBanner);
        }
    }



    private void sortByOrderNumbers(Integer currentOrderNumber, Integer newOrderNumber) {
        if (currentOrderNumber < newOrderNumber) {
            List<Banner> bannersToSort = bannerRepository.findByOrderNumberBetween(currentOrderNumber + 1, newOrderNumber);
            for (Banner banner : bannersToSort) {
                banner.setOrderNumber(banner.getOrderNumber() - 1);
                bannerRepository.save(banner);
            }
        } else {
            List<Banner> bannersToSort = bannerRepository.findByOrderNumberBetween(newOrderNumber, currentOrderNumber - 1);
            for (Banner banner : bannersToSort) {
                banner.setOrderNumber(banner.getOrderNumber() + 1);
                bannerRepository.save(banner);
            }
        }
    }
    @Override
    public ApiResponse<String> deleteBanner(Long id) {
        try {
            if (id == null) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
            Banner bannerToDelete = bannerRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.BANNER_NOT_FOUND));

            Integer orderNumber = bannerToDelete.getOrderNumber();
            bannerRepository.delete(bannerToDelete);
            List<Banner> bannersToSort = bannerRepository.findByOrderNumberGreaterThan(orderNumber);
            for (Banner banner : bannersToSort) {
                banner.setOrderNumber(banner.getOrderNumber() - 1);
                bannerRepository.save(banner);
            }
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
