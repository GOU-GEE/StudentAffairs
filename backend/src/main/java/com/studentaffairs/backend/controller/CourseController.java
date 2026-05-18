package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.Course;
import com.studentaffairs.backend.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Result<List<Course>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String semester) {
        return Result.success(courseService.getAllCourses(type, semester));
    }

    @PostMapping
    public Result<Course> create(@RequestBody Course course) {
        return Result.success(courseService.createCourse(course));
    }

    @PutMapping("/{id}")
    public Result<Course> update(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course)
                .map(Result::success)
                .orElse(Result.error(404, "课程不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (courseService.deleteCourse(id)) {
            return Result.success(null);
        }
        return Result.error(404, "课程不存在");
    }
}
