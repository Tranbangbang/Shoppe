package org.example.cy_shop.mapper.feedback;

import org.example.cy_shop.dto.request.feedback.FeedbackRequest;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.dto.response.feedback.FeedbackRespone;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.example.cy_shop.entity.feedback.MediaFeedBackEntity;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.feedback.TypeMediaFeedbackEnum;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.order.VariantMapper;
import org.example.cy_shop.repository.order.IOrderDetailRepository;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.feedback.IFeedbackService;
import org.example.cy_shop.service.feedback.IMediaFeedbackService;
import org.example.cy_shop.service.impl.feedback.FeedbackService;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FeedbackMapper {
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    IMediaFeedbackService mediaFeedbackService;
    @Autowired
    IAccountService accountService;
    @Autowired
    VariantMapper variantMapper;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IOrderDetailRepository orderDetailRepository;
    @Autowired
    IFeedbackService feedbackService;

    public FeedbackRespone convertToResponse(FeedBackEntity entity){
        //---media response
        List<MediaFeedBackEntity> mediaList = new ArrayList<>();
        if(entity != null && entity.getId() != null)
            mediaList = mediaFeedbackService.findByIdFeedback(entity.getId());

        List<String> images = new ArrayList<>();
        String video = null;

        if(mediaList != null){
            for(var it: mediaList){
                if(it.getTypeMedia().name().equalsIgnoreCase(TypeMediaFeedbackEnum.IMAGE.name())){
                    images.add(it.getMediaUrl());
                } else if (it.getTypeMedia().name().equalsIgnoreCase(TypeMediaFeedbackEnum.VIDEO.name())) {
                    video = it.getMediaUrl();
                }
            }
        }

        //---acc rsponse
        Account account = new Account();
        Long idAcc = null;
        String userName = null, avatar = null, shopName = null;


        if(entity != null && entity.getOrderDetail() != null && entity.getOrderDetail().getOrder() != null
                && entity.getOrderDetail().getOrder().getAccount() != null){
            account = entity.getOrderDetail().getOrder().getAccount();
            if(account != null && account.getUser() != null) {
                userName = account.getUser().getUsername();
                avatar = account.getUser().getAvatar();
                idAcc = account.getId();
            }
            if(account != null && account.getShop() != null){
                shopName = account.getShop().getShopName();
            }
        }

        return FeedbackRespone.builder()
                .id(entity.getId())
                .comment(entity.getComment())
                .rating(entity.getRating())
//                .idParent(entity.getIdParent())
                .images(images)
                .video(video)

                .idAcc(idAcc)
                .userName(userName)
                .avatar(avatar)
                .shopName(shopName)

                .variants(variantMapper.convertStringToDTO(entity.getOrderDetail().getVariant()))
                .timeFeed(entity.getCreateDate())

//                .countFb(countFb)
                .build();
    }


    public FeedBackEntity convertToFBEntity(FeedbackRequest request){
        Account account = null;
        account = accountService.getMyAccount();
        if(account == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);

        if(request == null || request.getIdOrderDetail() == null)
            throw new AppException(ErrorCode.FEEDBACK_REQUEST_INVALID);

        OrderDetailEntity orderDetail = orderDetailRepository.findById(request.getIdOrderDetail()).orElse(null);
        if(orderDetail == null)
            throw new AppException(ErrorCode.ORDER_DETAIL_NOT_FOUND);

        if(orderDetail.getOrder().getStatusOrder().name().equalsIgnoreCase(StatusOrderEnum.RECEIVED.name()) == false){
            throw new AppException(ErrorCode.STATUS_ORDER_NOT_RRECEIVE);
        }

        if(request.getRating() == null || request.getRating() < 1 || request.getRating() > 5)
            throw new AppException(ErrorCode.FEEDBACK_INVALID);

        return FeedBackEntity.builder()
                .comment(request.getComment())
                .rating(request.getRating())
                .orderDetail(orderDetail)
                .build();
    }

//    StatusOrderEnum
}

//FeedbackService
//FeedbackMapper