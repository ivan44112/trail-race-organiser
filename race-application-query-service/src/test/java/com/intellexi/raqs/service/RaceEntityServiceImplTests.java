package com.intellexi.raqs.service;

import com.intellexi.raqs.persistence.domain.RaceEntity;
import com.intellexi.raqs.persistence.repository.RaceEntityRepository;
import com.intellexi.raqs.service.dto.EventMessageRequest;
import com.intellexi.raqs.service.dto.EventMessageResponse;
import com.intellexi.raqs.service.dto.RaceDto;
import com.intellexi.raqs.service.race.RaceEntityServiceImpl;
import com.intellexi.raqs.utils.Distance;
import com.intellexi.raqs.utils.OperationType;
import com.intellexi.raqs.utils.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RaceEntityServiceImplTests {
    @Mock
    private RaceEntityRepository raceEntityRepository;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @InjectMocks
    private RaceEntityServiceImpl raceEntityService;
    private RaceDto raceDto;
    private RaceEntity raceEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        raceDto = RaceDto.builder()
                .id(UUID.randomUUID())
                .name("CROATIAN MARATHON")
                .distance(Distance.MARATHON)
                .build();

        raceEntity = RaceEntity.builder()
                .id(raceDto.getId())
                .name(raceDto.getName())
                .distance(raceDto.getDistance())
                .build();
    }

    @Test
    void getAllRaces_ShouldReturnListOfRaces() {
        when(raceEntityRepository.findAll()).thenReturn(List.of(raceEntity));

        List<RaceEntity> races = raceEntityService.getAllRaces();

        assertEquals(1, races.size());
        assertEquals("CROATIAN MARATHON", races.get(0).getName());
        assertEquals(Distance.MARATHON, races.get(0).getDistance());
        verify(raceEntityRepository, times(1)).findAll();
    }

    @Test
    void getRaceEntityById_ShouldReturnRace() {
        when(raceEntityRepository.findById(any(UUID.class))).thenReturn(Optional.of(raceEntity));

        RaceEntity foundRace = raceEntityService.getRaceEntityById(raceDto.getId());

        assertNotNull(foundRace);
        assertEquals("CROATIAN MARATHON", foundRace.getName());
        assertEquals(Distance.MARATHON, foundRace.getDistance());
        verify(raceEntityRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getRaceEntityById_ShouldThrowExceptionWhenNotFound() {
        when(raceEntityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                raceEntityService.getRaceEntityById(raceDto.getId()));

        assertTrue(exception.getMessage().contains("Unable to find race with id"));
        verify(raceEntityRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void receiveAndProcessEvent_CreateOperation_ShouldCreateRace() {
        EventMessageRequest<RaceDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.CREATE);
        eventMessageRequest.setPayload(raceDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE);

        raceEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(raceEntityRepository, times(1)).save(any(RaceEntity.class));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void receiveAndProcessEvent_UpdateOperation_ShouldUpdateRace() {
        EventMessageRequest<RaceDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.UPDATE);
        eventMessageRequest.setPayload(raceDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE);

        when(raceEntityRepository.findById(any(UUID.class))).thenReturn(Optional.of(raceEntity));

        raceEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(raceEntityRepository, times(1)).save(any(RaceEntity.class));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void receiveAndProcessEvent_DeleteOperation_ShouldDeleteRace() {
        EventMessageRequest<String> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.DELETE);
        eventMessageRequest.setPayload(raceDto.getId().toString());
        eventMessageRequest.setSubscriber(Subscriber.RACE);

        when(raceEntityRepository.existsById(any(UUID.class))).thenReturn(true);

        raceEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(raceEntityRepository, times(1)).deleteById(any(UUID.class));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void receiveAndProcessEvent_UnknownOperationType_ShouldSendErrorResponse() {
        EventMessageRequest<RaceDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(null);
        eventMessageRequest.setPayload(raceDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE);

        raceEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void handleCreate_ShouldSendErrorResponseWhenExceptionOccurs() {
        EventMessageRequest<RaceDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.CREATE);
        eventMessageRequest.setPayload(raceDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE);

        doThrow(new RuntimeException("Database error")).when(raceEntityRepository).save(any(RaceEntity.class));

        raceEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void handleUpdate_ShouldSendErrorResponseWhenRaceNotFound() {
        EventMessageRequest<RaceDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.UPDATE);
        eventMessageRequest.setPayload(raceDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE);

        when(raceEntityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        raceEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void handleDelete_ShouldSendErrorResponseWhenRaceNotFound() {
        EventMessageRequest<String> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.DELETE);
        eventMessageRequest.setPayload(raceDto.getId().toString());
        eventMessageRequest.setSubscriber(Subscriber.RACE);

        when(raceEntityRepository.existsById(any(UUID.class))).thenReturn(false);

        raceEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }
}