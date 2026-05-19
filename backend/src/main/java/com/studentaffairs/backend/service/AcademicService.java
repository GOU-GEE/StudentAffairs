package com.studentaffairs.backend.service;

import com.studentaffairs.backend.entity.AcademicRecord;
import com.studentaffairs.backend.repository.AcademicRecordRepository;
import com.studentaffairs.backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AcademicService {

    private final AcademicRecordRepository recordRepository;
    private final CourseRepository courseRepository;

    public AcademicService(AcademicRecordRepository recordRepository, CourseRepository courseRepository) {
        this.recordRepository = recordRepository;
        this.courseRepository = courseRepository;
    }

    /** 获取所有预警学生 */
    public List<AcademicRecord> getWarningRecords() {
        return recordRepository.findByIsWarningTrue();
    }

    /** 获取某学生所有成绩 */
    public List<AcademicRecord> getStudentRecords(String studentId) {
        return recordRepository.findByStudentId(studentId);
    }

    /** 学业预警引擎：扫描全库，自动标记挂科 & 低分学生 */
    public int runWarningEngine() {
        List<AcademicRecord> all = recordRepository.findAll();
        
        // 按学生分组计算均分
        Map<String, List<AcademicRecord>> byStudent = all.stream()
                .collect(Collectors.groupingBy(AcademicRecord::getStudentId));

        int warningCount = 0;
        List<AcademicRecord> toUpdate = new ArrayList<>();

        for (Map.Entry<String, List<AcademicRecord>> entry : byStudent.entrySet()) {
            List<AcademicRecord> records = entry.getValue();

            // 计算加权平均分
            double totalCredit = records.stream().mapToDouble(r -> r.getCredit()).sum();
            double weightedSum = records.stream().mapToDouble(r -> r.getScore() * r.getCredit()).sum();
            double gpa = totalCredit > 0 ? weightedSum / totalCredit : 0;

            // 预警规则：均分 < 60 或 有任意课程不及格（score < 60）
            boolean anyFail = records.stream().anyMatch(r -> r.getScore() < 60);
            boolean isWarning = gpa < 65 || anyFail;

            for (AcademicRecord r : records) {
                if (!Objects.equals(r.getIsWarning(), isWarning)) {
                    r.setIsWarning(isWarning);
                    toUpdate.add(r);
                }
            }
            if (isWarning) warningCount++;
        }

        recordRepository.saveAll(toUpdate);
        return warningCount;
    }

    /** 班级成绩分布（用于前端柱状图）*/
    public Map<String, Object> getClassScoreDistribution(String className) {
        List<AcademicRecord> records = recordRepository.findByClassName(className);

        // 分数段统计: [0-60), [60-70), [70-80), [80-90), [90-100]
        int[] buckets = new int[5];
        for (AcademicRecord r : records) {
            float s = r.getScore();
            if (s < 60) buckets[0]++;
            else if (s < 70) buckets[1]++;
            else if (s < 80) buckets[2]++;
            else if (s < 90) buckets[3]++;
            else buckets[4]++;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", List.of("不及格(<60)", "及格(60-70)", "中等(70-80)", "良好(80-90)", "优秀(90+)"));
        result.put("data", List.of(buckets[0], buckets[1], buckets[2], buckets[3], buckets[4]));
        result.put("totalStudents", records.stream().map(AcademicRecord::getStudentId).distinct().count());
        result.put("avgScore", records.stream().mapToDouble(r -> r.getScore()).average().orElse(0));
        return result;
    }

    /** 学生雷达图数据（按课程类型统计均分）*/
    public Map<String, Object> getStudentRadarData(String studentId) {
        List<AcademicRecord> records = recordRepository.findByStudentId(studentId);

        Map<String, Double> avgBySubject = records.stream()
                .collect(Collectors.groupingBy(
                        AcademicRecord::getCourseName,
                        Collectors.averagingDouble(r -> r.getScore())
                ));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("subjects", new ArrayList<>(avgBySubject.keySet()));
        result.put("scores", new ArrayList<>(avgBySubject.values()));
        result.put("totalCredits", records.stream().mapToDouble(r -> r.getCredit()).sum());
        result.put("gpa", records.stream().mapToDouble(r -> r.getScore() * r.getCredit()).sum()
                / Math.max(1, records.stream().mapToDouble(r -> r.getCredit()).sum()));
        return result;
    }

    /** 获取全部成绩记录 */
    public List<AcademicRecord> getAllRecords() {
        return recordRepository.findAll();
    }

    /** Dashboard 统计 */
    public Map<String, Object> getDashboardStats() {
        long studentCount = recordRepository.countDistinctStudents();
        long warningCount = recordRepository.findByIsWarningTrue().stream()
                .map(AcademicRecord::getStudentId).distinct().count();
        Double avgScore = recordRepository.calculateOverallAvgScore();
        List<AcademicRecord> recentWarnings = recordRepository.findByIsWarningTrue().stream()
                .collect(Collectors.groupingBy(AcademicRecord::getStudentId))
                .entrySet().stream()
                .map(e -> e.getValue().get(0))
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalStudents", studentCount);
        stats.put("warningCount", warningCount);
        stats.put("totalCourses", courseRepository.count());
        stats.put("avgScore", avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0);
        stats.put("recentWarnings", recentWarnings);
        return stats;
    }

    /** 新增成绩 */
    public AcademicRecord createRecord(AcademicRecord record) {
        return recordRepository.save(record);
    }

    /** 修改成绩 */
    public Optional<AcademicRecord> updateRecord(Long id, AcademicRecord updated) {
        return recordRepository.findById(id).map(r -> {
            r.setStudentId(updated.getStudentId());
            r.setStudentName(updated.getStudentName());
            r.setClassName(updated.getClassName());
            r.setCourseName(updated.getCourseName());
            r.setCourseType(updated.getCourseType());
            r.setScore(updated.getScore());
            r.setCredit(updated.getCredit());
            r.setSemester(updated.getSemester());
            return recordRepository.save(r);
        });
    }

    /** 删除成绩 */
    public boolean deleteRecord(Long id) {
        if (recordRepository.existsById(id)) {
            recordRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
