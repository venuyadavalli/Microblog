package com.microblog.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.microblog.models.Post;
import com.microblog.models.User;
import com.microblog.repositories.UserRepository;
import com.microblog.dto.PostView;
import com.microblog.services.PostService;
import com.microblog.services.PostMapperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostsController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostService postService;

  @Autowired
  private PostMapperService postMapperService;

  @GetMapping("/user/{username}")
  public List<PostView> getAllPostsByUsername(@PathVariable String username)
      throws FirebaseAuthException {
    FirebaseToken auth = (FirebaseToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String currentUserId = auth.getUid();
    List<Post> posts = postService.getPostsByUsername(username);
    List<PostView> views = posts
        .stream()
        .map(post -> postMapperService.toPostView(post, currentUserId))
        .collect(Collectors.toList());
    return views;
  }

  @PostMapping
  public PostView addPost(@RequestBody Post post) throws FirebaseAuthException {
    var auth = (FirebaseToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    String authorId = auth.getUid();
    User author = userRepository.findById(authorId).orElseThrow();
    post.setAuthor(author);

    Post saved = postService.addPost(post);
    PostView view = postMapperService.toPostView(saved, authorId);
    return view;
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
    postService.deletePost(postId);
    return ResponseEntity.noContent().build();
  }
}
