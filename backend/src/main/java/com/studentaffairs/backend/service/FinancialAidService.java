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
}
