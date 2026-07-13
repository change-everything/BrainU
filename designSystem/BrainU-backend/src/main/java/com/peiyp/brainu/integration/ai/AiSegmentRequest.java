package com.peiyp.brainu.integration.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiSegmentRequest {
    private String modelPath;
    private String filePath;
    private String modelName;
}
