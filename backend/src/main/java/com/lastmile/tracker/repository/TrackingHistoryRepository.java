package com.lastmile.tracker.repository;

import com.lastmile.tracker.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory, Long> {
    List<TrackingHistory> findByOrderIdOrderByTimestampAscIdAsc(Long orderId);
}
