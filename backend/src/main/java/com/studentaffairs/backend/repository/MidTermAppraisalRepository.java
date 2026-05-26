package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.MidTermAppraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MidTermAppraisalRepository extends JpaRepository<MidTermAppraisal, Long> {
    List<MidTermAppraisal> findByStudentId(String studentId);
    List<MidTermAppraisal> findByStudentIdOrderByIdDesc(String studentId);
    List<MidTermAppraisal> findAllByOrderBySubmitTimeDesc();
    List<MidTermAppraisal> findByStatusOrderBySubmitTimeDesc(String status);
}
