package com.microblog.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microblog.dto.UserItem;
import com.microblog.models.Like;
import com.microblog.models.Post;
import com.microblog.models.User;
import com.microblog.repositories.LikeRepository;
import com.microblog.repositories.PostRepository;
import com.microblog.repositories.UserRepository;

@Service
public class LikeService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private LikeRepository likeRepository;

  @Autowired
  private UserMapperService userMapperService;

  @Autowired
  private CurrentUserService currentUser;

  public Like likePost(UUID postId) {
    User user = userRepository.findById(currentUser.getId()).orElseThrow();
    Post post = postRepository.findById(postId).orElseThrow();
    if (likeRepository.findByUserAndPost(user, post).isEmpty()) {
      Like like = new Like(user, post);
      return likeRepository.save(like);
    }
    throw new RuntimeException("Already liked");
  }

  public void unlikePost(UUID postId) {
    User user = userRepository.findById(currentUser.getId()).orElseThrow();
    Post post = postRepository.findById(postId).orElseThrow();
    likeRepository.deleteByUserAndPost(user, post);
  }

  public boolean isLiked(UUID postId, String userId) {
    User user = userRepository.findById(userId).orElseThrow();
    Post post = postRepository.findById(postId).orElseThrow();
    return likeRepository.findByUserAndPost(user, post).isPresent();
  }

  public List<Post> getLikedPostsByUsername(String username) {
    User user = userRepository.findByUsername(username).orElseThrow();
    return user.getLikes().stream().map(Like::getPost).collect(Collectors.toList());
  }

  public long getLikeCount(UUID postId) {
    Post post = postRepository.findById(postId).orElseThrow();
    return likeRepository.countByPost(post);
  }

  public List<UserItem> getUsersWhoLikedPost(UUID postId) {
    Post post = postRepository.findById(postId).orElseThrow();
    return likeRepository.findByPost(post).stream().map(Like::getUser).map(userMapperService::toUserItem).toList();
  }
}
