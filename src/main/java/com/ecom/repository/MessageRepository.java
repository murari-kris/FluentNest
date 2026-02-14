package com.ecom.repository;

import com.ecom.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE (m.sender = :u1 AND m.receiver = :u2)
           OR (m.sender = :u2 AND m.receiver = :u1)
        ORDER BY m.timestamp ASC
    """)
    List<ChatMessage> getConversation(
            @Param("u1") String u1,
            @Param("u2") String u2
    );
}
