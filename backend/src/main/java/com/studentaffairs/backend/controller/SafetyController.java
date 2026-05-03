package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.SafetyIncident;
import com.studentaffairs.backend.repository.SafetyIncidentRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/safety")
@CrossOrigin(origins = "*")
public class SafetyController {

    private final SafetyIncidentRepository repository;

    public SafetyController(SafetyIncidentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/incidents")
    public Result<List<SafetyIncident>> getAll() {
        return Result.success(repository.findByOrderByReportTimeDesc());
    }

    @PostMapping("/incidents")
    public Result<SafetyIncident> create(@RequestBody SafetyIncident incident) {
        incident.setReportTime(LocalDateTime.now());
        if (incident.getStatus() == null) incident.setStatus("OPEN");
        return Result.success(repository.save(incident));
    }

    @PutMapping("/incidents/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return repository.findById(id).map(incident -> {
            incident.setStatus(body.get("status"));
            repository.save(incident);
            return Result.success("状态已更新");
        }).orElse(Result.error(404, "事件不存在"));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", repository.count());
        stats.put("open", repository.countByStatus("OPEN"));
        stats.put("processing", repository.countByStatus("PROCESSING"));
        stats.put("closed", repository.countByStatus("CLOSED"));
        stats.put("highLevel", repository.findByLevelOrderByReportTimeDesc("HIGH").size());
        return Result.success(stats);
    }
}
