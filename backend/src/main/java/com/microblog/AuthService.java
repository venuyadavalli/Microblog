package com.microblog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SessionCookieOptions;
import com.google.firebase.auth.UserRecord;
import com.microblog.models.User;
import com.microblog.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FirebaseAuth firebaseAuth;

  public Map<String, Object> register(String username, String email, String password)
      throws IllegalArgumentException, FirebaseAuthException {

    if (userRepository.findByUsername(username).isPresent()) {
      throw new IllegalArgumentException("Username is already taken");
    }

    UserRecord.CreateRequest request = new UserRecord.CreateRequest()
        .setEmail(email)
        .setPassword(password);

    UserRecord userRecord = firebaseAuth.createUser(request);

    User userEntity = new User();
    userEntity.setId(userRecord.getUid());
    userEntity.setUsername(username);
    userRepository.save(userEntity);

    return Map.of(
        "uid", userRecord.getUid(),
        "email", userRecord.getEmail(),
        "username", username);
  }

  public String createSessionCookie(String idToken) throws FirebaseAuthException {
    long expiresIn = 60L * 60L * 1000L; // 1 hour
    SessionCookieOptions options = SessionCookieOptions.builder()
        .setExpiresIn(expiresIn)
        .build();
    return firebaseAuth.createSessionCookie(idToken, options);
  }

  public Map<String, Object> getUser(String uid) throws FirebaseAuthException {
    UserRecord userRecord = firebaseAuth.getUser(uid);
    Map<String, Object> userData = new HashMap<>();
    userData.put("uid", userRecord.getUid());
    userData.put("email", userRecord.getEmail());
    return userData;
  }

  public void deleteUser(String uid) throws FirebaseAuthException {
    firebaseAuth.deleteUser(uid);
  }
}
