package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Repost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepostRepository extends JpaRepository<Repost, Long> {
    long countByPostId(Long postId);
}
