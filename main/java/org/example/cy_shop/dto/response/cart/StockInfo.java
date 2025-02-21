package org.example.cy_shop.dto.response.cart;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockInfo {
    private Long id;
    private Long quantity;
    private Double price;
    private String imageVariant;

    @Override
    public String toString() {
        return "StockInfo{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", price=" + price +
                ", imageVariant='" + imageVariant + '\'' +
                '}';
    }
}
