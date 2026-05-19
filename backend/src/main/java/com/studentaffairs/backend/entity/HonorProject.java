package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "honor_project")
public class HonorProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Integer quota;
    private Integer applied = 0;
    private String deadline;
    private String status; // 进行中, 已关闭
}
