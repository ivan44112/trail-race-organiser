package com.intellexi.raqs.persistence.domain;

import com.intellexi.raqs.utils.Distance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "race")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RaceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "distance", nullable = false)
    private Distance distance;
}

