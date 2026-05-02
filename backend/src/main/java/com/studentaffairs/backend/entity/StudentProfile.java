package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Entity
@Table(name = "student_profile")
public class StudentProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String studentId; // 学号

    @Column(nullable = false, length = 50)
    private String name; // 姓名

    @Column(length = 255)
    private String avatar; // 照片

    @Column(length = 50)
    private String college; // 学院

    @Column(length = 50)
    private String major; // 专业

    @Column(length = 50)
    private String gradeClass; // 年级班级

    @Column(length = 18)
    private String idCard; // 身份证号码（需AOP脱敏）

    // 非结构化数据降维：将紧急联系人、病史、爱好等存入 JSONB
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> extraInfo;
}
