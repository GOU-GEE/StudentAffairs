package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.WorkStudyJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkStudyJobRepository extends JpaRepository<WorkStudyJob, Long> {
}
