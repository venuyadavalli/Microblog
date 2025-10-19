package com.microblog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.microblog.dto.PostView;
import com.microblog.models.Post;
import com.microblog.repositories.PostRepository;
import com.microblog.repositories.UserRepository;

@Service
public class FeedService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private FollowsService followsService;

  @Autowired
  private PostMapperService postMapperService;

  @Autowired
  private CurrentUserService currentUser;

  @Autowired
  private UserRepository userRepository;

  public Page<PostView> getPublicFeed(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Post> posts = postRepository.findAll(pageable);
    return posts.map(post -> postMapperService.toPostView(post, currentUser.getId()));
  }

  public Page<PostView> getFollowingFeed(int page, int size) {

    List<String> followingUsernames = followsService.getFollowees(currentUser.getId())
        .stream()
        .map(followingUser -> followingUser.getUsername())
        .collect(Collectors.toList());

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Post> posts = postRepository.findByAuthorUsernameIn(followingUsernames, pageable);
    // return posts;
    return posts.map(post -> postMapperService.toPostView(post, currentUser.getId()));
  }
}
