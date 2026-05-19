package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "second_classroom_record")
public class SecondClassroomRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentId;
    private String studentName;
    private String className;
    private Integer categoryIndex; // 0-5
    private Integer hours;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    private LocalDateTime grantTime;
}
