package org.example.cy_shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.cy_shop.entity.product.ProductEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Tự động sinh giá trị ID
    private Long id;

    @Size(max = 50)
    @Column(name = "username", nullable = false)
    private String username;

    @Size(max = 50)
    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "phone")
    private String phone;

    @Size(max = 255)
    @Column(name = "location")
    private String location;

//    @OneToOne
//    @JoinColumn(name = "id_location")
//    private LocationEntity locations;

    private LocalDate dob;

    @Size(max = 200)
    @Column(name = "avatar")
    private String avatar;

//    @Column(name = "is_active")
//    private Boolean isActive;

    @Column(name = "gender")
    private int gender;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
