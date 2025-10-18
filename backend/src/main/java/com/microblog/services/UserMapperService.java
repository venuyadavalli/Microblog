package com.microblog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microblog.dto.UserInfo;
import com.microblog.dto.UserItem;
import com.microblog.dto.UserItemView;
import com.microblog.dto.UserProfileView;
import com.microblog.models.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapperService {

  @Autowired
  private FollowsService followService;

  @Autowired
  private CurrentUserService currentUser;

  public static String formatEpochMilliToDate(String epochMilliStr) {
    long epochMilli = Long.parseLong(epochMilliStr);
    Instant instant = Instant.ofEpochMilli(epochMilli);
    ZonedDateTime dateTime = instant.atZone(ZoneId.of("Asia/Kolkata"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    return dateTime.format(formatter);
  }

  public UserProfileView toUserProfileView(UserInfo userInfo, String currentUserId) {
    UserProfileView view = new UserProfileView();
    view.setId(userInfo.getId());
    view.setUsername(userInfo.getUsername());
    String createdAt = formatEpochMilliToDate(userInfo.getCreationTime());
    view.setCreatedAt(createdAt);
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
