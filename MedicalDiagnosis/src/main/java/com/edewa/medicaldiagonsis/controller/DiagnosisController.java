package com.edewa.medicaldiagonsis.controller;

import com.edewa.medicaldiagonsis.models.DiagnosisRequest;
import com.edewa.medicaldiagonsis.entity.DiagnosisResult;
import com.edewa.medicaldiagonsis.models.Symptoms;
import com.edewa.medicaldiagonsis.models.diagnosis.Response;
import com.edewa.medicaldiagonsis.service.DiagnosisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class DiagnosisController {
    private final DiagnosisService service;
    public DiagnosisController(DiagnosisService service) {
        this.service = service;
    }

    @GetMapping("/symptoms")
    public ResponseEntity<List<Symptoms>> getSymptoms() {
        return new ResponseEntity<>(service.getSymptoms(), HttpStatus.OK);
    }

    @PostMapping("/diagnosis")
    public ResponseEntity<List<Response>> getDiagnosis(@RequestBody DiagnosisRequest request) {
        return new ResponseEntity<>(service.getDiagnosis(request), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<DiagnosisResult> save(@RequestBody DiagnosisResult result) {
        return new ResponseEntity<>(service.saveDiagnosis(result), HttpStatus.CREATED);
    }
}
