package org.example.cy_shop.mapper.order;

import org.example.cy_shop.dto.request.cart.OptionCartRequest;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.service.order.ICartService;
import org.example.cy_shop.service.product.IValueService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VariantMapper {
    @Autowired
    IValueService valueService;

    public List<VariantDTO> convertStringToDTO(String str){
        List<VariantDTO> optionList = new ArrayList<>();
        if(str != null)
            str = str.trim();
        String[] lines = str.split("\n");

        for(var it: lines){
            String[] paths = it.split(Const.REGEX_SEPARATOR);

            if(paths.length == 2) {
                VariantDTO tmp = new VariantDTO(paths[0], paths[1]);
                optionList.add(tmp);
            }
        }
        return optionList;
    }

    public String convertVariantToString(List<VariantDTO> option){
        String variant = "";

        if(option != null){
            for(var it: option){
                variant = variant + it.getOptionName() + Const.SEPARATOR + it.getOptionValue() + "\n";
            }
        }

        return variant;
    }

//    public boolean checkVariantExits(List<VariantDTO> variantDTOS, Long idProduct){
//        ICartService
//        String op1, vl1, op2, vl2;
//        if()
//        List<Long> ids = valueService.findIdStockByManyOptionAndValue();
//        return false;
//    }

    public static void main(String[] args) {
        List<Long> tmp = new ArrayList<>();
        tmp.add(1L);
        System.out.println(UtilsFunction.notNull(tmp));
    }
}
