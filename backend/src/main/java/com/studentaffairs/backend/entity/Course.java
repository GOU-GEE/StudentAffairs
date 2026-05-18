package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String type; // 必修/选修

    @Column(nullable = false)
    private Float credit;

    @Column(nullable = false)
    private Integer hours;

    @Column(length = 20)
    private String semester;

    @Column(length = 50)
    private String teacher;

    @Column(name = "class_name", length = 100)
    private String className;
}
