package com.example.egovbus.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Bus bus;

    private double latitude;
    private double longitude;
    private double speedKph;

    private Instant timestamp;
}
