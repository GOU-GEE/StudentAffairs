package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.SecondClassroomRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecondClassroomRecordRepository extends JpaRepository<SecondClassroomRecord, Long> {
    List<SecondClassroomRecord> findByStudentId(String studentId);
}
