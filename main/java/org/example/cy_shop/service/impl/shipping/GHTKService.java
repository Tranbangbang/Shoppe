package org.example.cy_shop.service.impl.shipping;

import org.example.cy_shop.dto.request.ghtk.GHTKRequest;
import org.example.cy_shop.dto.response.ghtk.GHTKResponse;
import org.example.cy_shop.service.impl.order.VNPayService;
import org.example.cy_shop.service.shipping.IGHTKService;
import org.springframework.stereotype.Service;

@Service
public class GHTKService implements IGHTKService {

    @Override
    public GHTKResponse getFee(GHTKRequest request) {
        return null;
    }

    
}
