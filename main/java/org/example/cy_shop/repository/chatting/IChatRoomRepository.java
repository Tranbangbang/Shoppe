package org.example.cy_shop.repository.chatting;

import org.example.cy_shop.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query("SELECT cr FROM ChatRoomEntity cr " +
            "JOIN cr.account a1 " +
            "JOIN cr.account a2 " +
            "WHERE a1.id = :acc1 AND a2.id = :acc2")
    ChatRoomEntity findByAccID(@Param("acc1") Long acc1,
                                         @Param("acc2") Long acc2);

    @Query("select ch from ChatRoomEntity ch where :id in (select acc.id from ch.account acc)")
    List<ChatRoomEntity> findAllMyChat(@Param("id") Long idAcc);
}
