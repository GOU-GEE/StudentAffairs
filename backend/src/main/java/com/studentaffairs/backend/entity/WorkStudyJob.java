package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "work_study_job")
public class WorkStudyJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title; // 岗位名称

    @Column(nullable = false, length = 50)
    private String department; // 用人部门

    @Column(nullable = false)
    private Integer quota; // 总名额

    @Column(nullable = false)
    private Integer currentCount; // 当前已招募人数

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, FULL

    @Version
    private Long version; // 乐观锁字段，防止高并发超卖
}
