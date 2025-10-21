package com.microblog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.microblog.dto.UserInfo;
import com.microblog.models.User;
import com.microblog.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  private FirebaseAuth firebaseAuth;

  @Autowired
  private UserRepository userRepository;

  private UserInfo getUserInfoByUser(User user) {
    try {
      UserRecord userRecord = firebaseAuth.getUser(user.getId());

      UserInfo userInfo = new UserInfo();
      userInfo.setId(user.getId());
      userInfo.setEmail(userRecord.getEmail());
      userInfo.setUsername(user.getUsername());
      userInfo.setCreationTimestamp(userRecord.getUserMetadata().getCreationTimestamp());

      return userInfo;
    } catch (FirebaseAuthException e) {
      throw new RuntimeException("Failed to fetch Firebase info for user " + user.getId(), e);
    }
  }

  public UserInfo getUserInfoByUsername(String username) throws FirebaseAuthException {
    User user = userRepository.findByUsername(username).orElseThrow();
    return getUserInfoByUser(user);
  }

  public UserInfo getUserInfoById(String userId) throws FirebaseAuthException {
    User user = userRepository.findById(userId).orElseThrow();
    return getUserInfoByUser(user);
  }

  public List<UserInfo> searchUsersByUsername(String usernamePart) {
    List<User> users = userRepository.findByUsernameContainingIgnoreCase(usernamePart);
    return users.stream()
        .map(user -> getUserInfoByUser(user))
        .toList();
  }
}
