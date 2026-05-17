package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByStudentId(String studentId);

    boolean existsByStudentId(String studentId);

    java.util.List<StudentProfile> findByTeacherId(String teacherId);
}
