package org.example.cy_shop.specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.order.TrackingEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OrderShopSpecification {
    public static Specification<OrderEntity> hasKeySearch(String keySearch) {
        return (root, query, criteriaBuilder) -> {
            if (keySearch == null || keySearch.isEmpty())
                return null;

            String pattern = "%" + keySearch + "%";

            //chỉ định kiểu trả về của truy vấn con và root
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<OrderDetailEntity> subqueryRoot = subquery.from(OrderDetailEntity.class);

            //---trả ra 1 list id order details có productName phù hợp
            Predicate productPredicate = criteriaBuilder.like(subqueryRoot.get("product").get("productName"), pattern);

            //---lấy id Order
            subquery.select(subqueryRoot.get("order").get("id"))
                    .distinct(true)
                    .where(productPredicate);

            //---lấy ra order phù hợp
            Predicate orderProductName = criteriaBuilder.in(root.get("id")).value(subquery);

            //---tìm theo email hoặc userName
            Predicate orderEmail = criteriaBuilder.like(root.get("account").get("email"), pattern);

            //---tìm theo user_name
            Predicate orderUserName = criteriaBuilder.like(root.get("account").get("username"), pattern);

            return criteriaBuilder.or(orderProductName, orderEmail, orderUserName);
        };
    }

    public static Specification<OrderEntity> hasOrderStatus(StatusOrderEnum orderStatus) {
        return (root, query, criteriaBuilder) -> {
            if (orderStatus == null)
                return null;

            return criteriaBuilder.equal(root.get("statusOrder"), orderStatus);
        };
    }

    public static Specification<OrderEntity> hasStatusPayment(StatusPaymentEnum statusPayment) {
        return (root, query, criteriaBuilder) -> {
            if (statusPayment == null)
                return null;

            return criteriaBuilder.and(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("statusOrder"), StatusOrderEnum.CANCELED),
                            criteriaBuilder.equal(root.get("statusOrder"), StatusOrderEnum.NOT_RECEIVED),
                            criteriaBuilder.equal(root.get("statusOrder"), StatusOrderEnum.RETURNED)
                            ),
                    criteriaBuilder.equal(root.get("statusPayment"), StatusPaymentEnum.PAID)
            );
        };
    }

    public static Specification<OrderEntity> hasStartDate(LocalDate start) {
        return (root, query, criteriaBuilder) -> {
            if (start == null)
                return null;

            return criteriaBuilder.greaterThanOrEqualTo(root.get("modifierDate").as(LocalDate.class), start);
        };
    }

    public static Specification<OrderEntity> hasEndDate(LocalDate end) {
        return (root, query, criteriaBuilder) -> {
            if (end == null)
                return null;


            return criteriaBuilder.lessThanOrEqualTo(root.get("modifierDate").as(LocalDate.class), end);
        };
    }

    public static Specification<OrderEntity> hasIdShop(Long idShop) {
        return (root, query, criteriaBuilder) -> {
            if (idShop == null)
                return null;
            return criteriaBuilder.equal(root.get("idShop"), idShop);
        };
    }
}
