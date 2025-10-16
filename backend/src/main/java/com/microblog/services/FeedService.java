package com.microblog.services;

import com.microblog.dto.PostView;
import com.microblog.models.Post;
import com.microblog.repositories.PostRepository;
import com.microblog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private PostMapperService postMapperService;

  @Autowired
  private CurrentUserService currentUser;

  // @Autowired
  // private UserRepository userRepository;

  public Page<PostView> getPublicFeed(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Post> posts = postRepository.findAll(pageable);
    return posts.map(post -> postMapperService.toPostView(post, currentUser.getId()));
  }

  // public Page<PostView> getFollowingFeed(int page, int size) {
  // var user = userRepository.findById(currentUser.getId()).orElseThrow();
  // List<String> followingUsernames = user.get
  // .stream()
  // .map(followingUser -> followingUser.getUsername())
  // .collect(Collectors.toList());
  // Pageable pageable = PageRequest.of(page, size,
  // Sort.by("createdAt").descending());
  // Page<Post> posts = postRepository.findByAuthorUsernameIn(followingUsernames,
  // pageable);
  // return posts.map(post -> postMapperService.toPostView(post,
  // currentUser.getId()));
  // }
}
