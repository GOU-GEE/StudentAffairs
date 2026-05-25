package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.ActivityEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityEnrollmentRepository extends JpaRepository<ActivityEnrollment, Long> {
    List<ActivityEnrollment> findByActivityId(Long activityId);
    List<ActivityEnrollment> findByStudentId(String studentId);
    int countByActivityId(Long activityId);
    boolean existsByActivityIdAndStudentId(Long activityId, String studentId);
}
