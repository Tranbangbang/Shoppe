package org.example.cy_shop.controller.seller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "SELLER_03. MANAGER_MEDIA(SELLER)")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/manager_media")
public class SellerMediaController {

    @PreAuthorize("hasAnyRole('ROLE_SHOP', 'ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/upload_an_image")
    public ApiResponse<String> addOptionImage(MultipartFile image) {
        try {
            if(image == null)
                return new ApiResponse<>(400, "File ảnh trống", null);

            var sourceMedia = Const.FOLDER_MEDIA_PRODUCT;
            var imageName = image.getOriginalFilename();


            if (UtilsFunction.isImage(imageName) == true) {
                String saveImageDetail = UtilsFunction.saveMediaFile(image, sourceMedia);
                saveImageDetail = Const.PREFIX_URL_MEDIA + saveImageDetail;
                return new ApiResponse<>(200, "Lưu ảnh option thành công", saveImageDetail);
            }
            return new ApiResponse<>(400, "Định dạng ảnh không đúng", null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Lỗi không xác định", e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_SHOP', 'ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/upload_many_image")
    public ApiResponse<List<String>> addManyImage(MultipartFile[] multipartFiles) {
        try {
            if(multipartFiles == null)
                return new ApiResponse<>(400, "File ảnh trống", null);

            List<String> result = new ArrayList<>();
            for (var it: multipartFiles){
                var sourceMedia = Const.FOLDER_MEDIA_PRODUCT;
                var imageName = it.getOriginalFilename();

                if (UtilsFunction.isImage(imageName) == true) {
                    String saveImageDetail = UtilsFunction.saveMediaFile(it, sourceMedia);
                    saveImageDetail = Const.PREFIX_URL_MEDIA + saveImageDetail;

                    result.add(saveImageDetail);
                }
                if(UtilsFunction.isImage(imageName)== false)
                    return new ApiResponse<>(400, "Không phải định dạng ảnh", null);
            }
            return new ApiResponse<>(200, "Lưu nhiều ảnh thành công", result);
        }catch (Exception e){
            System.out.println("Loi khi luu nhieu anh (seller media): " + e);
            return new ApiResponse<>(400, "Lỗi không xác định", null);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_SHOP', 'ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/upload_video")
    public ApiResponse<String> addVideo(MultipartFile video) {
        try {
            if(video == null)
                return new ApiResponse<>(400, "File video trống", null);

            var sourceMedia = Const.FOLDER_MEDIA_PRODUCT;
            var videoName = video.getOriginalFilename();


            if (UtilsFunction.isVideo(videoName) == true) {
                String saveVideo = UtilsFunction.saveMediaFile(video, sourceMedia);
                saveVideo = Const.PREFIX_URL_MEDIA + saveVideo;
                return new ApiResponse<>(200, "Lưu video thành công", saveVideo);
            }
            return new ApiResponse<>(400, "Định dạng video không đúng", null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Lỗi không xác định", e.getMessage());
        }
    }
}
