package org.example.cy_shop.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import org.example.cy_shop.controller.order.shop.ShopOrderController;
import org.example.cy_shop.controller.search.SearchController;
import org.example.cy_shop.dto.response.order.StaticOrderResponse;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.service.impl.SearchServiceImpl;

@Getter
@Setter
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
}

//SearchServiceImpl
//OrderDetailEntity
//ShopOrderController