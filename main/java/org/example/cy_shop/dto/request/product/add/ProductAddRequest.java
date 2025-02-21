package org.example.cy_shop.dto.request.product.add;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAddRequest {
    private Long id;
    private String productName;
    private String productDescription;

    private MultipartFile[] detailImage;
    private MultipartFile introVideo;
    private MultipartFile coverImage;

    private Long categoryId;
    private Long shopId = 1L;

    public String generateProductCode(String shopeName, String productName) {
        String res = "SH";

        if(shopeName == null)
            shopeName = "";
        if(productName == null)
            productName = "";

        shopeName = normalizeString(shopeName);
        productName = normalizeString(productName);

        String shopeStr = "";
        if (shopeName.length() >= 2) {
            shopeStr = shopeName.substring(0, 2).toUpperCase(Locale.ROOT);
        } else {
            shopeStr = (shopeName + "XX").substring(0, 2).toUpperCase(Locale.ROOT);
        }

         String productStr = "";
        if (productName.length() >= 2) {
            productStr = productName.substring(0, 2).toUpperCase(Locale.ROOT);
        } else {
            productStr = (productName + "XX").substring(0, 2).toUpperCase(Locale.ROOT);
        }



         String currentYear = String.valueOf(LocalDateTime.now().getYear());

         String randomNumber = generateRandomNumber(4);

         res += "-" + shopeStr +  productStr + "-" + currentYear + randomNumber;

        return res;
    }

    private String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));  // Chọn ngẫu nhiên từ 0 đến 9
        }
        return sb.toString();
    }

    private String normalizeString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
        return normalized.toLowerCase(Locale.ROOT);
    }

    public void simpleValidate(){
        if(this.productName != null)
            this.productName = this.productName.trim();
    }
}
