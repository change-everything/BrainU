package com.peiyp.brainu.integration.ai;

import com.peiyp.brainu.config.BrainUAiProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client for the Python inference service.
 */
@Component
public class AiInferenceClient {

    private final RestTemplate restTemplate;
    private final BrainUAiProperties properties;

    public AiInferenceClient(
            @Qualifier("brainuAiRestTemplate") RestTemplate restTemplate,
            BrainUAiProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public String segment(String modelPath, String filePath, String modelName) {
        String endpoint = trimTrailingSlash(properties.getBaseUrl()) + "/segment";
        AiSegmentRequest payload = new AiSegmentRequest(modelPath, filePath, modelName);

        try {
            ResponseEntity<AiSegmentResponse> response = restTemplate.postForEntity(
                    endpoint, payload, AiSegmentResponse.class);
            AiSegmentResponse body = response.getBody();
            if (body == null || !StringUtils.hasText(body.getImagePath())) {
                throw new IllegalStateException("Python inference service returned an empty imagePath");
            }
            return body.getImagePath();
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException(
                    "Python inference service returned HTTP " + e.getRawStatusCode()
                            + ": " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new IllegalStateException(
                    "Unable to call Python inference service at " + endpoint, e);
        }
    }

    private String trimTrailingSlash(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException("brainu.ai.base-url must not be empty");
        }
        return value.replaceAll("/+$", "");
    }
}
