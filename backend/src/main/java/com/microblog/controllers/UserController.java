package com.microblog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuthException;
import com.microblog.dto.UserInfo;
import com.microblog.dto.UserItemView;
import com.microblog.dto.UserProfileView;
import com.microblog.services.CurrentUserService;
import com.microblog.services.UserMapperService;
import com.microblog.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapperService userMapperService;

  @Autowired
  private CurrentUserService currentUser;

  @GetMapping("/info/{username}")
  public UserProfileView getUserProfileViewByUsername(@PathVariable String username) throws FirebaseAuthException {
    UserInfo targetUser = userService.getUserInfoByUsername(username);
    return userMapperService.toUserProfileView(targetUser, currentUser.getId());
  }

  @GetMapping("/search/{usernamePart}")
  public List<UserItemView> searchUsers(@PathVariable String usernamePart) throws FirebaseAuthException {
    var matchingUsers = userService.searchUsersByUsername(usernamePart);
    return userMapperService.toUserItemViewList(matchingUsers, currentUser.getId());
  }

}
