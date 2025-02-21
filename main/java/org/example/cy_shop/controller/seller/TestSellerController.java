package org.example.cy_shop.controller.seller;

import org.example.cy_shop.controller.order.shop.ShopOrderController;
import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.repository.feedback.IFeedbackRepository;
import org.example.cy_shop.service.feedback.IFeedbackService;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.service.product.ICategoryService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.service.product.IStockService;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestSellerController {
    @Autowired
    IFeedbackService feedbackService;

    @GetMapping("/test_api")
    public String test(){
        return UtilsFunction.getVietNameTimeNow() + "";
    }

    @GetMapping("/test_feed")
    public CountFeedbackResponse testCate(@RequestParam(name = "id") Long id){
        return feedbackService.findCountByIdProduct(id);
    }
}

//ShopOrderController