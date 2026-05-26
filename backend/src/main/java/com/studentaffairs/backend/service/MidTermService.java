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
        if (appraisal.getStatus() == null) {
            appraisal.setStatus("SUBMITTED");
        }
        
        // Enforce exactly one mid-term appraisal record per student
        List<MidTermAppraisal> existing = repository.findByStudentId(appraisal.getStudentId());
        if (!existing.isEmpty()) {
            MidTermAppraisal existingRecord = existing.get(0);
            
            // Clean up any duplicates in the database to prevent anomalies
            if (existing.size() > 1) {
                for (int i = 1; i < existing.size(); i++) {
                    repository.delete(existing.get(i));
                }
            }
            
            existingRecord.setStudentName(appraisal.getStudentName());
            existingRecord.setClassName(appraisal.getClassName());
            existingRecord.setThoughtPerformance(appraisal.getThoughtPerformance());
            existingRecord.setAcademicPerformance(appraisal.getAcademicPerformance());
            existingRecord.setOverallPerformance(appraisal.getOverallPerformance());
            existingRecord.setSelfAssessment(appraisal.getSelfAssessment());
            existingRecord.setSubmitTime(appraisal.getSubmitTime());
            existingRecord.setStatus(appraisal.getStatus());
            return repository.save(existingRecord);
        }
        
        return repository.save(appraisal);
    }

    public List<MidTermAppraisal> getByStudent(String studentId) {
        return repository.findByStudentIdOrderByIdDesc(studentId);
    }

    public List<MidTermAppraisal> getAll() {
        return repository.findByStatusOrderBySubmitTimeDesc("SUBMITTED");
    }

    public Optional<MidTermAppraisal> getById(Long id) {
        return repository.findById(id);
    }
}
