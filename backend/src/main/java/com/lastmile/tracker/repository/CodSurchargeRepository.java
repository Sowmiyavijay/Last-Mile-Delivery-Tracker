package com.lastmile.tracker.repository;

import com.lastmile.tracker.entity.CodSurcharge;
import com.lastmile.tracker.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodSurchargeRepository extends JpaRepository<CodSurcharge, Long> {

    Optional<CodSurcharge> findByOrderType(OrderType orderType);
}
