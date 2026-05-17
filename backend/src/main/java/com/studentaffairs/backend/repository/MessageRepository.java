package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 查两个用户之间的全部消息（按时间升序）
    @Query("SELECT m FROM Message m WHERE (m.senderId = :user1 AND m.receiverId = :user2) OR (m.senderId = :user2 AND m.receiverId = :user1) ORDER BY m.sentTime ASC")
    List<Message> findConversation(@Param("user1") String user1, @Param("user2") String user2);

    // 查某用户的所有会话对方ID（去重），用于构建联系人列表
    @Query("SELECT DISTINCT m.senderId FROM Message m WHERE m.receiverId = :userId AND m.senderRole = 'student'")
    List<String> findStudentSendersByReceiverId(@Param("userId") String userId);

    @Query("SELECT DISTINCT m.receiverId FROM Message m WHERE m.senderId = :userId AND m.senderRole = 'teacher'")
    List<String> findStudentReceiversBySenderId(@Param("userId") String userId);

    // 查某用户的未读消息数
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.isRead = false AND m.isRecalled = false")
    long countUnreadByReceiverId(@Param("userId") String userId);

    // 查某人作为接收者的未读数，按发送者分组
    @Query("SELECT m.senderId, COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.isRead = false AND m.isRecalled = false GROUP BY m.senderId")
    List<Object[]> countUnreadBySender(@Param("userId") String userId);
}
