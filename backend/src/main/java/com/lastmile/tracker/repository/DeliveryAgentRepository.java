package com.lastmile.tracker.repository;

import com.lastmile.tracker.entity.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {

    List<DeliveryAgent> findByAvailableTrueAndCurrentZoneIdOrderByIdAsc(Long zoneId);
    
    List<DeliveryAgent> findByAvailableTrueOrderByIdAsc();

    Optional<DeliveryAgent> findByUserId(Long userId);
}
