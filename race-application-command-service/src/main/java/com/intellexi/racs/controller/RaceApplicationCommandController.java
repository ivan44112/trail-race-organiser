package com.intellexi.racs.controller;

import com.intellexi.racs.aspect.LogRequests;
import com.intellexi.racs.dto.EventMessageRequest;
import com.intellexi.racs.dto.RaceApplicationDto;
import com.intellexi.racs.dto.SuccessfulTopicResponseMessage;
import com.intellexi.racs.utils.OperationType;
import com.intellexi.racs.utils.Subscriber;
import com.intellexi.racs.utils.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.intellexi.racs.utils.RaceApplicationValidator.*;

@Controller
@RequestMapping("/api/v1/commands/applications")
@LogRequests
@Slf4j
public class RaceApplicationCommandController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RaceApplicationCommandController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createRaceApplication(@RequestBody RaceApplicationDto application) {
        log.info("Sending CREATE event to command topic");

        ValidationResult result = (isFirstNameValid())
                .and(isLastNameValid())
                .and(isClubValid())
                .apply(application);

        if (result != ValidationResult.SUCCESS) {
            throw new ValidationException(result.name());
        }

        simpMessagingTemplate.convertAndSend("/topic/commands",
                new EventMessageRequest<>(OperationType.CREATE,
                        application,
                        Subscriber.RACE_APPLICATION)
        );
        log.info("Message sent to /topic/commands: {}", application);

        return ResponseEntity.ok(new SuccessfulTopicResponseMessage("CREATE event sent to topic successfully."));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRaceApplication(@PathVariable UUID id, @RequestBody RaceApplicationDto application) {
        log.info("Sending UPDATE event to command topic");
        application.setId(id);

        simpMessagingTemplate.convertAndSend("/topic/commands",
                new EventMessageRequest<>(OperationType.UPDATE,
                        application,
                        Subscriber.RACE_APPLICATION)
        );
        log.info("Message sent to /topic/commands: {}", application);

        return ResponseEntity.ok(new SuccessfulTopicResponseMessage("UPDATE event sent to topic successfully."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRaceApplication(@PathVariable UUID id) {
        log.info("Sending DELETE event to command topic");

        if (id == null) {
            throw new ValidationException("Id is null.");
        }
        simpMessagingTemplate.convertAndSend("/topic/commands",
                new EventMessageRequest<>(OperationType.DELETE,
                        id,
                        Subscriber.RACE_APPLICATION));
        log.info("Message sent to /topic/commands: {}", id);

        return ResponseEntity.ok(new SuccessfulTopicResponseMessage("DELETE event sent to topic successfully."));
    }
}
