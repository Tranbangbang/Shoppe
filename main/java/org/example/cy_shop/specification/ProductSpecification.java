package org.example.cy_shop.specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.controller.client.feedback.ClientFeedbackController;
import org.example.cy_shop.dto.response.product.CategoryResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.entity.SearchHistory;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.entity.product.CategoryEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.entity.product.ProductView;
import org.example.cy_shop.entity.product.StockEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.ISearchHistoryRepository;
import org.example.cy_shop.repository.product_repository.ICategoryRepository;
import org.example.cy_shop.service.ISearchHistoryService;
import org.example.cy_shop.service.impl.SearchServiceImpl;
import org.example.cy_shop.service.impl.feedback.FeedbackService;
import org.example.cy_shop.service.impl.product.CategoryService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.service.product.ICategoryService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProductSpecification {
    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IAccountRepository accountRepository;

    //comment in here

    public Specification<ProductEntity> hasKeySearch(String keySearch) {
        return (root, query, criteriaBuilder) -> {
            if (keySearch == null || keySearch.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // Không có điều kiện tìm kiếm, trả về tất cả
            }

            String searchPattern = "%" + keySearch.trim() + "%";
            return criteriaBuilder.or(
//                    criteriaBuilder.like(root.get("productDescription"), searchPattern),
                    criteriaBuilder.like(root.get("productName"), searchPattern),
                    criteriaBuilder.equal(root.get("productCode"), keySearch.trim()),
                    criteriaBuilder.like(root.join("shop").get("shopName"), searchPattern)
            );
        };
    }


    public Specification<ProductEntity> hasShop(Long shopId) {
        return (root, query, criteriaBuilder) -> {
            if (shopId == null)
                return null;
            return criteriaBuilder.equal(root.get("shop").get("id"), shopId);
        };
    }

    public Specification<ProductEntity> hasCategoryName(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null)
                return null;
//            String searchPattern = "%" + categoryName + "%";
            String searchPattern = categoryName;

            Subquery<String> subquery = query.subquery(String.class);
            Root<CategoryEntity> categoryRoot = subquery.from(CategoryEntity.class);
            subquery.select(categoryRoot.get("name"));
            subquery.where(
                    criteriaBuilder.equal(categoryRoot.get("id"), root.get("category").get("idParent"))
            );

            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("category").get("name"), searchPattern),
                    criteriaBuilder.equal(subquery, searchPattern)
            );
        };
    }

    public Specification<ProductEntity> hasSubCategoryId(Long categoryId) {

        return (root, query, criteriaBuilder) -> {
            if (categoryId == null)
                return null;

//            return root.get("category").get("id"), categoryId;
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public Specification<ProductEntity> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null)
                return null;

            CategoryEntity category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null)
                return null;

            List<Long> ids = new ArrayList<>();
            if (category.getLevel() == 2)
                ids.add(category.getId());

            if (category.getLevel() == 1) {
                List<CategoryEntity> categoryList = categoryRepository.findByIdParent(category.getId());
                for (var el : categoryList) {
                    ids.add(el.getId());
                }
            }

            if (ids.size() == 0)
                return null;

            return criteriaBuilder.in(root.get("category").get("id")).value(ids);
        };
    }

    public Specification<ProductEntity> hasNewProduct(Boolean newPrd) {
        return (root, query, criteriaBuilder) -> {
            //---nếu không có điều kiện đó => luôn đúng
            if (newPrd == false)
                return null;
            LocalDate today = UtilsFunction.getVietNameTimeNow().toLocalDate();
            return criteriaBuilder.equal(
                    criteriaBuilder.function("DATE", LocalDate.class, root.get("createDate")),
                    today
            );
        };
    }

    public Specification<ProductEntity> sortByPopular(Boolean bestSeller) {
        return (root, query, criteriaBuilder) -> {
            if (bestSeller == false)
                return null;

            Subquery<Long> subSeller = query.subquery(Long.class);
            Root<OrderDetailEntity> orderDetails = subSeller.from(OrderDetailEntity.class);

            //---số lượng order ("RECEIVED"
            subSeller.select(criteriaBuilder.count(orderDetails.get("id")))
                    .where(
                            criteriaBuilder.equal(orderDetails.get("product"), root),
                            criteriaBuilder.equal(orderDetails.get("order").get("statusOrder"), "RECEIVED")
                    );

            //---rating
            Subquery<Double> subVote = query.subquery(Double.class);
            Root<FeedBackEntity> feedRoot = subVote.from(FeedBackEntity.class);
            subVote.select(criteriaBuilder.avg(feedRoot.get("rating")))
                    .where(
                            criteriaBuilder.equal(feedRoot.get("orderDetail").get("product"), root),
                            criteriaBuilder.equal(feedRoot.get("orderDetail").get("order").get("account").get("isActive"), true)
                    );

            //---view
            Subquery<Long> subView = query.subquery(Long.class);
            Root<ProductView> prdView = subView.from(ProductView.class);
            subView.select(criteriaBuilder.count(prdView.get("id")))
                    .where(criteriaBuilder.equal(prdView.get("product"), root));

            query.orderBy(
                    criteriaBuilder.desc(subVote),
                    criteriaBuilder.desc(subSeller),
                    criteriaBuilder.desc(subView)
            );

            query.orderBy(
                    criteriaBuilder.desc(subSeller),// Rating cao nhất trước
                    criteriaBuilder.desc(subVote),   // Số lượng đơn hàng nhận cao nhất tiếp theo
                    criteriaBuilder.desc(subView)  // Số lượt xem cao nhất
            );

            // Giới hạn số lượng kết quả trả về (lấy tối đa 5 sản phẩm)
//            query.setMaxResults(5);
            return null;

        };
    }

    public Specification<ProductEntity> hasIdShop(Long idShop) {
        return (root, query, criteriaBuilder) -> {
            if (idShop == null)
                return null;
            return criteriaBuilder.equal(root.get("shop").get("id"), idShop);
        };
    }

    public Specification<ProductEntity> hasParentCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null)
                return null;

            List<CategoryResponse> categoryResponseList = categoryService.findAllChildren(categoryId);
            List<Long> ids = new ArrayList<>();
            for (var it : categoryResponseList) {
                ids.add(it.getId());
            }

            return root.get("category").get("id").in(ids);
        };
    }

    public Specification<ProductEntity> hasStartPrice(Double startPrice) {
        return (root, query, criteriaBuilder) -> {
            if (startPrice == null)
                return null;

//            Subquery<Long> subquery = query.subquery(Long.class);
//            Root<StockEntity> stockRoot = subquery.from(StockEntity.class);
//
//            subquery.select(stockRoot.get("id"));
//            subquery.where(
//                    criteriaBuilder.equal(stockRoot.get("product").get("id"), root.get("id")),
//                    criteriaBuilder.greaterThanOrEqualTo(stockRoot.get("price"), startPrice)
//            );
//
//            return criteriaBuilder.or(
//                criteriaBuilder.greaterThanOrEqualTo(root.get("basePrice"), startPrice),
//                    criteriaBuilder.exists(subquery)
//            );

            Subquery<Double> subquery = query.subquery(Double.class);
            Root<StockEntity> stock = subquery.from(StockEntity.class);
            subquery.select(criteriaBuilder.min(stock.get("price")))
                    .where(criteriaBuilder.equal(stock.get("product"), root));

            return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(subquery, startPrice)
            );
        };
    }

    public Specification<ProductEntity> hasEndPrice(Double endPrice) {
        return (root, query, criteriaBuilder) -> {
//            if (endPrice == null)
//                return null;
//
//            Subquery<Long> subquery = query.subquery(Long.class);
//            Root<StockEntity> stockRoot = subquery.from(StockEntity.class);
//
//            subquery.select(stockRoot.get("id"));
//
//            subquery.where(
//                    criteriaBuilder.equal(stockRoot.get("product").get("id"), root.get("id")),
//                    criteriaBuilder.lessThanOrEqualTo(stockRoot.get("price"), endPrice)
//            );
//
//            return criteriaBuilder.or(
////              criteriaBuilder.lessThanOrEqualTo(root.get("basePrice"), endPrice),
//                    criteriaBuilder.exists(subquery)
//            );

            if (endPrice == null)
                return null;

            Subquery<Double> subquery = query.subquery(Double.class);
            Root<StockEntity> stock = subquery.from(StockEntity.class);
            subquery.select(criteriaBuilder.min(stock.get("price")))
                    .where(criteriaBuilder.equal(stock.get("product"), root));

            return criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(subquery, endPrice)
            );

        };
    }

    public Specification<ProductEntity> hasDefaultFindShop() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("isDelete")),
                    criteriaBuilder.equal(root.get("isDelete"), false)
            );
        };
    }

    //---tìm theo trạng thái
    public Specification<ProductEntity> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null)
                return null;
            //---active
            if (status.equalsIgnoreCase("active")) {
                return criteriaBuilder.equal(root.get("isActive"), true);
            }
            //---in active
            else if (status.equalsIgnoreCase("hidden")) {
                return criteriaBuilder.equal(root.get("isActive"), false);
            }
            //---ban
            else if (status.equalsIgnoreCase("ban")) {
                return criteriaBuilder.equal(root.get("isBan"), true);
            }
            //---normal
            else if (status.equalsIgnoreCase("normal")) {
                return criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("isActive"), true),
                        criteriaBuilder.equal(root.get("isBan"), false)
                );
            }
            return null;
        };
    }

    public Specification<ProductEntity> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                isActive == null ? null : criteriaBuilder.equal(root.get("isActive"), isActive);
    }


    public Specification<ProductEntity> defaultFind() {
        return (root, query, criteriaBuilder) -> {

            //---tạo 1 truy vấn con với kiểu trả về là Long
            Subquery<Long> subquery = query.subquery(Long.class);

            //---roor lấy từ Stock
            Root<StockEntity> stockRoot = subquery.from(StockEntity.class);

            // Lấy ra id
            subquery.select(stockRoot.get("id"));
            subquery.where(
                    criteriaBuilder.equal(stockRoot.get("product").get("id"), root.get("id")),
                    criteriaBuilder.greaterThan(stockRoot.get("quantity"), 0)
            );

            return criteriaBuilder.and(
                    criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("isActive")),
                            criteriaBuilder.equal(root.get("isActive"), true)
                    ),
                    criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("isBan")),
                            criteriaBuilder.equal(root.get("isBan"), false)
                    ),
                    criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("isDelete")),
                            criteriaBuilder.equal(root.get("isDelete"), false)
                    ),
                    criteriaBuilder.equal(root.get("shop").get("isApproved"), true),
                    criteriaBuilder.or(
                            criteriaBuilder.exists(subquery)
                    )
            );
        };
    }

    //---location
    public Specification<ProductEntity> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> {
            if (location == null)
                return null;

            return criteriaBuilder.equal(root.get("shop").get("detailedAddress").get("province"), location);
        };
    }

    //----many location
    public Specification<ProductEntity> hasManyLocation(List<String> locations) {
        return (root, query, criteriaBuilder) -> {
            if (locations == null)
                return null;

//            return criteriaBuilder.equal(root.get("shop").get("detailedAddress").get("province"), locations);

            return root.get("shop")
                    .get("detailedAddress")
                    .get("province")
                    .in(locations);
        };
    }
//    IProductService

    //---feed back
    public Specification<ProductEntity> hasVoting(Double voteValue) {
        return (root, query, criteriaBuilder) -> {
            if (voteValue == null)
                return null;

            Subquery<Double> subquery = query.subquery(Double.class);
            Root<FeedBackEntity> feedRoot = subquery.from(FeedBackEntity.class);
            subquery.select(criteriaBuilder.avg(feedRoot.get("rating")))
                    .where(
                            criteriaBuilder.equal(feedRoot.get("orderDetail").get("product"), root),
                            criteriaBuilder.equal(feedRoot.get("orderDetail").get("order").get("account").get("isActive"), true)
                    );

            return criteriaBuilder.greaterThanOrEqualTo(subquery, voteValue);
        };
    }

    //    ProductService
//ProductMapper
//---sorting
    public Specification<ProductEntity> sortBy(String sortKey) {
        return (root, query, criteriaBuilder) -> {
            if (sortKey == null)
                return null;

            //---sản phẩm mới nhất
            if (sortKey.equalsIgnoreCase("new")) {
                //---desc là giảm dần
                query.orderBy(criteriaBuilder.desc(root.get("createDate")));
            }
            //---tăng theo giá
            else if (sortKey.equalsIgnoreCase("priceUp")) {
                Subquery<Number> subquery = query.subquery(Number.class);
                Root<StockEntity> stock = subquery.from(StockEntity.class);
                subquery.select(criteriaBuilder.min(stock.get("price")))
                        .where(criteriaBuilder.equal(stock.get("product"), root));

                query.orderBy(criteriaBuilder.asc(subquery));
            }
            //----giảm theo giá
            else if (sortKey.equalsIgnoreCase("priceDown")) {
                Subquery<Number> subquery = query.subquery(Number.class);
                Root<StockEntity> stock = subquery.from(StockEntity.class);
                subquery.select(criteriaBuilder.min(stock.get("price")))
                        .where(criteriaBuilder.equal(stock.get("product"), root));

                query.orderBy(criteriaBuilder.desc(subquery));
            }
            //---độ phổ biến
            //(rating, bán nhiều, view)
            else if (sortKey.equalsIgnoreCase("popular")) {
                //---số lượng bán
                Subquery<Long> subSeller = query.subquery(Long.class);
                Root<OrderDetailEntity> orderDetails = subSeller.from(OrderDetailEntity.class);

                //---số lượng order ("RECEIVED"
                subSeller.select(criteriaBuilder.count(orderDetails.get("id")))
                        .where(
                                criteriaBuilder.equal(orderDetails.get("product"), root),
                                criteriaBuilder.equal(orderDetails.get("order").get("statusOrder"), "RECEIVED")
                        );

                //---rating
                Subquery<Double> subVote = query.subquery(Double.class);
                Root<FeedBackEntity> feedRoot = subVote.from(FeedBackEntity.class);
                subVote.select(criteriaBuilder.avg(feedRoot.get("rating")))
                        .where(
                                criteriaBuilder.equal(feedRoot.get("orderDetail").get("product"), root),
                                criteriaBuilder.equal(feedRoot.get("orderDetail").get("order").get("account").get("isActive"), true)
                        );

                //---view
                Subquery<Long> subView = query.subquery(Long.class);
                Root<ProductView> prdView = subView.from(ProductView.class);
                subView.select(criteriaBuilder.count(prdView.get("id")))
                        .where(criteriaBuilder.equal(prdView.get("product"), root));

                query.orderBy(
                        criteriaBuilder.desc(subSeller),
                        criteriaBuilder.desc(subVote),
                        criteriaBuilder.desc(subView)
                );
            }
            //---bán chạy
            else if (sortKey.equalsIgnoreCase("sellest")) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<OrderDetailEntity> orderDetails = subquery.from(OrderDetailEntity.class);


                //---số lượng order ("RECEIVED"
                subquery.select(criteriaBuilder.count(orderDetails.get("id")))
                        .where(
                                criteriaBuilder.equal(orderDetails.get("product"), root),
                                criteriaBuilder.equal(orderDetails.get("order").get("statusOrder"), "RECEIVED")
                        );

                query.orderBy(criteriaBuilder.desc(subquery));
            }

            return null;
        };
    }

//    IProductService
}

//CategoryService
//ProductService
//ProductEntity
//ClientFeedbackController