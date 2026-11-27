package com.zhao.ai.controller;

import com.zhao.ai.service.AIAnalyzer;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIAnalyzer aiAnalyzer;

    public AIController(AIAnalyzer aiAnalyzer) {
        this.aiAnalyzer = aiAnalyzer;
    }

    @PostMapping("/analyze")
    public Map<String, Object> analyzeReport(@RequestBody AnalyzeRequest request) {
        return aiAnalyzer.analyzeReport(
                request.getReportType(),
                request.getLocation(),
                request.getDescription(),
                request.getEmotion()
        );
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "AI分析服务运行正常 - " + System.currentTimeMillis();
    }

    public static class AnalyzeRequest {
        private String reportType;
        private String location;
        private String description;
        private String emotion;

        // getters and setters
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getEmotion() { return emotion; }
        public void setEmotion(String emotion) { this.emotion = emotion; }
    }
}