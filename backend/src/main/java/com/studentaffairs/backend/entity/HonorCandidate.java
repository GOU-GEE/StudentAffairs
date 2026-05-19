package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "honor_candidate")
public class HonorCandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private String studentId;
    private String studentName;
    private String className;
    private Double gpa;
    private String status; // 候选, 已入选
}
