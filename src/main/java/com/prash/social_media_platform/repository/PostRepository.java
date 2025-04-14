package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Optionally add: List<Post> findByUserId(Long userId);
}
