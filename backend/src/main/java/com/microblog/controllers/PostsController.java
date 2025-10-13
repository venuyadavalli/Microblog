package com.microblog.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.microblog.dto.PostView;
import com.microblog.models.Post;
import com.microblog.models.User;
import com.microblog.repositories.UserRepository;
import com.microblog.services.CurrentUserService;
import com.microblog.services.PostMapperService;
import com.microblog.services.PostService;

@RestController
@RequestMapping("/posts")
public class PostsController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostService postService;

  @Autowired
  private PostMapperService postMapperService;

  @Autowired
  private CurrentUserService currentUser;

  @GetMapping("/{username}")
  public List<PostView> getAllPostsByUsername(@PathVariable String username)
      throws FirebaseAuthException {
    List<Post> posts = postService.getPostsByUsername(username);
    List<PostView> views = posts
        .stream()
        .map(post -> postMapperService.toPostView(post, currentUser.getId()))
        .collect(Collectors.toList());
    return views;
  }

  @PostMapping
  public PostView addPost(@RequestBody Post post) throws FirebaseAuthException {
    User author = userRepository.findById(currentUser.getId()).orElseThrow();
    post.setAuthor(author);
    Post saved = postService.addPost(post);
    PostView view = postMapperService.toPostView(saved, currentUser.getId());
    return view;
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
    postService.deletePost(postId);
    return ResponseEntity.noContent().build();
  }
}
