package com.microblog.services;

import org.springframework.stereotype.Service;

@Service
public class FollowService {

  public Boolean isFollowing(String currentUserId, String targetUserId) {
    if (currentUserId.equals(targetUserId)) {
      return null;
    }
    return true;
  }
}
