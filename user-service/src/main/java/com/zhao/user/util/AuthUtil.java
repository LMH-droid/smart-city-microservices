package com.zhao.user.util;

public class AuthUtil {

    public static boolean isGovernmentUser(String userType) {
        return "government".equals(userType) || "admin".equals(userType);
    }

    public static boolean isCitizenUser(String userType) {
        return "citizen".equals(userType);
    }

    public static void checkGovernmentAccess(String userType) {
        if (!isGovernmentUser(userType)) {
            throw new SecurityException("权限不足：需要政府工作人员权限");
        }
    }

    public static void checkCitizenAccess(String userType) {
        if (!isCitizenUser(userType)) {
            throw new SecurityException("权限不足：需要市民权限");
        }
    }

    public static String getUserRoleDescription(String userType) {
        switch (userType) {
            case "admin":
                return "系统管理员";
            case "government":
                return "政府工作人员";
            case "citizen":
                return "市民用户";
            default:
                return "未知用户";
        }
    }
}