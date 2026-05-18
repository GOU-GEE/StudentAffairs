package com.studentaffairs.backend.service;

import com.studentaffairs.backend.entity.Course;
import com.studentaffairs.backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses(String type, String semester) {
        if (type != null && semester != null) {
            return courseRepository.findByTypeAndSemester(type, semester);
        } else if (type != null) {
            return courseRepository.findByType(type);
        } else if (semester != null) {
            return courseRepository.findBySemester(semester);
        }
        return courseRepository.findAll();
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> updateCourse(Long id, Course updated) {
        return courseRepository.findById(id).map(c -> {
            c.setCode(updated.getCode());
            c.setName(updated.getName());
            c.setType(updated.getType());
            c.setCredit(updated.getCredit());
            c.setHours(updated.getHours());
            c.setSemester(updated.getSemester());
            c.setTeacher(updated.getTeacher());
            c.setClassName(updated.getClassName());
            return courseRepository.save(c);
        });
    }

    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
