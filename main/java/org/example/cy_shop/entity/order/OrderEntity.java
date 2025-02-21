package org.example.cy_shop.entity.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.cy_shop.controller.client.feedback.ClientFeedbackController;
import org.example.cy_shop.controller.order.client.ClientCartController;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.enums.order.TypePaymenEnum;

import java.security.SecureRandom;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_order")
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;

    @Column(name = "shipping_address")
    private String shippingAdress;

    private String province;
    private String district;
    private String commune;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "last_price")
    private Double lastPrice;

    @Column(name = "discount_voucher")
    private Double discountVoucher;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_order")
    private StatusOrderEnum statusOrder;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_payment")
    private StatusPaymentEnum statusPayment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_payment")
    private TypePaymenEnum typePayment;

    @Column(name = "id_user_voucher")
    private Long idUserVoucher;

    @Column(name = "id_shop")
    private Long idShop;

    @OneToMany(mappedBy = "order")
    private List<OrderDetailEntity> orderDetail;

    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;

    @OneToMany(mappedBy = "order")
    private List<TrackingEntity> tracking;

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

//    @OneToMany(mappedBy = "order")
//    private List<FeedBackEntity> feedBack;

}

//ClientCartController
//ClientFeedbackController
