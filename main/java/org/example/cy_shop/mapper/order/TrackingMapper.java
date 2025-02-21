package org.example.cy_shop.mapper.order;

import org.example.cy_shop.dto.request.order.tracking.TrackingRequest;
import org.example.cy_shop.dto.response.order.TrackingResponse;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.order.TrackingEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackingMapper {
    @Autowired
    IOrderRepository orderRepository;

    public TrackingResponse convertToResponse(TrackingResponse response){
        return TrackingResponse.builder()
                .id(response.getId())
                .status(response.getStatus())
                .note(response.getNote())
                .userUpdateStatus(response.getUserUpdateStatus())
                .idOrder(response.getIdOrder())
                .build();
    }

    public TrackingEntity convertToEntity(TrackingRequest request){
        OrderEntity orderEntity = new OrderEntity();
        if(request == null || request.getIdOrder() == null)
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        orderEntity = orderRepository.findById(request.getIdOrder()).orElse(null);
        if(orderEntity == null)
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);

        return TrackingEntity.builder()
                .status(request.getStatus())
                .note(request.getNote())
                .userUpdateStatus(request.getUserUpdateStatus())
                .order(orderEntity)
                .build();
    }
}
