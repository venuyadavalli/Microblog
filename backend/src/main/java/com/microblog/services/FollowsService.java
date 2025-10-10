package com.microblog.services;

import com.google.firebase.auth.FirebaseToken;
import com.microblog.models.Follows;
import com.microblog.models.User;
import com.microblog.repositories.FollowsRepository;
import com.microblog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowsService {

  @Autowired
  private FollowsRepository followsRepository;

  @Autowired
  private UserRepository userRepository;

  private String getCurrentUserId() {
    var auth = (FirebaseToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return auth.getUid();
  }

  public void followUser(String targetUserId) {
    String currentUserId = getCurrentUserId();
    if (!followsRepository.existsByFollower_IdAndFollowee_Id(currentUserId, targetUserId)) {
      User follower = userRepository.findById(currentUserId).orElseThrow();
      User followee = userRepository.findById(targetUserId).orElseThrow();
      Follows follows = new Follows(follower, followee);
      followsRepository.save(follows);
    }
  }

  public void unfollowUser(String targetUserId) {
    String currentUserId = getCurrentUserId();
    followsRepository.deleteById(new com.microblog.models.FollowsId(currentUserId, targetUserId));
  }

  public List<UserDTO> getFollowers(String targetUserId) {
    String currentUserId = getCurrentUserId();
    List<Follows> follows = followsRepository.findByFollowee_Id(targetUserId);

    return follows.stream().map(f -> {
      User u = f.getFollower();
      boolean isFollowing = followsRepository.existsByFollower_IdAndFollowee_Id(currentUserId, u.getId());
      return new UserDTO(u.getId(), u.getUsername(), isFollowing);
    }).collect(Collectors.toList());
  }

  public List<UserDTO> getFollowees(String targetUserId) {
    String currentUserId = getCurrentUserId();
    List<Follows> follows = followsRepository.findByFollower_Id(targetUserId);

    return follows.stream().map(f -> {
      User u = f.getFollowee();
      boolean isFollowing = followsRepository.existsByFollower_IdAndFollowee_Id(currentUserId, u.getId());
      return new UserDTO(u.getId(), u.getUsername(), isFollowing);
    }).collect(Collectors.toList());
  }

  public static class UserDTO {
    public String uid;
    public String username;
    public boolean isFollowing;

    public UserDTO(String uid, String username, boolean isFollowing) {
      this.uid = uid;
      this.username = username;
      this.isFollowing = isFollowing;
    }
  }
}
