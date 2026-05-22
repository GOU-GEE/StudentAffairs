package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreateTimeDesc(String userId);
    List<Notification> findByUserIdAndIsReadOrderByCreateTimeDesc(String userId, Boolean isRead);
}
