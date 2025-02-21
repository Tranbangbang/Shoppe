package org.example.cy_shop.entity.chat;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.service.impl.order.VNPayService;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_chart_room")
public class ChatRoomEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_name")
    private String chatName;

    @ManyToMany(mappedBy = "chatRoom")  // mappedBy cho biết trường ở phía Account đã xác định quan hệ này
    private List<Account> account;

    @OneToMany(mappedBy = "chatRoom")
    private List<MessageEntity> message;

    @Override
    public String toString() {
        return "ChatRoomEntity{" +
                "id=" + id +
                ", chatName='" + chatName + '\'' +
                ", account=" + account +
                ", message=" + message +
                '}';
    }
}

//Account
