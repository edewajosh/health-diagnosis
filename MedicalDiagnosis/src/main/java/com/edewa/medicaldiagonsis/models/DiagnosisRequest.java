package com.edewa.medicaldiagonsis.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class DiagnosisRequest {
    private String gender;
    private int yearOfBirth;
    private String patientName;
    private ArrayList <Symptoms> symptoms;
}
