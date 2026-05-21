package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStudentIdOrderByCreateTimeDesc(String studentId);
    List<Feedback> findByOrderByCreateTimeDesc();
}
