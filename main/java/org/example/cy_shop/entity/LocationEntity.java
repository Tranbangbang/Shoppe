package org.example.cy_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.controller.order.client.ClientCartController;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_location_user")
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    ClientCartController
    private String province;
    private String district;
    private String ward;
    private String detailedAddress;

//    @OneToOne(mappedBy = "locations")
//    private User user;
}
