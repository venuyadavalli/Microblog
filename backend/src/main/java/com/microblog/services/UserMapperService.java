package com.microblog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microblog.dto.UserInfo;
import com.microblog.dto.UserItemView;
import com.microblog.dto.UserProfileView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapperService {

  @Autowired
  private FollowService followService;

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

}
