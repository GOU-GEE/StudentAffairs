package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId; // "youth" or studentId (e.g., "202301042")

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String tag; // e.g. "待审核", "已通过", "已驳回"

    @Column(name = "tag_style", length = 100)
    private String tagStyle; // e.g. Tailwind style or class name representation

    @Column(length = 200)
    private String path; // Redirect routing path

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
