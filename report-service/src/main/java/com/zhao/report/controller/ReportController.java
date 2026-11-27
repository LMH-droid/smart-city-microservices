package com.zhao.report.controller;

import com.zhao.report.entity.CitizenReport;
import com.zhao.report.dao.CitizenReportRepository; // 注意：导入已改为Repository
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    // 1. 字段已修改为Repository
    private final CitizenReportRepository reportRepository;

    // 2. 构造方法参数已修改为Repository
    public ReportController(CitizenReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 获取所有报告
    @GetMapping
    public List<CitizenReport> getAllReports() {
        // 3. 方法调用已修改
        return reportRepository.findAll(); // 替换原有的 reportDao.getAllReports()
    }

    // 根据ID获取报告
    @GetMapping("/{id}")
    public CitizenReport getReportById(@PathVariable Long id) {
        // 4. 注意：findById返回Optional，需要处理null情况
        return reportRepository.findById(id).orElse(null); // 替换原有的 reportDao.getReportById(id)
    }

    // 提交新报告
    @PostMapping
    public Map<String, Object> submitReport(@RequestBody ReportRequest request) {
        CitizenReport report = new CitizenReport(); // 5. 注意：已移除构造函数，需使用setter
        report.setReportType(request.getReportType());
        report.setLocation(request.getLocation());
        report.setDescription(request.getDescription());
        report.setEmotion(request.getEmotion());

        // 6. 方法调用已修改
        CitizenReport savedReport = reportRepository.save(report); // 替换原有的 reportDao.createReport(report)

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "报告提交成功");
        response.put("report", savedReport);

        return response;
    }

    // 更新报告状态
    @PutMapping("/{id}/status")
    public Map<String, Object> updateReportStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        // 7. 使用findById获取Optional
        CitizenReport report = reportRepository.findById(id).orElse(null);
        if (report != null) {
            report.setStatus(statusUpdate.get("status"));
            // 8. 更新操作也使用save方法
            reportRepository.save(report); // 替换原有的 reportDao.updateReport(id, report)

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "状态更新成功");
            return response;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "报告不存在");
        return response;
    }

    // 删除报告
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteReport(@PathVariable Long id) {
        // 9. 检查报告是否存在后再删除
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id); // 替换原有的 reportDao.deleteReport(id)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "报告删除成功");
            return response;
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "报告不存在");
            return response;
        }
    }

    // 根据状态获取报告
    @GetMapping("/status/{status}")
    public List<CitizenReport> getReportsByStatus(@PathVariable String status) {
        // 10. 使用Repository的自定义查询方法
        return reportRepository.findByStatus(status); // 替换原有的 reportDao.getReportsByStatus(status)
    }

    // 根据部门获取报告
    @GetMapping("/department/{department}")
    public List<CitizenReport> getReportsByDepartment(@PathVariable String department) {
        // 11. 使用Repository的自定义查询方法
        return reportRepository.findByAssignedDepartment(department); // 替换原有的 reportDao.getReportsByDepartment(department)
    }

    // 健康检查
    @GetMapping("/health")
    public String healthCheck() {
        // 12. 使用count()方法获取总数
        return "报告服务运行正常 - " + System.currentTimeMillis() + "，总报告数: " + reportRepository.count();
    }

    // 请求类 (保持不变)
    public static class ReportRequest {
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