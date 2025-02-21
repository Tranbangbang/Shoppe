package org.example.cy_shop.entity.feedback;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.enums.feedback.TypeMediaFeedbackEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_media_feed_back")
public class MediaFeedBackEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "media_url")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_media")
    private TypeMediaFeedbackEnum typeMedia;

    @ManyToOne
    @JoinColumn(name = "id_feedback")
    private FeedBackEntity feedBack;
}
