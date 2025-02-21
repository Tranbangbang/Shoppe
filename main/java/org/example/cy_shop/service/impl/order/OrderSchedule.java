package org.example.cy_shop.service.impl.order;

import jakarta.transaction.Transactional;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.order.TrackingEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.user.TypeUserEnum;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.repository.order.ITrackingRepository;
import org.example.cy_shop.service.impl.feedback.FeedbackService;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.service.product.IStockService;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderSchedule {
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    ITrackingRepository trackingRepository;
    @Autowired
    IOrderService orderService;

    //---1 ngày    3600000
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void cancelExpried(){
        try {
            List<OrderEntity> order = orderRepository.findAllNotPaid();
            System.out.println("So luong can huy: " + order.size());
            for( var it: order){
//                var timeEx = it.getCreateDate().plusHours(1);
                var timeEx = it.getCreateDate().plusMinutes(1);
                if(UtilsFunction.getVietNameTimeNow().isAfter(timeEx)) {
                    it.setStatusOrder(StatusOrderEnum.CANCELED);
                    orderRepository.save(it);
//                    orderService.updateStock(it);
                    TrackingEntity trackingEntity = new TrackingEntity(null, StatusOrderEnum.CANCELED, "Chưa thanh toán",
                            TypeUserEnum.SHOP, it);
                    trackingRepository.save(trackingEntity);
//                    System.out.println("Cap nha thanh cong");

                    try {
                        orderService.updateStock(it);
                    }catch (Exception e){
                        System.out.println("Loi cap nhat stock(order schedule): " + e.getMessage());
                    }
                }
            }
        }catch (Exception e){
            System.out.println("Loi cap nhat order(schedeule): " + e.getMessage());
        }
    }
}

//FeedbackService