package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.area.AreaRequest;
import com.lastmile.tracker.dto.area.AreaResponse;
import com.lastmile.tracker.entity.Area;
import com.lastmile.tracker.entity.Zone;
import com.lastmile.tracker.exception.DuplicateResourceException;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final ZoneService zoneService;

    @Transactional
    public AreaResponse create(AreaRequest request) {
        if (areaRepository.existsByPincode(request.getPincode())) {
            throw new DuplicateResourceException("Pincode already exists");
        }

        Zone zone = zoneService.getZoneEntity(request.getZoneId());

        Area area = Area.builder()
                .name(request.getName())
                .pincode(request.getPincode())
                .zone(zone)
                .build();

        return toResponse(areaRepository.save(area));
    }

    @Transactional(readOnly = true)
    public List<AreaResponse> findAll() {
        return areaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AreaResponse findById(Long id) {
        return toResponse(getAreaEntity(id));
    }

    @Transactional(readOnly = true)
    public AreaResponse findByPincode(String pincode) {
        Area area = areaRepository.findByPincode(pincode)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found for pincode: " + pincode));
        return toResponse(area);
    }

    @Transactional
    public AreaResponse update(Long id, AreaRequest request) {
        Area area = getAreaEntity(id);

        if (areaRepository.existsByPincodeAndIdNot(request.getPincode(), id)) {
            throw new DuplicateResourceException("Pincode already exists");
        }

        Zone zone = zoneService.getZoneEntity(request.getZoneId());

        area.setName(request.getName());
        area.setPincode(request.getPincode());
        area.setZone(zone);

        return toResponse(areaRepository.save(area));
    }

    @Transactional
    public void delete(Long id) {
        Area area = getAreaEntity(id);
        areaRepository.delete(area);
    }

    private Area getAreaEntity(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found"));
    }

    private AreaResponse toResponse(Area area) {
        return AreaResponse.builder()
                .id(area.getId())
                .name(area.getName())
                .pincode(area.getPincode())
                .zoneId(area.getZone().getId())
                .zoneName(area.getZone().getName())
                .createdAt(area.getCreatedAt())
                .updatedAt(area.getUpdatedAt())
                .build();
    }
}
