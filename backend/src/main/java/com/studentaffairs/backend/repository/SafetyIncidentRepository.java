package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.SafetyIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SafetyIncidentRepository extends JpaRepository<SafetyIncident, Long> {
    List<SafetyIncident> findByOrderByReportTimeDesc();
    List<SafetyIncident> findByLevelOrderByReportTimeDesc(String level);
    long countByStatus(String status);
}
