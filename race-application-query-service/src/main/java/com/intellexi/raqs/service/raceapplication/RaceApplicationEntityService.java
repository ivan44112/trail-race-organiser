package com.intellexi.raqs.service.raceapplication;

import com.intellexi.raqs.service.dto.EventMessageRequest;
import com.intellexi.raqs.persistence.domain.RaceApplicationEntity;

import java.util.List;
import java.util.UUID;

public interface RaceApplicationEntityService {
    List<RaceApplicationEntity> getAllRaceApplications();
    RaceApplicationEntity getApplicationById(UUID id);
    void receiveAndProcessEvent(EventMessageRequest<?> eventMessageRequest);
}
