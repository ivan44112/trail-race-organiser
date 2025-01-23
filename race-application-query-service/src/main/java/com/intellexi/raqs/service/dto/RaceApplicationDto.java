package com.intellexi.raqs.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RaceApplicationDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String club;
    private UUID raceId;
}
