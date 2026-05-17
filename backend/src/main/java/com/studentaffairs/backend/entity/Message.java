package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false, length = 50)
    private String senderId;

    @Column(name = "sender_role", nullable = false, length = 20)
    private String senderRole; // teacher, student

    @Column(name = "receiver_id", nullable = false, length = 50)
    private String receiverId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "quote_id")
    private Long quoteId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_recalled", nullable = false)
    private Boolean isRecalled = false;

    @Column(name = "sent_time", nullable = false)
    private LocalDateTime sentTime;
}
