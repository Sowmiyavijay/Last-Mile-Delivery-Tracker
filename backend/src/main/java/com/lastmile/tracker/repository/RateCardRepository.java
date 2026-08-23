package com.lastmile.tracker.repository;

import com.lastmile.tracker.entity.RateCard;
import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.enums.RateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, Long> {

    boolean existsByPickupZoneIdOrDropZoneId(Long pickupZoneId, Long dropZoneId);

    Optional<RateCard> findByRateTypeAndOrderTypeAndPickupZoneIdAndDropZoneId(
            RateType rateType,
            OrderType orderType,
            Long pickupZoneId,
            Long dropZoneId
    );

    boolean existsByRateTypeAndOrderTypeAndPickupZoneIdAndDropZoneIdAndIdNot(
            RateType rateType,
            OrderType orderType,
            Long pickupZoneId,
            Long dropZoneId,
            Long id
    );
}
