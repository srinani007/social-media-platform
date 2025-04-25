package com.prash.social_media_platform.service;


import com.prash.social_media_platform.model.Post;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.PostRepository;
import com.prash.social_media_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public void createPost(Post post , String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    public List<Post> findAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }

    public List<Post> searchPosts(String keyword) {
        // search in title OR content
        List<Post> byTitle   = postRepository.findByTitleContainingIgnoreCase(keyword);
        List<Post> byContent = postRepository.findByContentContainingIgnoreCase(keyword);
        // merge or choose one
        return Stream.concat(byTitle.stream(), byContent.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
