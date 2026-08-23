package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.agent.DeliveryAgentRequest;
import com.lastmile.tracker.dto.agent.DeliveryAgentResponse;
import com.lastmile.tracker.entity.DeliveryAgent;
import com.lastmile.tracker.entity.User;
import com.lastmile.tracker.entity.Zone;
import com.lastmile.tracker.enums.Role;
import com.lastmile.tracker.exception.DuplicateResourceException;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.DeliveryAgentRepository;
import com.lastmile.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAgentService {

    private final DeliveryAgentRepository deliveryAgentRepository;
    private final UserRepository userRepository;
    private final ZoneService zoneService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DeliveryAgentResponse createAgent(DeliveryAgentRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DELIVERY_AGENT)
                .build();
        
        user = userRepository.save(user);

        Zone zone = null;
        if (request.getCurrentZoneId() != null) {
            zone = zoneService.getZoneEntity(request.getCurrentZoneId());
        }

        DeliveryAgent agent = DeliveryAgent.builder()
                .user(user)
                .phone(request.getPhone())
                .currentZone(zone)
                .available(request.isAvailable())
                .build();

        return toResponse(deliveryAgentRepository.save(agent));
    }

    @Transactional(readOnly = true)
    public List<DeliveryAgentResponse> findAll() {
        return deliveryAgentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeliveryAgentResponse findById(Long id) {
        return toResponse(getAgentEntity(id));
    }

    @Transactional
    public DeliveryAgentResponse updateAgent(Long id, DeliveryAgentRequest request) {
        DeliveryAgent agent = getAgentEntity(id);

        User user = agent.getUser();
        user.setName(request.getName());
        
        // Update password if provided explicitly (naive check)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
             user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);

        Zone zone = null;
        if (request.getCurrentZoneId() != null) {
            zone = zoneService.getZoneEntity(request.getCurrentZoneId());
        }

        agent.setPhone(request.getPhone());
        agent.setCurrentZone(zone);
        agent.setAvailable(request.isAvailable());

        return toResponse(deliveryAgentRepository.save(agent));
    }

    @Transactional
    public void updateAvailability(Long id, boolean available) {
        DeliveryAgent agent = getAgentEntity(id);
        agent.setAvailable(available);
        deliveryAgentRepository.save(agent);
    }

    public DeliveryAgent getAgentEntity(Long id) {
        return deliveryAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found"));
    }

    public DeliveryAgentResponse toResponse(DeliveryAgent agent) {
        return DeliveryAgentResponse.builder()
                .id(agent.getId())
                .userId(agent.getUser().getId())
                .name(agent.getUser().getName())
                .email(agent.getUser().getEmail())
                .phone(agent.getPhone())
                .currentZoneId(agent.getCurrentZone() != null ? agent.getCurrentZone().getId() : null)
                .currentZoneName(agent.getCurrentZone() != null ? agent.getCurrentZone().getName() : null)
                .available(agent.isAvailable())
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();
    }
}
