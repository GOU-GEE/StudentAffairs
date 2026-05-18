package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {

    List<AcademicRecord> findByStudentId(String studentId);

    List<AcademicRecord> findByIsWarningTrue();

    List<AcademicRecord> findByClassName(String className);

    @Query("SELECT a.studentId, a.studentName, a.className, AVG(a.score) as avgScore FROM AcademicRecord a GROUP BY a.studentId, a.studentName, a.className ORDER BY avgScore ASC")
    List<Object[]> findStudentGpaSummary();

    @Query("SELECT a.courseName, AVG(a.score) as avgScore, COUNT(a) as total FROM AcademicRecord a WHERE a.className = :className GROUP BY a.courseName")
    List<Object[]> findClassCourseStats(String className);

    @Query("SELECT COUNT(DISTINCT a.studentId) FROM AcademicRecord a")
    Long countDistinctStudents();

    @Query("SELECT AVG(a.score) FROM AcademicRecord a")
    Double calculateOverallAvgScore();
}
