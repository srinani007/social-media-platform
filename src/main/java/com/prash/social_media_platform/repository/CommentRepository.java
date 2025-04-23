package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPostId(Long postId);
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
