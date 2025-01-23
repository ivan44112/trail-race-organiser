package com.intellexi.racs.controller;

import com.intellexi.racs.dto.EventMessageRequest;
import com.intellexi.racs.dto.RaceDto;
import com.intellexi.racs.utils.Distance;
import com.intellexi.racs.utils.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RaceControllerTests {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RaceController raceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRace_Success() {
        RaceDto race = RaceDto.builder()
                .name("Croatian marathon")
                .distance(Distance.MARATHON)
                .build();

        ResponseEntity<?> response = raceController.createRace(race);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                eq("/topic/commands"),
                any(EventMessageRequest.class)
        );
    }

    @Test
    void testCreateRace_InvalidName() {
        RaceDto race = RaceDto.builder()
                .name(null)
                .distance(Distance.MARATHON)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> raceController.createRace(race));

        assertEquals("Race name is null", exception.getMessage());
        verify(simpMessagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void testUpdateRace_Success() {
        UUID id = UUID.randomUUID();
        RaceDto race = RaceDto.builder()
                .name("Half Marathon")
                .distance(Distance.HALF_MARATHON)
                .build();

        ResponseEntity<?> response = raceController.updateRace(id, race);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                eq("/topic/commands"),
                any(EventMessageRequest.class)
        );
    }

    @Test
    void testDeleteRace_Success() {
        UUID id = UUID.randomUUID();

        ResponseEntity<?> response = raceController.deleteRace(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                eq("/topic/commands"),
                any(EventMessageRequest.class)
        );
    }

    @Test
    void testDeleteRace_InvalidUUID() {
        ValidationException exception = assertThrows(ValidationException.class, () -> raceController.deleteRace(null));

        assertEquals("Id is null.", exception.getMessage());
        verify(simpMessagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }
}
