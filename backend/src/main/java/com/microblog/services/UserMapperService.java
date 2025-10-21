package com.microblog.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microblog.dto.UserInfo;
import com.microblog.dto.UserItem;
import com.microblog.dto.UserItemView;
import com.microblog.dto.UserProfileView;
import com.microblog.models.User;

@Service
public class UserMapperService {

  @Autowired
  private FollowsService followService;

  @Autowired
  private CurrentUserService currentUser;

  public UserProfileView toUserProfileView(UserInfo userInfo, String currentUserId) {
    UserProfileView view = new UserProfileView();
    view.setId(userInfo.getId());
    view.setUsername(userInfo.getUsername());
    view.setCreatedAt(Instant.ofEpochMilli(userInfo.getCreationTimestamp()).toString());
    view.setIsFollowed(followService.isFollowing(currentUserId, userInfo.getId()));
    return view;
  }

  private UserItemView toUserItemView(UserInfo userInfo, String currentUserId) {
    UserItemView view = new UserItemView();
    view.setId(userInfo.getId());
    view.setUsername(userInfo.getUsername());
    Boolean isFollowed = followService.isFollowing(currentUserId, userInfo.getId());
    view.setIsFollowed(isFollowed);
    return view;
  }

  public List<UserItemView> toUserItemViewList(List<UserInfo> userInfos, String currentUserId) {
    return userInfos.stream()
        .map(userInfo -> toUserItemView(userInfo, currentUserId))
        .collect(Collectors.toList());
  }

  private UserItemView toUserItemView(UserItem userItem, String currentUserId) {
    UserItemView view = new UserItemView();
    view.setId(userItem.getId());
    view.setUsername(userItem.getUsername());
    Boolean isFollowed = followService.isFollowing(currentUserId, userItem.getId());
    view.setIsFollowed(isFollowed);
    return view;
  }

  public List<UserItemView> toUserItemViewListFromItems(List<UserItem> userItems) {
    return userItems.stream()
        .map(userItem -> toUserItemView(userItem, currentUser.getId()))
        .collect(Collectors.toList());
  }

  public UserItem toUserItem(User user) {
    UserItem u = new UserItem();
    u.setId(user.getId());
    u.setUsername(user.getUsername());
    return u;
  }

}
