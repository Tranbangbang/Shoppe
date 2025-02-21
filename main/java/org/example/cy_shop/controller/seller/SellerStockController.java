package org.example.cy_shop.controller.seller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.add.ManyStockRequest;
import org.example.cy_shop.dto.request.product.OptionRequest;
import org.example.cy_shop.dto.response.product.stock_response.OneParentOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.OptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.ValueResponse;
import org.example.cy_shop.entity.product.ValueRequest;
import org.example.cy_shop.service.product.IOptionService;
import org.example.cy_shop.service.product.IStockService;
import org.example.cy_shop.service.product.IValueService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "SELLER_02. MANAGER_STOCK(SELLER)")
@RequestMapping(value = Const.PREFIX_VERSION + "/manager_stock")
public class SellerStockController {
    @Autowired
    IStockService stockService;
    @Autowired
    IOptionService optionService;
    @Autowired
    IValueService valueService;

//    @PostMapping("/add")
//    public ApiResponse<StockResponse> add(@RequestBody StockRequest stockRequest){
//        return stockService.save(stockRequest);
//    }

//    @PostMapping("/add")
//    public ApiResponse<String> add(@ModelAttribute ManyStockRequest manyStockRequest){
//        return stockService.saveMany(manyStockRequest, false);
//    }

    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ManyStockRequest manyStockRequest){
//        System.out.println("In the function");
        return stockService.saveMany(manyStockRequest, false);
    }

    @PostMapping("/update")
    public ApiResponse<String> update(@RequestBody ManyStockRequest manyStockRequest){
        return stockService.update(manyStockRequest);
    }

    @PostMapping("/add_option")
    public ApiResponse<OptionResponse> addOption(@RequestBody OptionRequest optionRequest){
        return optionService.save(optionRequest);
    }

    @PostMapping("/add_value")
    public ApiResponse<ValueResponse> addValue(@RequestBody ValueRequest valueRequest){
        return valueService.save(valueRequest);
    }

    @GetMapping("/list_option")
    List<OneParentOptionResponse>findAllOption(@RequestParam(value = "idProduct", required = false) Long idProduct){
        System.out.println("In the function ");
        return optionService.findListParentResponse(idProduct);
    }

}
