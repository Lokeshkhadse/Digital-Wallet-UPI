package com.example.Kyc_Service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SarvamSpeechToTextService {

    @Value("${sarvam.api-key}")
    private String apiKey;

    @Value("${sarvam.base-url}")
    private String baseUrl;
    // https://api.sarvam.ai/speech-to-text

    private final RestTemplate restTemplate;

    public String convertSpeechToText(MultipartFile audioFile) {

        try {

            HttpHeaders headers = new HttpHeaders();

            // ✅ correct auth
            headers.set("api-subscription-key", apiKey);

            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("file", new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            });

            body.add("model", "saaras:v3");
            body.add("mode", "transcribe");

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(baseUrl, request, String.class);

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("STT Failed: " + e.getMessage());
        }
    }
}