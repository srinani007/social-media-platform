package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    //List<Message> findByUserId(Long userId);
}
