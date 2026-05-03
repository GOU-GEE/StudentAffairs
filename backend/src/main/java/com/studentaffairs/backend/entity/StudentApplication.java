package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "student_application")
public class StudentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    /**
     * 申请类型:
     * SCHOLARSHIP    - 奖学金
     * FINANCIAL_AID  - 助学金
     * WORK_STUDY     - 勤工助学
     * LEAVE          - 请假
     * POVERTY        - 困难认定
     */
    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    /**
     * 审批状态状态机:
     * PENDING -> APPROVED
     * PENDING -> REJECTED
     */
    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "apply_time")
    private LocalDateTime applyTime;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;

    @Column(name = "reviewer_name", length = 50)
    private String reviewerName;
}
