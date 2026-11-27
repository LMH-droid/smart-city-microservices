package com.zhao.report.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "citizen_reports")
public class CitizenReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_type", nullable = false, length = 20)
    private String reportType;

    @Column(nullable = false, length = 100)
    private String location;

    @Lob
    private String description;

    @Column(nullable = false, length = 10)
    private String emotion;

    @Column(name = "created_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime = new Date();

    @Column(length = 10)
    private String status = "待处理";

    @Column(name = "assigned_admin", length = 50)
    private String assignedAdmin;

    @Column(name = "assigned_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedTime;

    @Column(name = "solved_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date solvedTime;

    @Lob
    @Column(name = "solution_text")
    private String solutionText;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "assigned_department", length = 50)
    private String assignedDepartment;

    @Column(length = 20)
    private String progress = "待受理";

    @Column(name = "citizen_rating")
    private Integer citizenRating;

    @Lob
    @Column(name = "citizen_feedback")
    private String citizenFeedback;

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    // Getter and Setter 方法 (必须全部生成)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAssignedAdmin() { return assignedAdmin; }
    public void setAssignedAdmin(String assignedAdmin) { this.assignedAdmin = assignedAdmin; }
    public Date getAssignedTime() { return assignedTime; }
    public void setAssignedTime(Date assignedTime) { this.assignedTime = assignedTime; }
    public Date getSolvedTime() { return solvedTime; }
    public void setSolvedTime(Date solvedTime) { this.solvedTime = solvedTime; }
    public String getSolutionText() { return solutionText; }
    public void setSolutionText(String solutionText) { this.solutionText = solutionText; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public String getAssignedDepartment() { return assignedDepartment; }
    public void setAssignedDepartment(String assignedDepartment) { this.assignedDepartment = assignedDepartment; }
    public String getProgress() { return progress; }
    public void setProgress(String progress) { this.progress = progress; }
    public Integer getCitizenRating() { return citizenRating; }
    public void setCitizenRating(Integer citizenRating) { this.citizenRating = citizenRating; }
    public String getCitizenFeedback() { return citizenFeedback; }
    public void setCitizenFeedback(String citizenFeedback) { this.citizenFeedback = citizenFeedback; }
    public Boolean getIsArchived() { return isArchived; }
    public void setIsArchived(Boolean isArchived) { this.isArchived = isArchived; }
}