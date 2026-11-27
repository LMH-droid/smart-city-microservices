package com.zhao.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 关键：注释掉整个控制器类，使其完全失效
/*
@RestController
public class GatewayController {

    @GetMapping("/health")
    public String healthCheck() {
        return "网关服务运行正常 - " + System.currentTimeMillis();
    }

    @GetMapping("/")
    public String gatewayHome() {
        return "智慧城市微服务网关 - 服务列表: \n" +
                "• 用户服务: http://localhost:8001\n" +
                "• 报告服务: http://localhost:8002\n" +
                "• AI服务: http://localhost:8003\n" +
                "• 数据服务: http://localhost:8004\n" +
                "• 网关服务: http://localhost:8080";
    }
}
*/