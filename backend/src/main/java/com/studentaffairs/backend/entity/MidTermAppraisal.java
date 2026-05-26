package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mid_term_appraisal")
public class MidTermAppraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "thought_performance", length = 20)
    private String thoughtPerformance;

    @Column(name = "academic_performance", length = 20)
    private String academicPerformance;

    @Column(name = "overall_performance", length = 20)
    private String overallPerformance;

    @Column(name = "self_assessment", columnDefinition = "TEXT")
    private String selfAssessment;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "status", length = 20)
    private String status; // DRAFT, SUBMITTED
}
