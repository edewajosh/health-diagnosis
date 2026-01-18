package com.edewa.medicaldiagonsis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
@Getter
public class DiagnosisResult {
    @Id
    @GeneratedValue
    private Long id;
    private String symptoms;
    private String gender;
    private int yearOfBirth;
    private String diagnosis;
    private boolean isValid;
    private LocalDateTime timestamp = LocalDateTime.now();
}
