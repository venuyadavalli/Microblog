package com.microblog.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.microblog.dto.UserItemView;
import com.microblog.dto.UserProfileView;
import com.microblog.services.UserMapperService;
import com.microblog.services.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapperService userMapperService;

  @GetMapping("/info/{username}")
  public UserProfileView getUserProfileViewByUsername(@PathVariable String username) throws FirebaseAuthException {
    var auth = (FirebaseToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var currentUserId = auth.getUid();
    var targetUser = userService.getUserInfoByUsername(username);
    return userMapperService.toUserProfileView(targetUser, currentUserId);
  }

  @GetMapping("/search/{usernamePart}")
  public List<UserItemView> searchUsers(@PathVariable String usernamePart) throws FirebaseAuthException {
    var auth = (FirebaseToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    var currentUserId = auth.getUid();
    var matchingUsers = userService.searchUsersByUsername(usernamePart);
    return userMapperService.toUserItemViewList(matchingUsers, currentUserId);
  }

}
