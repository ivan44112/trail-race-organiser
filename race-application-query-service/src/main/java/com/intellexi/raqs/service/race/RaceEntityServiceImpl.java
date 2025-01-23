package com.intellexi.raqs.service.race;

import com.intellexi.raqs.service.dto.EventMessageRequest;
import com.intellexi.raqs.service.dto.EventMessageResponse;
import com.intellexi.raqs.service.dto.RaceDto;
import com.intellexi.raqs.persistence.domain.RaceEntity;
import com.intellexi.raqs.persistence.repository.RaceEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RaceEntityServiceImpl implements RaceEntityService {
    private final RaceEntityRepository raceEntityRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RaceEntityServiceImpl(RaceEntityRepository raceEntityRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.raceEntityRepository = raceEntityRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public List<RaceEntity> getAllRaces() {
        return raceEntityRepository.findAll();
    }

    @Override
    public RaceEntity getRaceEntityById(UUID id) {
        return raceEntityRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Unable to find race with id: " + id));
    }

    @Override
    public void receiveAndProcessEvent(EventMessageRequest<?> eventMessageRequest) {
        log.info("Received event: {}", eventMessageRequest);

        if (eventMessageRequest.getOperationType() == null) {
            log.warn("Operation type is null");
            sendResponse("Invalid operation: Operation type is null", 400);
            return;
        }

        switch (eventMessageRequest.getOperationType()) {
            case CREATE -> handleCreate((RaceDto) eventMessageRequest.getPayload());
            case UPDATE -> handleUpdate((RaceDto) eventMessageRequest.getPayload());
            case DELETE -> handleDelete(UUID.fromString((String) eventMessageRequest.getPayload()));
            default -> {
                log.warn("Unknown event type: {}", eventMessageRequest.getOperationType());
                sendResponse("Invalid operation", 400);
            }
        }
    }

    private void handleCreate(RaceDto dto) {
        log.info("Attempting to create new race");

        try {
            RaceEntity raceEntity = RaceEntity.builder()
                    .name(dto.getName())
                    .distance(dto.getDistance())
                    .build();
            raceEntityRepository.save(raceEntity);
            log.info("Race created: {}", raceEntity);
            sendResponse("Race created successfully", 201);
        } catch (Exception e) {
            log.error("Error creating race", e);
            sendResponse("Failed to create race", 500);
        }
    }

    private void handleUpdate(RaceDto dto) {
        log.info("Attempting to update race");

        try {
            Optional<RaceEntity> optionalRaceEntity = raceEntityRepository.findById(dto.getId());
            if (optionalRaceEntity.isPresent()) {
                RaceEntity raceEntity = optionalRaceEntity.get();
                if (dto.getName() != null) {
                    raceEntity.setName(dto.getName());
                }
                if (dto.getDistance() != null) {
                    raceEntity.setDistance(dto.getDistance());
                }
                raceEntityRepository.save(raceEntity);
                log.info("Race updated: {}", raceEntity);
                sendResponse("Race updated successfully", 200);
            } else {
                sendResponse("Race not found", 404);
            }
        } catch (Exception e) {
            log.error("Error updating race", e);
            sendResponse("Failed to update race", 500);
        }
    }

    private void handleDelete(UUID id) {
        log.info("Attempting to delete race");

        try {
            if (raceEntityRepository.existsById(id)) {
                raceEntityRepository.deleteById(id);
                log.info("Race deleted: {}", id);
                sendResponse("Race deleted successfully", 200);
            } else {
                sendResponse("Race not found", 404);
            }
        } catch (Exception e) {
            log.error("Error deleting race", e);
            sendResponse("Failed to delete race", 500);
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
