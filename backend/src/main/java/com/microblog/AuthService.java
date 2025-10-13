package com.microblog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.microblog.dto.UserInfo;
import com.microblog.repositories.UserRepository;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FirebaseAuth firebaseAuth;

  public UserInfo register(String username, String email, String password)
      throws IllegalArgumentException, FirebaseAuthException {

    if (userRepository.findByUsername(username).isPresent()) {
      throw new IllegalArgumentException("Username is already taken");
    }

    UserRecord.CreateRequest request = new UserRecord.CreateRequest()
        .setEmail(email)
        .setPassword(password);
    UserRecord userRecord = firebaseAuth.createUser(request);
    String createdAt = String.valueOf(userRecord.getUserMetadata().getCreationTimestamp());

    UserInfo userInfo = new UserInfo();
    userInfo.setId(userRecord.getUid());
    userInfo.setEmail(userRecord.getEmail());
    userInfo.setUsername(username);
    userInfo.setCreationTime(createdAt);
    return userInfo;
  }

  public void deleteUser(String uid) throws FirebaseAuthException {
    firebaseAuth.deleteUser(uid);
  }
}
