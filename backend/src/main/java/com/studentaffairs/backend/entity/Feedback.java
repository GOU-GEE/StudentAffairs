package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(length = 2000)
    private String reply;

    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, REPLIED

    @PrePersist
    public void prePersist() {
        if (this.createTime == null) this.createTime = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }
}
