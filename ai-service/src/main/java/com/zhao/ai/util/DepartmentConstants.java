package com.zhao.ai.util;

import java.util.HashMap;
import java.util.Map;

public class DepartmentConstants {

    public static final Map<String, String> REPORT_TYPE_TO_DEPARTMENT = new HashMap<>();

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

    public static String getDepartmentByReportType(String reportType) {
        return REPORT_TYPE_TO_DEPARTMENT.getOrDefault(reportType, "综合管理局");
    }

    public static boolean isValidDepartment(String department) {
        return REPORT_TYPE_TO_DEPARTMENT.containsValue(department);
    }

    public static String getDefaultDepartment() {
        return "综合管理局";
    }
}