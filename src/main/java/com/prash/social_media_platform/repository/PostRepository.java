package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Post;
import com.prash.social_media_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);


}
