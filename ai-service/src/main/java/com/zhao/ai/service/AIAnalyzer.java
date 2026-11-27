package com.zhao.ai.service;

import com.zhao.ai.util.DepartmentConstants;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIAnalyzer {
    // 系数设置
    private static final double ALPHA = 0.25;   // 空间影响系数
    private static final double BETA = 0.20;    // 人群影响系数
    private static final double GAMMA = 0.30;   // 时间紧迫系数
    private static final double DELTA = 0.15;   // 历史频次系数
    private static final double EPSILON = 0.10; // 舆情关注系数

    // 区域人口密度系数（模拟数据）
    private static final Map<String, Double> AREA_DENSITY = Map.of(
            "渝中区", 1.5, "江北区", 1.3, "南岸区", 1.2,
            "沙坪坝", 1.1, "九龙坡", 1.0, "渝北区", 0.9, "巴南区", 0.8
    );

    // 事件历史记录
    private static final Map<String, List<Date>> eventHistory = new ConcurrentHashMap<>();

    // 部门映射 - 使用HashMap避免参数限制
    private static final Map<String, String> REPORT_TYPE_TO_DEPARTMENT = new HashMap<>();

    static {
        REPORT_TYPE_TO_DEPARTMENT.put("交通拥堵", "交通管理局");
        REPORT_TYPE_TO_DEPARTMENT.put("违章停车", "交通管理局");
        REPORT_TYPE_TO_DEPARTMENT.put("空气异味", "环境保护局");
        REPORT_TYPE_TO_DEPARTMENT.put("水质污染", "环境保护局");
        REPORT_TYPE_TO_DEPARTMENT.put("道路损坏", "市政设施局");
        REPORT_TYPE_TO_DEPARTMENT.put("路灯故障", "市政设施局");
        REPORT_TYPE_TO_DEPARTMENT.put("占道经营", "城管执法局");
        REPORT_TYPE_TO_DEPARTMENT.put("违章建筑", "城管执法局");
        REPORT_TYPE_TO_DEPARTMENT.put("安全隐患", "公安消防局");
        REPORT_TYPE_TO_DEPARTMENT.put("消防通道堵塞", "公安消防局");
        REPORT_TYPE_TO_DEPARTMENT.put("垃圾堆积", "卫生健康局");
        REPORT_TYPE_TO_DEPARTMENT.put("公共卫生", "卫生健康局");
        REPORT_TYPE_TO_DEPARTMENT.put("设施损坏", "教育文化局");
    }

    public Map<String, Object> analyzeReport(String reportType, String location, String description, String emotion) {
        // 1. 确定分配部门
        String actualType = determineReportType(description);
        String department = DepartmentConstants.getDepartmentByReportType(actualType);
        // 2. 计算风险指数
        double A = calculateSpatialImpact(location);
        double B = calculatePopulationImpact(description);
        double C = calculateTimeUrgency(reportType);
        double D = calculateHistoricalFrequency(reportType, location);
        double E = calculatePublicConcern(emotion, description);

        double riskScore = Math.sqrt(Math.pow(ALPHA * A, 2) + Math.pow(BETA * B, 2) + Math.pow(GAMMA * C, 2))
                * Math.log10(DELTA * D + 1) * (1 + EPSILON * E);

        int finalScore = (int) Math.min(Math.max(riskScore, 0), 100);
        String riskLevel = determineRiskLevel(finalScore);

        // 3. 生成处理建议
        String suggestions = generateSuggestions(reportType, riskLevel, finalScore);

        // 4. 返回分析结果
        Map<String, Object> result = new HashMap<>();
        result.put("department", department);
        result.put("riskScore", finalScore);
        result.put("riskLevel", riskLevel);
        result.put("suggestions", suggestions);
        result.put("analyzedType", actualType);
        result.put("formulaDetails", Map.of(
                "spatialImpact", A,
                "populationImpact", B,
                "timeUrgency", C,
                "historicalFrequency", D,
                "publicConcern", E
        ));

        return result;
    }

    private String determineReportType(String description) {
        if (description.contains("消防") || description.contains("安全") || description.contains("隐患")) {
            return "安全隐患";
        }
        if (description.contains("拥堵") || description.contains("交通") || description.contains("车辆")) {
            return "交通拥堵";
        }
        if (description.contains("空气") || description.contains("污染") || description.contains("异味")) {
            return "空气异味";
        }
        if (description.contains("道路") || description.contains("损坏") || description.contains("坑洼")) {
            return "道路损坏";
        }
        if (description.contains("垃圾") || description.contains("卫生")) {
            return "垃圾堆积";
        }
        return "其他";
    }

    // 其他辅助方法保持不变...
    private double calculateSpatialImpact(String location) {
        double radius = 0.5 + Math.random() * 2.0;
        double densityCoefficient = AREA_DENSITY.getOrDefault(extractArea(location), 1.0);
        return radius * densityCoefficient * 20;
    }

    private double calculatePopulationImpact(String description) {
        int basePopulation = 100 + (int)(Math.random() * 900);
        double sensitiveRatio = 0.1 + Math.random() * 0.4;
        return basePopulation * sensitiveRatio;
    }

    private double calculateTimeUrgency(String reportType) {
        double decayFactor = getTimeDecayFactor(reportType);
        int responseTimeLimit = getResponseTimeLimit(reportType);
        return decayFactor * responseTimeLimit;
    }

    private double calculateHistoricalFrequency(String reportType, String location) {
        String key = reportType + ":" + extractArea(location);
        List<Date> history = eventHistory.getOrDefault(key, new ArrayList<>());
        history.add(new Date());
        eventHistory.put(key, history);
        long frequency = history.stream()
                .filter(date -> System.currentTimeMillis() - date.getTime() < 30L * 24 * 60 * 60 * 1000)
                .count();
        double growthRate = calculateGrowthRate(history);
        return frequency * (1 + growthRate);
    }

    private double calculatePublicConcern(String emotion, String description) {
        double emotionWeight = Map.of("愤怒", 1.5, "不满", 1.2, "一般", 1.0, "满意", 0.8)
                .getOrDefault(emotion, 1.0);
        return emotionWeight * (1 + description.length() * 0.001);
    }

    private String determineRiskLevel(int score) {
        if (score >= 95) return "特大风险";
        else if (score >= 85) return "重大风险";
        else if (score >= 70) return "较高风险";
        else if (score >= 50) return "中度风险";
        else if (score >= 30) return "一般风险";
        else return "低风险";
    }

    private String generateSuggestions(String reportType, String riskLevel, int score) {
        List<String> suggestions = new ArrayList<>();

        if (score >= 85) {
            suggestions.add("立即启动应急响应机制");
            suggestions.add("多部门联合处置");
            suggestions.add("实时上报指挥中心");
        } else if (score >= 70) {
            suggestions.add("2小时内现场处置");
            suggestions.add("调配专业处置队伍");
        } else if (score >= 50) {
            suggestions.add("24小时内处理完成");
            suggestions.add("标准流程处置");
        } else {
            suggestions.add("按常规流程处理");
        }

        switch(reportType) {
            case "交通拥堵":
                suggestions.add("调度交警疏导交通");
                suggestions.add("优化信号灯配时");
                break;
            case "空气异味":
                suggestions.add("环境监测人员现场检测");
                suggestions.add("排查污染源");
                break;
            case "设施损坏":
                suggestions.add("市政维修队伍现场勘查");
                suggestions.add("设置安全警示标志");
                break;
        }

        return String.join("；", suggestions);
    }

    private String extractArea(String location) {
        for (String area : AREA_DENSITY.keySet()) {
            if (location.contains(area)) return area;
        }
        return "其他区域";
    }

    private double getTimeDecayFactor(String reportType) {
        return Map.of("安全隐患", 1.5, "消防通道堵塞", 1.3)
                .getOrDefault(reportType, 1.0);
    }

    private int getResponseTimeLimit(String reportType) {
        return Map.of("安全隐患", 2, "消防通道堵塞", 2, "交通拥堵", 4)
                .getOrDefault(reportType, 24);
    }

    private double calculateGrowthRate(List<Date> history) {
        if (history.size() < 2) return 0;
        return Math.random() * 0.3;
    }
}