package com.microblog.services;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.microblog.dto.UserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

  @Autowired
  private UserService userService;

  // private FirebaseToken getCurrentUserToken() {
  // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
  // FirebaseToken principal = (FirebaseToken) auth.getPrincipal();
  // return principal;
  // }

  private FirebaseToken getCurrentUserToken() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object principal = auth.getPrincipal();
    if (principal instanceof FirebaseToken) {
      return (FirebaseToken) principal;
    }
    return null;
  }

  public UserInfo getInfo() throws FirebaseAuthException {
    FirebaseToken token = getCurrentUserToken();
    return userService.getUserInfoById(token.getUid());
  }

  public String getId() {
    FirebaseToken token = getCurrentUserToken();
    return (token != null) ? token.getUid() : null;
  }

  public String getEmail() {
    FirebaseToken token = getCurrentUserToken();
    return (token != null) ? token.getEmail() : null;
  }

  public String getName() {
    FirebaseToken token = getCurrentUserToken();
    return (token != null) ? token.getName() : null;
  }
}
