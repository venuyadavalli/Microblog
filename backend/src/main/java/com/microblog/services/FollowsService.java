package com.microblog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microblog.dto.UserItem;
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

  public Boolean isFollowing(String currentUserId, String targetUserId) {
    if (currentUserId.equals(targetUserId)) {
      return null;
    }
    return followsRepository.existsByFollower_IdAndFollowee_Id(currentUserId, targetUserId);
  }

  private UserItem toUserItem(User user) {
    UserItem userItem = new UserItem();
    userItem.setId(user.getId());
    userItem.setUsername(user.getUsername());
    return userItem;
  }

  public List<UserItem> getFollowers(String targetUserId) {
    List<Follows> follows = followsRepository.findByFollowee_Id(targetUserId);
    return follows
        .stream()
        .map(f -> toUserItem(f.getFollower()))
        .collect(Collectors.toList());
  }

  public List<UserItem> getFollowees(String targetUserId) {
    List<Follows> follows = followsRepository.findByFollower_Id(targetUserId);
    return follows
        .stream()
        .map(f -> toUserItem(f.getFollowee()))
        .collect(Collectors.toList());
  }

}
