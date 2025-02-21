package org.example.cy_shop.enums.order;

public enum StatusOrderEnum {
    PENDING,           //đang xử lý - lúc mới tạo
    ACCEPT,
//    SHIPPING,          //đang giao
//    DELIVERED,         //đã giao thành công

    RECEIVED,          //nhận hàng - user
    RETURNED,          //hoàn trả - user
    NOT_RECEIVED,      //không nhận được hàng - user
    CANCELED           // user, admin
}
