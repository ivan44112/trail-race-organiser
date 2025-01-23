package com.intellexi.raqs.service.raceapplication;

import com.intellexi.raqs.service.dto.EventMessageRequest;
import com.intellexi.raqs.service.dto.EventMessageResponse;
import com.intellexi.raqs.service.dto.RaceApplicationDto;
import com.intellexi.raqs.persistence.domain.RaceApplicationEntity;
import com.intellexi.raqs.persistence.repository.RaceApplicationEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RaceApplicationEntityServiceImpl implements RaceApplicationEntityService {
    private final RaceApplicationEntityRepository raceApplicationEntityRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RaceApplicationEntityServiceImpl(RaceApplicationEntityRepository raceApplicationEntityRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.raceApplicationEntityRepository = raceApplicationEntityRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public List<RaceApplicationEntity> getAllRaceApplications() {
        return raceApplicationEntityRepository.findAll();
    }

    @Override
    public RaceApplicationEntity getApplicationById(UUID id) {
        return raceApplicationEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Unable to find race application with id: " + id));
    }

    @Override
    @EventListener
    public void receiveAndProcessEvent(EventMessageRequest<?> eventMessageRequest) {
        log.info("Received event: {}", eventMessageRequest);

        if (eventMessageRequest.getOperationType() == null) {
            log.warn("Operation type is null");
            sendResponse("Invalid operation: Operation type is null", 400);
            return;
        }

        switch (eventMessageRequest.getOperationType()) {
            case CREATE -> handleCreate((RaceApplicationDto) eventMessageRequest.getPayload());
            case UPDATE -> handleUpdate((RaceApplicationDto) eventMessageRequest.getPayload());
            case DELETE -> handleDelete(UUID.fromString((String) eventMessageRequest.getPayload()));
            default -> {
                log.warn("Unknown event type: {}", eventMessageRequest.getOperationType());
                sendResponse("Invalid operation", 400);
            }
        }
    }

    private void handleCreate(RaceApplicationDto dto) {
        log.info("Attempting to create new race application");

        try {
            RaceApplicationEntity raceApplicationEntity = RaceApplicationEntity.builder()
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .club(dto.getClub())
                    .raceId(dto.getRaceId())
                    .build();
            raceApplicationEntityRepository.save(raceApplicationEntity);
            log.info("Race application created: {}", raceApplicationEntity);
            sendResponse("Race Application created successfully", 201);
        } catch (Exception e) {
            log.error("Error creating application", e);
            sendResponse("Failed to create race application", 500);
        }
    }

    private void handleUpdate(RaceApplicationDto dto) {
        log.info("Attempting to update race application");

        try {
            Optional<RaceApplicationEntity> optionalRaceApplication = raceApplicationEntityRepository.findById(dto.getId());
            if (optionalRaceApplication.isPresent()) {
                RaceApplicationEntity raceApplicationEntity = optionalRaceApplication.get();
                if (dto.getFirstName() != null) {
                    raceApplicationEntity.setFirstName(dto.getFirstName());
                }
                if (dto.getLastName() != null) {
                    raceApplicationEntity.setLastName(dto.getLastName());
                }
                if (dto.getClub() != null) {
                    raceApplicationEntity.setClub(dto.getClub());
                }
                raceApplicationEntityRepository.save(raceApplicationEntity);
                log.info("Application updated: {}", raceApplicationEntity);
                sendResponse("Race application updated successfully", 200);
            } else {
                sendResponse("Race application not found", 404);
            }
        } catch (Exception e) {
            log.error("Error updating application", e);
            sendResponse("Failed to update race application", 500);
        }
    }

    private void handleDelete(UUID id) {
        log.info("Attempting to delete race application");

        try {
            if (raceApplicationEntityRepository.existsById(id)) {
                raceApplicationEntityRepository.deleteById(id);
                log.info("Race application deleted: {}", id);
                sendResponse("Race application deleted successfully", 200);
            } else {
                sendResponse("Race application not found", 404);
            }
        } catch (Exception e) {
            log.error("Error deleting race application", e);
            sendResponse("Failed to delete race application", 500);
        }
    }

    private void sendResponse(String message, int statusCode) {
        EventMessageResponse response = EventMessageResponse.builder()
                .message(message)
                .statusCode(statusCode)
                .build();

        log.info("Sending response: {}", response);
        simpMessagingTemplate.convertAndSend("/topic/responses", response);
    }
}
