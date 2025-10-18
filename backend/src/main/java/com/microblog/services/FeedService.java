package com.microblog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.microblog.dto.PostView;
import com.microblog.models.Post;
import com.microblog.repositories.PostRepository;

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
