package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    long countByPostId(Long postId);
}
