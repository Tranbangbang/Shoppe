package org.example.cy_shop.service.impl.feedback;

import jakarta.transaction.Transactional;
import org.example.cy_shop.controller.seller.TestSellerController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.feedback.FeedbackRequest;
import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.dto.response.feedback.FeedbackRespone;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.UserRole;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.example.cy_shop.entity.feedback.MediaFeedBackEntity;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.enums.feedback.TypeMediaFeedbackEnum;
import org.example.cy_shop.enums.feedback.TypeSelectFbEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.feedback.FeedbackMapper;
import org.example.cy_shop.repository.feedback.IFeedbackRepository;
import org.example.cy_shop.repository.feedback.IMediaFeedbackRepository;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.feedback.IFeedbackService;
import org.example.cy_shop.specification.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class FeedbackService implements IFeedbackService {
    @Autowired
    IFeedbackRepository feedbackRepository;
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    FeedbackMapper feedbackMapper;
    @Autowired
    IAccountService accountService;
    @Autowired
    IMediaFeedbackRepository mediaFeedbackRepository;

    @Override
    public Page<FeedbackRespone> findAll(Long idPrd, String filterBy, Pageable pageable) {


        Double start = null;
        Boolean isText = null;
        Boolean isMedia = null;
        if(filterBy == null)
            filterBy = "";

        if(filterBy.equalsIgnoreCase(TypeSelectFbEnum.STAR_5.name()))
            start = 5.0;
        else if(filterBy.equalsIgnoreCase(TypeSelectFbEnum.STAR_4.name()))
            start = 4.0;
        else if(filterBy.equalsIgnoreCase(TypeSelectFbEnum.STAR_3.name()))
            start = 3.0;
        else if(filterBy.equalsIgnoreCase(TypeSelectFbEnum.STAR_2.name()))
            start = 2.0;
        else if(filterBy.equalsIgnoreCase(TypeSelectFbEnum.STAR_1.name()))
            start = 1.0;
        else if(filterBy.equalsIgnoreCase(TypeSelectFbEnum.TEXT.name()))
            isText = true;
        else if(filterBy.equalsIgnoreCase(TypeSelectFbEnum.MEDIA.name()))
            isMedia = true;

        System.out.println("is text = " + isText);
        Page<FeedBackEntity> feeds = feedbackRepository.findAll(idPrd, start, isText, isMedia, pageable);

        Page<FeedbackRespone> result =  feeds.map(it -> feedbackMapper.convertToResponse(it));
        return result;
    }

    @Override
    public ApiResponse<Long> add(List<FeedbackRequest> requests) {
        Account account = accountService.getMyAccount();
        if(account == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);

        //---nếu đã feedback cho đơn hàng => không thể feedback nữa
//        if(requests == null || requests.size() == 0)
//            throw new AppException(ErrorCode.FEEDBACK_REQUEST_NULL);
//
//        OrderEntity orderSearch = orderRepository.findByAccountAndIdOrderDetails(account.getId(), requests.get(0).getIdOrderDetail());
//        if(orderSearch != null)
//            throw new AppException(ErrorCode.YOU_WAS_FEEDBACK);

        for (var it: requests){
            it.simpleValidate();

            //---thiếu media
            FeedBackEntity entity = feedbackMapper.convertToFBEntity(it);
            FeedBackEntity feedSave = feedbackRepository.save(entity);

            if(feedSave == null)
                throw new AppException(ErrorCode.CANNOT_SAVE_FEEDBACK);


            if(it.getImages() != null){
                for(var img: it.getImages()) {
                    MediaFeedBackEntity mediaImg = new MediaFeedBackEntity(null, img, TypeMediaFeedbackEnum.IMAGE, feedSave);
                    mediaFeedbackRepository.save(mediaImg);
                }
            }

            if(it.getVideos() != null){
                MediaFeedBackEntity media = new MediaFeedBackEntity(null, it.getVideos(), TypeMediaFeedbackEnum.VIDEO, feedSave);
                mediaFeedbackRepository.save(media);
            }
        }
        return new ApiResponse<>(200, "Feed back thành công", null);
    }

//    @Override
//    public ApiResponse<Long> add(FeedbackRequest request) {
//        FeedBackEntity entity = feedbackMapper.convertToFBEntity(request);
//        FeedBackEntity feedSave = feedbackRepository.save(entity);
//
//        if (feedSave != null && feedSave.getId() != 0)
//            return new ApiResponse<>(200, "Feed back thành công", feedSave.getId());
//        throw new AppException(ErrorCode.CANNOT_SAVE_FEEDBACK);
//    }

    @Override
    public ApiResponse<String> delete(Long id) {
        FeedBackEntity entity = feedbackRepository.findById(id).orElse(null);
        if (entity == null)
            throw new AppException(ErrorCode.CANNOT_FIND_FEEDBACK);
        feedbackRepository.delete(entity);
        return new ApiResponse<>(200, "Xóa feed back", null);
    }

    @Override
    public Double getRatingAve(Long idPrd) {
        List<FeedBackEntity> feedBack = feedbackRepository.findByIdProduct(idPrd);

        Double sum = 0.0;
        Long cnt = 0L;

        if(feedBack == null || feedBack.isEmpty())
            return  null;

        for (var it : feedBack) {
            if(it.getRating() != null){
                sum += it.getRating();
                cnt++;
            }
        }

        if(cnt == 0)
            return null;

        return sum/cnt;
    }

    @Override
    public CountFeedbackResponse findCountByIdProduct(Long idPrd) {
        CountFeedbackResponse result = feedbackRepository.countFeedbacksByProductId(idPrd);
        return result;
    }

//    TestSellerController
//    ProductMapper
//    FeedbackMapper

}

//UserRole