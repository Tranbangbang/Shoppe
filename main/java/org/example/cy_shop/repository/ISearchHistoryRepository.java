package org.example.cy_shop.repository;

import org.example.cy_shop.entity.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByAccIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
