package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.ratecard.RateCardRequest;
import com.lastmile.tracker.dto.ratecard.RateCardResponse;
import com.lastmile.tracker.entity.RateCard;
import com.lastmile.tracker.entity.Zone;
import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.enums.RateType;
import com.lastmile.tracker.exception.DuplicateResourceException;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.RateCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateCardService {

    private final RateCardRepository rateCardRepository;
    private final ZoneService zoneService;

    @Transactional
    public RateCardResponse create(RateCardRequest request) {
        validateDirectionAndType(request.getPickupZoneId(), request.getDropZoneId(), request.getRateType());
        
        // This findBy logic is actually existsBy in repo, but wait, the repo only has findBy and existsBy...IdNot.
        rateCardRepository.findByRateTypeAndOrderTypeAndPickupZoneIdAndDropZoneId(
                request.getRateType(), request.getOrderType(), request.getPickupZoneId(), request.getDropZoneId()
        ).ifPresent(rc -> {
            throw new DuplicateResourceException("Rate card already exists for this combination");
        });

        Zone pickupZone = zoneService.getZoneEntity(request.getPickupZoneId());
        Zone dropZone = zoneService.getZoneEntity(request.getDropZoneId());

        RateCard rateCard = RateCard.builder()
                .rateType(request.getRateType())
                .orderType(request.getOrderType())
                .pickupZone(pickupZone)
                .dropZone(dropZone)
                .baseRate(request.getBaseRate())
                .ratePerKg(request.getRatePerKg())
                .build();

        return toResponse(rateCardRepository.save(rateCard));
    }

    @Transactional(readOnly = true)
    public List<RateCardResponse> findAll() {
        return rateCardRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RateCardResponse findById(Long id) {
        return toResponse(getRateCardEntity(id));
    }

    @Transactional
    public RateCardResponse update(Long id, RateCardRequest request) {
        validateDirectionAndType(request.getPickupZoneId(), request.getDropZoneId(), request.getRateType());
        RateCard rateCard = getRateCardEntity(id);

        if (rateCardRepository.existsByRateTypeAndOrderTypeAndPickupZoneIdAndDropZoneIdAndIdNot(
                request.getRateType(), request.getOrderType(), request.getPickupZoneId(), request.getDropZoneId(), id)) {
            throw new DuplicateResourceException("Rate card already exists for this combination");
        }

        Zone pickupZone = zoneService.getZoneEntity(request.getPickupZoneId());
        Zone dropZone = zoneService.getZoneEntity(request.getDropZoneId());

        rateCard.setRateType(request.getRateType());
        rateCard.setOrderType(request.getOrderType());
        rateCard.setPickupZone(pickupZone);
        rateCard.setDropZone(dropZone);
        rateCard.setBaseRate(request.getBaseRate());
        rateCard.setRatePerKg(request.getRatePerKg());

        return toResponse(rateCardRepository.save(rateCard));
    }

    @Transactional
    public void delete(Long id) {
        RateCard rateCard = getRateCardEntity(id);
        rateCardRepository.delete(rateCard);
    }

    @Transactional(readOnly = true)
    public RateCardResponse findApplicableRateCard(Long pickupZoneId, Long dropZoneId, OrderType orderType) {
        RateType rateType = pickupZoneId.equals(dropZoneId) ? RateType.INTRA_ZONE : RateType.INTER_ZONE;
        RateCard rc = rateCardRepository.findByRateTypeAndOrderTypeAndPickupZoneIdAndDropZoneId(
                rateType, orderType, pickupZoneId, dropZoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Rate card not found for this route"));
        return toResponse(rc);
    }

    private void validateDirectionAndType(Long pZone, Long dZone, RateType type) {
        if (pZone.equals(dZone) && type != RateType.INTRA_ZONE) {
            throw new IllegalArgumentException("Invalid rate type. Must be INTRA_ZONE when pickup and drop zones are the same.");
        }
        if (!pZone.equals(dZone) && type != RateType.INTER_ZONE) {
            throw new IllegalArgumentException("Invalid rate type. Must be INTER_ZONE when pickup and drop zones are different.");
        }
    }

    private RateCard getRateCardEntity(Long id) {
        return rateCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rate card not found"));
    }

    private RateCardResponse toResponse(RateCard rateCard) {
        return RateCardResponse.builder()
                .id(rateCard.getId())
                .rateType(rateCard.getRateType())
                .orderType(rateCard.getOrderType())
                .pickupZoneId(rateCard.getPickupZone().getId())
                .pickupZoneName(rateCard.getPickupZone().getName())
                .dropZoneId(rateCard.getDropZone().getId())
                .dropZoneName(rateCard.getDropZone().getName())
                .baseRate(rateCard.getBaseRate())
                .ratePerKg(rateCard.getRatePerKg())
                .createdAt(rateCard.getCreatedAt())
                .updatedAt(rateCard.getUpdatedAt())
                .build();
    }
}
