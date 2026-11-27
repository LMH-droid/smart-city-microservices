package com.zhao.report.dao;

import com.zhao.report.entity.CitizenReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CitizenReportRepository extends JpaRepository<CitizenReport, Long> {
    List<CitizenReport> findByStatus(String status);
    List<CitizenReport> findByAssignedDepartment(String department);
    List<CitizenReport> findByProgress(String progress);
}