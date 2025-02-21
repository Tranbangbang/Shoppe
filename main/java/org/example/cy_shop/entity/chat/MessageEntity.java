package org.example.cy_shop.entity.chat;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.enums.chat.TypeMessageEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_message")
public class MessageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_message", nullable = false)
    private TypeMessageEnum typeMessage;

    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "id_chat_room")
    private ChatRoomEntity chatRoom;

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", typeMessage=" + typeMessage +
                ", account=" + account +
                ", chatRoom=" + chatRoom +
                '}';
    }
}
