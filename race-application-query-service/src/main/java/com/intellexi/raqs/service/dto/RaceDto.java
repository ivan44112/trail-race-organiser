package com.intellexi.raqs.service.dto;

import com.intellexi.raqs.utils.Distance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RaceDto {
    private UUID id;
    private String name;
    private Distance distance;
}
