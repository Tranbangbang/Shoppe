package org.example.cy_shop.controller.client.feedback;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.controller.client.product.ClientProductController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.feedback.FeedbackRequest;
import org.example.cy_shop.dto.response.feedback.FeedbackRespone;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.mapper.feedback.FeedbackMapper;
import org.example.cy_shop.service.feedback.IFeedbackService;
import org.example.cy_shop.service.impl.feedback.FeedbackService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FEEDBACK")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/feedback")
public class ClientFeedbackController {
    @Autowired
    IFeedbackService feedbackService;

    //---lấy ra
    @Operation(
            summary = "get all"
    )
    @GetMapping("/find_all")
    public Page<FeedbackRespone> findAll(@RequestParam(name = "page",required = false)Integer page,
                                         @RequestParam(name = "limit", required = false)Integer limit,
                                         @RequestParam(name = "idPrd")Long idPrd,
                                         @RequestParam(name = "fileterBy", required = false) String filterBy){

        if(page == null)
            page = 1;
        if(limit == null)
            limit = 5;

        Pageable pageable = PageRequest.of(page-1, limit, Sort.by(Sort.Order.by("createDate")));
        if(filterBy != null){
            filterBy = filterBy.trim();
        }
        return feedbackService.findAll(idPrd, filterBy ,pageable);
    }

    //---feedback
    @Operation(
            summary = "feed back"
    )
    @PostMapping("/create_fb")
    public ApiResponse<Long> feed(@RequestBody List<FeedbackRequest> requests){
        return feedbackService.add(requests);
    }

    //---xóa
    @Operation(
            summary = "Xóa feed back"
    )
    @DeleteMapping("/delete/{idFeebback}")
    public ApiResponse<String> delete(@PathVariable("idFeebback") Long id){
        return feedbackService.delete(id);
    }
}

//FeedbackService
//FeedbackMapper
//ClientProductController