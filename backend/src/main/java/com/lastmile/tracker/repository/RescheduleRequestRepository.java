package com.lastmile.tracker.repository;

import com.lastmile.tracker.entity.RescheduleRequest;
import com.lastmile.tracker.enums.RescheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RescheduleRequestRepository extends JpaRepository<RescheduleRequest, Long> {
    List<RescheduleRequest> findByOrderIdAndRequestedByIdOrderByCreatedAtDesc(Long orderId, Long requestedById);
    List<RescheduleRequest> findByOrderIdAndStatusIn(Long orderId, Collection<RescheduleStatus> statuses);
    List<RescheduleRequest> findByStatusAndOrderIdOrderByCreatedAtDesc(RescheduleStatus status, Long orderId);
    List<RescheduleRequest> findAllByOrderByCreatedAtDesc();
}
