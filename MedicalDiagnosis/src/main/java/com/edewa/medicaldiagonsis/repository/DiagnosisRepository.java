package com.edewa.medicaldiagonsis.repository;

import com.edewa.medicaldiagonsis.entity.DiagnosisResult;
import org.springframework.data.repository.CrudRepository;

public interface DiagnosisRepository extends CrudRepository<DiagnosisResult, Long> {
}
