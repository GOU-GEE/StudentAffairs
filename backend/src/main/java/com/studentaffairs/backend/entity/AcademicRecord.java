package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "academic_record")
public class AcademicRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "course_type", length = 20)
    private String courseType; // 必修/选修

    @Column(nullable = false)
    private Float score;

    @Column(nullable = false)
    private Float credit;

    @Column(nullable = false, length = 20)
    private String semester; // 如 2024-2025-1

    // 是否触发学业预警（GPA < 2.0 或 挂科）
    @Column(name = "is_warning")
    private Boolean isWarning;
}
