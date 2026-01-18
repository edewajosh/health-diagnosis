package com.edewa.medicaldiagonsis.service;

import com.edewa.medicaldiagonsis.configs.ApiMedicConfigs;
import com.edewa.medicaldiagonsis.entity.DiagnosisResult;
import com.edewa.medicaldiagonsis.models.DiagnosisRequest;
import com.edewa.medicaldiagonsis.models.Symptoms;
import com.edewa.medicaldiagonsis.models.diagnosis.Response;
import com.edewa.medicaldiagonsis.repository.DiagnosisRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class DiagnosisService {
    static final Logger LOGGER = LoggerFactory.getLogger(DiagnosisService.class);
    private final DiagnosisRepository repository;
    private final RestClient restClient;
    private final ApiMedicConfigs apiMedicConfigs;
    final ObjectMapper mapper;

    public DiagnosisService(DiagnosisRepository repository,
                            ApiMedicConfigs apiMedicConfigs, ObjectMapper mapper) {
        this.repository = repository;
        this.apiMedicConfigs = apiMedicConfigs;
        this.mapper = mapper;
        this.restClient = RestClient.builder()
                .baseUrl(apiMedicConfigs.authUrl().substring(0, apiMedicConfigs.authUrl().lastIndexOf('/')))
                .build();
    }

    private String getToken() {
        String md5Pass = DigestUtils.md5Hex(apiMedicConfigs.password());
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_MD5, md5Pass).hmacHex(apiMedicConfigs.authUrl());
        String authString = apiMedicConfigs.username() + ":" + Base64.getEncoder().encodeToString(hmac.getBytes());
        String authHeader = "Bearer " + authString;

        try {
            return Objects.requireNonNull(restClient.post()
                            .uri(apiMedicConfigs.authUrl())
                            .header("Authorization", authHeader)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                                throw new RuntimeException("Authentication failed: " + res.getStatusCode());
                            })
                            .body(TokenResponse.class))
                    .token();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to obtain ApiMedic token", e);
        }
    }

    public List<Symptoms> getSymptoms() {
        LOGGER.info("Fetching all the symptoms...");

        if (apiMedicConfigs.mockEnabled()) {
            LOGGER.info("Mock enabled - returning mock symptoms");
            String mockJson = """
                    [
                      {"id": "10", "name": "Headache"},
                      {"id": "15", "name": "Fever"},
                      {"id": "20", "name": "Cough"},
                      {"id": "25", "name": "Sore throat"},
                      {"id": "30", "name": "Nausea"},
                      {"id": "35", "name": "Fatigue"},
                      {"id": "40", "name": "Dizziness"},
                      {"id": "45", "name": "Shortness of breath"},
                      {"id": "50", "name": "Chest pain"},
                      {"id": "55", "name": "Abdominal pain"}
                    ]
                    """;
            try {
                return mapper.readValue(mockJson, new TypeReference<>() {
                });
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse mock symptoms", e);
            }
        } else {
            String token = getToken();
            LOGGER.info("Mock disabled - fetching symptoms from API");
            return restClient.get()
                    .uri(apiMedicConfigs.baseUrl() + "/symptoms?language={lang}", token, apiMedicConfigs.language())
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .exchange((_, res) -> {
                        LOGGER.info("Symptoms response code: {} Raw response body: {}", res.getStatusCode(), res.getBody());
                        if (res.getStatusCode().isError()) {
                            throw new RuntimeException("An error occurred on symptoms fetching");
                        } else {
                            return mapper.readValue(res.getBody(), new TypeReference<>() {
                            });
                        }
                    });

        }
    }

    public List<Response> getDiagnosis(DiagnosisRequest request) {
        String requestBody = mapper.writeValueAsString(request);
        LOGGER.info("Diagnosis request body: {}", requestBody);
        if (apiMedicConfigs.mockEnabled()) {
            LOGGER.info("Mock enabled - returning mock diagnosis");
            try {
                return mapper.readValue(mockDiagnosisResponse(), new TypeReference<>() {
                });
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse mock diagnosis", e);
            }
        } else {
            LOGGER.info("Mock disabled - fetching diagnosis from API");
            String token = getToken();
            return restClient.post()
                    .uri("/api/diagnosis")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bear " + token)
                    .body(requestBody)
                    .exchange((_, res) -> {
                        LOGGER.info("Diagnosis response code: {} Raw response body: {}", res.getStatusCode(), res.getBody());
                        if (res.getStatusCode().isError()) {
                            throw new RuntimeException("An error occurred on diagnosis");
                        } else {
                            return mapper.readValue(res.getBody(), new TypeReference<>() {
                            });
                        }
                    });
        }

    }

    public DiagnosisResult saveDiagnosis(DiagnosisResult result) {
        LOGGER.info("Saving diagnosis result for user...");
        return repository.save(result);
    }

    private record TokenResponse(String Token, long ValidThrough) {
        String token() {
            return Token;
        }
    }

    String mockDiagnosisResponse() {
        return """
                [
                   {
                     "issue": {
                       "id": 100,
                       "name": "Urinary Tract Infection (UTI)",
                       "accuracy": 85,
                       "icd": "N39.0",
                       "icdName": "Urinary tract infection, site not specified",
                       "profName": "Urinary tract infection"
                     },
                     "specialisation": [
                       {
                         "id": 5,
                         "name": "General practitioner",
                         "specId": 5,
                         "specialistName": "General medicine"
                       }
                     ]
                   },
                   {
                     "issue": {
                       "id": 150,
                       "name": "Dehydration",
                       "accuracy": 60,
                       "icd": "E86.0",
                       "icdName": "Volume depletion",
                       "profName": "Dehydration"
                     },
                     "specialisation": [
                       {
                         "id": 10,
                         "name": "Internal medicine",
                         "specId": 10,
                         "specialistName": "Internal medicine"
                       }
                     ]
                   },
                   {
                     "issue": {
                       "id": 210,
                       "name": "Liver Disease (early stages)",
                       "accuracy": 30,
                       "icd": "K72.90",
                       "icdName": "Hepatic failure, unspecified without coma",
                       "profName": "Hepatic failure"
                     },
                     "specialisation": [
                       {
                         "id": 25,
                         "name": "Hepatologist",
                         "specId": 25,
                         "specialistName": "Hepatology"
                       }
                     ]
                   }
                 ]
                """;
    }
}