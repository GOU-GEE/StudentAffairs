package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.YouthAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YouthAwardRepository extends JpaRepository<YouthAward, Long> {
    List<YouthAward> findByStudentId(String studentId);
}
