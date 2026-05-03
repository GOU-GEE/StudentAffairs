package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.StudentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentApplicationRepository extends JpaRepository<StudentApplication, Long> {
    List<StudentApplication> findByStudentIdOrderByApplyTimeDesc(String studentId);
    List<StudentApplication> findByStatusOrderByApplyTimeDesc(String status);
    List<StudentApplication> findAllByOrderByApplyTimeDesc();
}
