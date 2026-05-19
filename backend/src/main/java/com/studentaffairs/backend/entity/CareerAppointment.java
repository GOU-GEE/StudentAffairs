package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "career_appointment")
public class CareerAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    @Column(name = "teacher_id", length = 50)
    private String teacherId;

    @Column(name = "appointment_time", length = 100)
    private String appointmentTime;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(length = 20)
    private String status; // PENDING / ACCEPTED / REJECTED

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
