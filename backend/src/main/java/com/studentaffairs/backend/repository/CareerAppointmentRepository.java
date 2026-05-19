package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.CareerAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerAppointmentRepository extends JpaRepository<CareerAppointment, Long> {
    List<CareerAppointment> findByStudentIdOrderByCreateTimeDesc(String studentId);
    List<CareerAppointment> findByTeacherIdOrderByCreateTimeDesc(String teacherId);
    List<CareerAppointment> findAllByOrderByCreateTimeDesc();
}
