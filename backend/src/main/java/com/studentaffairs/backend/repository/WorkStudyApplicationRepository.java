package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.WorkStudyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkStudyApplicationRepository extends JpaRepository<WorkStudyApplication, Long> {
    boolean existsByStudentIdAndJobId(String studentId, Long jobId);

    List<WorkStudyApplication> findByOrderByApplyTimeDesc();

    List<WorkStudyApplication> findByJobId(Long jobId);

    long countByStatus(String status);
}
