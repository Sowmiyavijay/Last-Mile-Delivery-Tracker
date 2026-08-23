package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.surcharge.CodSurchargeRequest;
import com.lastmile.tracker.dto.surcharge.CodSurchargeResponse;
import com.lastmile.tracker.entity.CodSurcharge;
import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.exception.DuplicateResourceException;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.CodSurchargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodSurchargeService {

    private final CodSurchargeRepository codSurchargeRepository;

    @Transactional
    public CodSurchargeResponse updateSurcharge(OrderType orderType, CodSurchargeRequest request) {
        CodSurcharge surcharge = codSurchargeRepository.findByOrderType(orderType)
                .orElse(CodSurcharge.builder().orderType(orderType).build());
        
        surcharge.setSurchargeAmount(request.getSurchargeAmount());
        return toResponse(codSurchargeRepository.save(surcharge));
    }

    @Transactional(readOnly = true)
    public CodSurchargeResponse getCodSurcharge(OrderType orderType) {
        CodSurcharge surcharge = codSurchargeRepository.findByOrderType(orderType)
                .orElseThrow(() -> new ResourceNotFoundException("COD Surcharge configuration not found for " + orderType));
        return toResponse(surcharge);
    }
    
    @Transactional(readOnly = true)
    public List<CodSurchargeResponse> findAll() {
        return codSurchargeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private CodSurchargeResponse toResponse(CodSurcharge surcharge) {
        return CodSurchargeResponse.builder()
                .id(surcharge.getId())
                .orderType(surcharge.getOrderType())
                .surchargeAmount(surcharge.getSurchargeAmount())
                .createdAt(surcharge.getCreatedAt())
                .updatedAt(surcharge.getUpdatedAt())
                .build();
    }
}
