package org.example.cy_shop.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.enums.EnumDiscountType;
import org.example.cy_shop.enums.EnumReportStatus;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "voucher")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "voucher_code", nullable = false, unique = true)
    private String voucherCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private EnumDiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    //---số lượng voucher
    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "shop_id",  nullable = false)
    private Shop shop;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

//    @ManyToOne
//    @JoinColumn(name = "id_order_item")
//    private OrderDetailEntity orderDetail;


    public static String generateRandomCode(){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(12);  // Độ dài mã là 12 ký tự

        for (int i = 0; i < 12; i++) {
            int randomIndex = random.nextInt(chars.length());
            code.append(chars.charAt(randomIndex));
            if (i % 4 == 3 && i < 11) {  // Thêm dấu gạch ngang sau mỗi 4 ký tự
                code.append("-");
            }
        }

        return code.toString();
    }
}
