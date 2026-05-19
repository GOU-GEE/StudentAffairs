package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "youth_award")
public class YouthAward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentId;
    private String studentName;
    private String awardName;
    private String level;
    private String category;
    private String awardTime;
    private String status; // PENDING, APPROVED, REJECTED
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
