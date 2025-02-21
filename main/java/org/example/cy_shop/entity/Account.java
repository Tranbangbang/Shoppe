package org.example.cy_shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.example.cy_shop.entity.chat.MessageEntity;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.example.cy_shop.entity.notification.NotificationEntity;
import org.example.cy_shop.entity.order.CartEntity;
import org.example.cy_shop.entity.order.OrderEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "accounts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "user_name")
    private String username;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 255)
    @Column(name = "password")
    private String password;

    @Size(max = 500)
    @Column(name = "forgot_password_token")
    private String forgotPasswordToken;

    @Size(max = 100)
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;

    @OneToMany(mappedBy = "reporter", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserVoucher> userVouchers;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToMany(mappedBy = "account")
    private List<NotificationEntity> notification;

    @OneToMany(mappedBy = "account")
    private List<CartEntity> cart;

    @OneToMany(mappedBy = "account")
    private List<OrderEntity> order;

    @ManyToMany
    @JoinTable(
            name = "tbl_account_chat_room",  // Tên bảng liên kết
            joinColumns = @JoinColumn(name = "id_account"), // Khóa ngoài tham chiếu đến Account
            inverseJoinColumns = @JoinColumn(name = "id_chat_room")
    )
    private List<ChatRoomEntity> chatRoom;

    @OneToMany(mappedBy = "account")
    private List<MessageEntity> message;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<AddressAccount> addresses;


}