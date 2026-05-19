package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.HonorProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HonorProjectRepository extends JpaRepository<HonorProject, Long> {
}
