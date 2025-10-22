package com.microblog.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microblog.demo.PingEventPublisher;
import com.microblog.models.Post;
import com.microblog.repositories.PostRepository;

@Service
public class PostService {

  @Autowired
  private PingEventPublisher pingEventPublisher;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CurrentUserService currentUser;

  public List<Post> getPostsByUsername(String username) {
    return postRepository.findAllByAuthorUsername(username);
  }

  public Post addPost(Post post) {
    post.setAuthor(currentUser.getUser());
    pingEventPublisher.publishPing("a new post is created by: " + post.getAuthor().getUsername());
    return postRepository.save(post);
  }

  public void deletePost(UUID postId) {
    postRepository.deleteById(postId);
  }
}
