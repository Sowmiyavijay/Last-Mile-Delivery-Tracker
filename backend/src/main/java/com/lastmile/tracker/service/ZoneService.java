package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.zone.ZoneRequest;
import com.lastmile.tracker.dto.zone.ZoneResponse;
import com.lastmile.tracker.entity.Zone;
import com.lastmile.tracker.exception.DuplicateResourceException;
import com.lastmile.tracker.exception.ReferencedResourceException;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.AreaRepository;
import com.lastmile.tracker.repository.RateCardRepository;
import com.lastmile.tracker.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final AreaRepository areaRepository;
    private final RateCardRepository rateCardRepository;

    @Transactional
    public ZoneResponse create(ZoneRequest request) {
        if (zoneRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Zone name already exists");
        }

        Zone zone = Zone.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return toResponse(zoneRepository.save(zone));
    }

    @Transactional(readOnly = true)
    public List<ZoneResponse> findAll() {
        return zoneRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ZoneResponse findById(Long id) {
        return toResponse(getZoneEntity(id));
    }

    @Transactional(readOnly = true)
    public Zone getZoneEntity(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));
    }

    @Transactional
    public ZoneResponse update(Long id, ZoneRequest request) {
        Zone zone = getZoneEntity(id);

        if (zoneRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException("Zone name already exists");
        }

        zone.setName(request.getName());
        zone.setDescription(request.getDescription());

        return toResponse(zoneRepository.save(zone));
    }

    @Transactional
    public void delete(Long id) {
        Zone zone = getZoneEntity(id);

        if (areaRepository.existsByZoneId(id)) {
            throw new ReferencedResourceException("Cannot delete zone: areas are assigned to this zone");
        }

        if (rateCardRepository.existsByPickupZoneIdOrDropZoneId(id, id)) {
            throw new ReferencedResourceException("Cannot delete zone: rate cards reference this zone");
        }

        zoneRepository.delete(zone);
    }

    private ZoneResponse toResponse(Zone zone) {
        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .description(zone.getDescription())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }
}
