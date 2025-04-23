// InteractionService.java
package com.prash.social_media_platform.service;

import com.prash.social_media_platform.model.*;
import com.prash.social_media_platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InteractionService {
    @Autowired private PostLikeRepository likeRepo;
    @Autowired private CommentRepository commentRepo;
    @Autowired private ShareRepository shareRepo;
    @Autowired private RepostRepository repostRepo;
    @Autowired private UserService userService;
    @Autowired private PostService postService;

    @Transactional
    public void likePost(Long postId, String username) {
        User u = userService.getByUsernameOrEmail(username);
        Post p = postService.findAllPosts().stream()
                .filter(x->x.getId().equals(postId))
                .findFirst()
                .orElseThrow();
        if (!likeRepo.existsByUserIdAndPostId(u.getId(), postId)) {
            PostLike like = new PostLike();
            like.setUser(u);
            like.setPost(p);
            likeRepo.save(like);
        }
    }

    @Transactional
    public void commentOnPost(Long postId, String content, String username) {
        User u = userService.getByUsernameOrEmail(username);
        Post p = postService.findAllPosts().stream()
                .filter(x->x.getId().equals(postId))
                .findFirst()
                .orElseThrow();
        Comment c = new Comment();
        c.setUser(u);
        c.setPost(p);
        c.setContent(content);
        commentRepo.save(c);
    }

    @Transactional
    public void sharePost(Long postId, String username) {
        User u = userService.getByUsernameOrEmail(username);
        Post p = postService.findAllPosts().stream()
                .filter(x->x.getId().equals(postId))
                .findFirst()
                .orElseThrow();
        Share s = new Share();
        s.setUser(u);
        s.setPost(p);
        shareRepo.save(s);
    }

    @Transactional
    public void repostPost(Long postId, String username) {
        User u = userService.getByUsernameOrEmail(username);
        Post p = postService.findAllPosts().stream()
                .filter(x->x.getId().equals(postId))
                .findFirst()
                .orElseThrow();
        Repost r = new Repost();
        r.setUser(u);
        r.setPost(p);
        repostRepo.save(r);
    }

    // count helpers
    public long getLikeCount(Long postId)    { return likeRepo.countByPostId(postId); }
    public long getCommentCount(Long postId) { return commentRepo.countByPostId(postId); }
    public long getShareCount(Long postId)   { return shareRepo.countByPostId(postId); }
    public long getRepostCount(Long postId)  { return repostRepo.countByPostId(postId); }

    public List<Comment> getComments(Long postId) {
        return commentRepo.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
