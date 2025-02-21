package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Report;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.EnumReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IReportRepository extends JpaRepository<Report,Long> {
    @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
    List<Report> findAllByCreatedAtAndDesc();

    Report getById(Long id);
    List<Report> findByStatusAndProduct_Shop_Id(EnumReportStatus status, Long shopId, Pageable pageable);
    long countByProduct_Shop_IdAndStatus(Long shopId, EnumReportStatus status);
    Optional<Report> findByProductAndReporter(ProductEntity product, Account reporter);
    long countByProduct(ProductEntity product);
}
