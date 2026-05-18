package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByType(String type);
    List<Course> findBySemester(String semester);
    List<Course> findByTypeAndSemester(String type, String semester);
}
