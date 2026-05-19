package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.HonorCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorCandidateRepository extends JpaRepository<HonorCandidate, Long> {
    List<HonorCandidate> findByProjectId(Long projectId);
    List<HonorCandidate> findByStudentId(String studentId);
}
