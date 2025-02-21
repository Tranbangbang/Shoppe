package org.example.cy_shop.service.shipping;

import org.example.cy_shop.dto.request.ghtk.GHTKRequest;
import org.example.cy_shop.dto.response.ghtk.GHTKResponse;

public interface IGHTKService {
    GHTKResponse getFee(GHTKRequest request);
}
