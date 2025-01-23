package com.intellexi.racs.controller;

import com.intellexi.racs.aspect.LogRequests;
import com.intellexi.racs.dto.EventMessageRequest;
import com.intellexi.racs.dto.RaceDto;
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

@Controller
@RequestMapping("/api/v1/commands/races")
@LogRequests
@Slf4j
public class RaceController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RaceController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createRace(@RequestBody RaceDto race) {
        log.info("Sending CREATE event to command topic");

        if (race.getName() == null) {
            throw new ValidationException("Race name is null");
        }

        simpMessagingTemplate.convertAndSend("/topic/commands",
                new EventMessageRequest<>(OperationType.CREATE,
                        race,
                        Subscriber.RACE)
        );
        log.info("Message sent to /topic/commands: {}", race);

        return ResponseEntity.ok(new SuccessfulTopicResponseMessage("CREATE event sent to topic successfully."));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRace(@PathVariable UUID id, @RequestBody RaceDto race) {
        log.info("Sending UPDATE event to command topic");
        race.setId(id);

        simpMessagingTemplate.convertAndSend("/topic/commands",
                new EventMessageRequest<>(OperationType.UPDATE,
                        race,
                        Subscriber.RACE)
        );
        log.info("Message sent to /topic/commands: {}", race);

        return ResponseEntity.ok(new SuccessfulTopicResponseMessage("UPDATE event sent to topic successfully."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRace(@PathVariable UUID id) {
        log.info("Sending DELETE event to command topic");

        if (id == null) {
            throw new ValidationException("Id is null.");
        }
        simpMessagingTemplate.convertAndSend("/topic/commands",
                new EventMessageRequest<>(OperationType.DELETE,
                        id,
                        Subscriber.RACE));
        log.info("Message sent to /topic/commands: {}", id);

        return ResponseEntity.ok(new SuccessfulTopicResponseMessage("DELETE event sent to topic successfully."));
    }
}
