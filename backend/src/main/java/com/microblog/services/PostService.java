package com.microblog.services;

import com.microblog.demo.PingEventPublisher;
import com.microblog.models.Post;
import com.microblog.repositories.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PingEventPublisher pingEventPublisher;

    @Autowired
    private PostRepository postRepository;

    public List<Post> getPostsByUsername(String username) {
        return postRepository.findAllByAuthorUsername(username);
    }

    public Post addPost(Post post) {
        pingEventPublisher.publishPing("a new post is created by: " + post.getAuthor().getUsername());
        return postRepository.save(post);
    }

    public void deletePost(UUID postId) {
        postRepository.deleteById(postId);
    }
}
