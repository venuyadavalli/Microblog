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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microblog.dto.PostView;
import com.microblog.dto.UserItem;
import com.microblog.dto.UserItemView;
import com.microblog.models.Like;
import com.microblog.models.Post;
import com.microblog.services.CurrentUserService;
import com.microblog.services.LikeService;
import com.microblog.services.PostMapperService;
import com.microblog.services.UserMapperService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/likes")
public class LikeController {

  @Autowired
  private LikeService likeService;

  @Autowired
  private CurrentUserService currentUser;

  @Autowired
  private PostMapperService postMapperService;

  @Autowired
  private UserMapperService userMapperService;

  @PostMapping("/{postId}")
  public ResponseEntity<Like> likePost(@PathVariable UUID postId) {
    Like like = likeService.likePost(postId);
    return ResponseEntity.ok(like);
  }

  @DeleteMapping("/{postId}")
  @Transactional
  public ResponseEntity<Void> unlikePost(@PathVariable UUID postId) {
    likeService.unlikePost(postId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{postId}")
  public ResponseEntity<List<UserItemView>> getLikedUsersOfPost(@PathVariable UUID postId) {
    List<UserItem> likedUsers = likeService.getUsersWhoLikedPost(postId);
    List<UserItemView> likedUsersView = userMapperService.toUserItemViewListFromItems(likedUsers);
    return ResponseEntity.ok(likedUsersView);
  }

  @GetMapping("/user/{username}")
  public ResponseEntity<List<PostView>> getLikedPostsByUser(@PathVariable String username) {
    List<Post> likedPosts = likeService.getLikedPostsByUsername(username);
    List<PostView> likedPostsView = likedPosts.stream()
        .map(post -> postMapperService.toPostView(post, currentUser.getId()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(likedPostsView);
  }

}
