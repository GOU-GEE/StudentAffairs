package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String date;
    private String location;
    private Integer participants = 0;
    private Integer maxParticipants;
    private String status; // 报名中, 进行中, 已结束
    private Double credits;
    private String level; // 校级, 院级, 班级
    private String bgGradient; // Optional UI info

    // New fields for ActivityCenter redesign
    private String coverImage;
    private String enrollTime;
    private String timeDetail;
    private String rangeName; // 参与范围
    private String leaveSupport; // 活动请假
    private String creditType; // 学分设置
    private String enrollLimit; // 报名人数 (UI representation)
}
