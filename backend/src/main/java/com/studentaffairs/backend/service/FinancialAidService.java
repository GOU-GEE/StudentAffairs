package com.studentaffairs.backend.service;

import com.studentaffairs.backend.entity.WorkStudyApplication;
import com.studentaffairs.backend.entity.WorkStudyJob;
import com.studentaffairs.backend.repository.WorkStudyApplicationRepository;
import com.studentaffairs.backend.repository.WorkStudyJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinancialAidService {

    private final WorkStudyJobRepository jobRepository;
    private final WorkStudyApplicationRepository applicationRepository;

    public FinancialAidService(WorkStudyJobRepository jobRepository, WorkStudyApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<WorkStudyJob> getAllJobs() {
        return jobRepository.findAll();
    }

    @Transactional
    public void applyForJob(Long jobId, String studentId) {
        // 1. 检查是否重复申请
        if (applicationRepository.existsByStudentIdAndJobId(studentId, jobId)) {
            throw new RuntimeException("您已经申请过该岗位，请勿重复提交");
        }

        // 2. 获取岗位信息
        WorkStudyJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("岗位不存在"));

        // 3. 检查容量状态
        if ("FULL".equals(job.getStatus()) || job.getCurrentCount() >= job.getQuota()) {
            throw new RuntimeException("该岗位名额已满");
        }

        // 4. 更新岗位招募人数 (触发 @Version 乐观锁校验)
        job.setCurrentCount(job.getCurrentCount() + 1);
        if (job.getCurrentCount().equals(job.getQuota())) {
            job.setStatus("FULL");
        }
        jobRepository.save(job);

        // 5. 插入申请流转记录 (状态机初始态为 PENDING)
        WorkStudyApplication application = new WorkStudyApplication();
        application.setStudentId(studentId);
        application.setJobId(jobId);
        application.setStatus("PENDING");
        application.setApplyTime(LocalDateTime.now());
        applicationRepository.save(application);
    }

    public List<WorkStudyApplication> getAllApplications() {
        return applicationRepository.findByOrderByApplyTimeDesc();
    }

    @Transactional
    public void reviewApplication(Long applicationId, String status) {
        WorkStudyApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));
        if (!"PENDING".equals(app.getStatus())) {
            throw new RuntimeException("该申请已处理");
        }
        app.setStatus(status);
        applicationRepository.save(app);

        // 如果拒绝，释放岗位名额
        if ("REJECTED".equals(status)) {
            WorkStudyJob job = jobRepository.findById(app.getJobId()).orElse(null);
            if (job != null) {
                job.setCurrentCount(Math.max(0, job.getCurrentCount() - 1));
                if ("FULL".equals(job.getStatus())) job.setStatus("ACTIVE");
                jobRepository.save(job);
            }
        }
    }

    public WorkStudyJob createJob(WorkStudyJob job) {
        job.setCurrentCount(0);
        if (job.getStatus() == null) job.setStatus("ACTIVE");
        return jobRepository.save(job);
    }

    public java.util.Map<String, Object> getDashboardStats() {
        long totalJobs = jobRepository.count();
        long activeJobs = jobRepository.findAll().stream()
                .filter(j -> "ACTIVE".equals(j.getStatus())).count();
        long totalApplications = applicationRepository.count();
        long pendingReviews = applicationRepository.countByStatus("PENDING");

        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("totalJobs", totalJobs);
        stats.put("activeJobs", activeJobs);
        stats.put("totalApplications", totalApplications);
        stats.put("pendingReviews", pendingReviews);
        return stats;
    }
}
