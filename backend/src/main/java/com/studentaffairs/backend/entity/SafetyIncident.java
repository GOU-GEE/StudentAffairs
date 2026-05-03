package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "safety_incident")
public class SafetyIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", length = 50)
    private String studentId;

    @Column(name = "student_name", length = 50)
    private String studentName;

    @Column(nullable = false, length = 50)
    private String type; // FIGHT, MISSING, MENTAL, MEDICAL, VIOLATION

    @Column(nullable = false, length = 10)
    private String level; // HIGH, MEDIUM, LOW

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 20)
    private String status; // OPEN, PROCESSING, CLOSED

    @Column(name = "report_time")
    private LocalDateTime reportTime;

    @Column(name = "handler_name", length = 50)
    private String handlerName;
}
