package org.example.cy_shop.service.order;

import java.io.UnsupportedEncodingException;

public interface IVNPayService {
    String createOrder(int total, String orderInfo, String urlReturn) throws UnsupportedEncodingException;

}
