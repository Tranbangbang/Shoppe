package org.example.cy_shop.repository.chatting;

import org.example.cy_shop.entity.chat.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("select me from MessageEntity me where me.chatRoom.id = :idChart")
    Page<MessageEntity> findAll(@Param("idChart")Long idChatRoom, Pageable pageable);

    @Query("select me from MessageEntity  me where me.chatRoom.id = :idRoom order by  me.createDate desc")
    List<MessageEntity> findLastMessageByRoom(@Param("idRoom") Long idRoom);
}
