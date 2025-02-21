package org.example.cy_shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address_account")
public class AddressAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 15)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Size(max = 50)
    @Column(name = "province")
    private String province;

    @Size(max = 50)
    @Column(name = "district")
    private String district;

    @Size(max = 50)
    @Column(name = "ward")
    private String ward;

    @Size(max = 150)
    @Column(name = "detailed_address")
    private String detailedAddress;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
