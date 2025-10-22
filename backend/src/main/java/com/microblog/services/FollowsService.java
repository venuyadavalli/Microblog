package com.microblog.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microblog.models.Follows;
import com.microblog.models.User;
import com.microblog.repositories.FollowsRepository;
import com.microblog.repositories.UserRepository;

@Service
public class FollowsService {

  @Autowired
  private FollowsRepository followsRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CurrentUserService currentUser;

  public void followUser(String targetUserId) {
    if (!followsRepository.existsByFollower_IdAndFollowee_Id(currentUser.getId(), targetUserId)) {
      User follower = userRepository.findById(currentUser.getId()).orElseThrow();
      User followee = userRepository.findById(targetUserId).orElseThrow();
      Follows follows = new Follows(follower, followee);
      followsRepository.save(follows);
    }
  }

  public void unfollowUser(String targetUserId) {
    followsRepository.deleteById(new com.microblog.models.FollowsId(currentUser.getId(), targetUserId));
  }

  public List<User> getFollowers(String targetUserId) {
    List<Follows> follows = followsRepository.findByFollowee_Id(targetUserId);
    return follows
        .stream()
        .map(f -> f.getFollower())
        .collect(Collectors.toList());
  }

  public List<User> getFollowees(String targetUserId) {
    List<Follows> follows = followsRepository.findByFollower_Id(targetUserId);
    return follows
        .stream()
        .map(f -> f.getFollowee())
        .collect(Collectors.toList());
  }

  public Boolean isFollowing(String currentUserId, String targetUserId) {
    if (currentUserId.equals(targetUserId)) {
      return null;
    }
    return followsRepository.existsByFollower_IdAndFollowee_Id(currentUserId, targetUserId);
  }

  public Set<String> getFollowedUserIds(String currentUserId, List<String> targetUserIds) {
    return followsRepository.findFollowedUserIds(currentUserId, targetUserIds);
  }

}
