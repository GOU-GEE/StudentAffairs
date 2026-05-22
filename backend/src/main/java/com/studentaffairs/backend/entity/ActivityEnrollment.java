package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_enrollment")
public class ActivityEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String studentId;
    private Long activityId;
    private LocalDateTime enrollTime;
}
