package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.Announcement;
import com.studentaffairs.backend.repository.AnnouncementRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/communication")
@CrossOrigin(origins = "*")
public class CommunicationController {

    private final AnnouncementRepository repository;

    public CommunicationController(AnnouncementRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/announcements")
    public Result<List<Announcement>> getAll() {
        return Result.success(repository.findByOrderByIsPinnedDescPublishTimeDesc());
    }

    @PostMapping("/announcements")
    public Result<Announcement> publish(@RequestBody Announcement announcement) {
        announcement.setPublishTime(LocalDateTime.now());
        if (announcement.getIsPinned() == null) announcement.setIsPinned(false);
        return Result.success(repository.save(announcement));
    }

    @DeleteMapping("/announcements/{id}")
    public Result<String> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return Result.success("公告已删除");
    }
}
