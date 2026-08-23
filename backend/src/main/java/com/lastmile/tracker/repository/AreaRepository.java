package com.lastmile.tracker.repository;

import com.lastmile.tracker.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findByPincode(String pincode);

    boolean existsByPincode(String pincode);

    boolean existsByPincodeAndIdNot(String pincode, Long id);

    boolean existsByZoneId(Long zoneId);
}
