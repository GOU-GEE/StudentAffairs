package com.studentaffairs.backend.service;

import com.studentaffairs.backend.entity.MidTermAppraisal;
import com.studentaffairs.backend.repository.MidTermAppraisalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MidTermService {

    private final MidTermAppraisalRepository repository;

    public MidTermService(MidTermAppraisalRepository repository) {
        this.repository = repository;
    }

    public MidTermAppraisal submit(MidTermAppraisal appraisal) {
        appraisal.setSubmitTime(LocalDateTime.now());
        return repository.save(appraisal);
    }

    public List<MidTermAppraisal> getByStudent(String studentId) {
        return repository.findByStudentId(studentId);
    }

    public List<MidTermAppraisal> getAll() {
        return repository.findAllByOrderBySubmitTimeDesc();
    }

    public Optional<MidTermAppraisal> getById(Long id) {
        return repository.findById(id);
    }
}
