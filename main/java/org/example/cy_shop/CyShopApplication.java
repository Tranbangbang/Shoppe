package org.example.cy_shop;

import org.example.cy_shop.controller.search.SearchController;
import org.example.cy_shop.dto.request.search.SearchProduct;
import org.example.cy_shop.service.impl.feedback.FeedbackService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CyShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyShopApplication.class, args);
    }

}

//SearchController
//FeedbackService
