package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "announcement")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "publisher_name", nullable = false, length = 50)
    private String publisherName;

    @Column(nullable = false, length = 20)
    private String type; // NOTICE, URGENT, EVENT

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "is_pinned")
    private Boolean isPinned;
}
