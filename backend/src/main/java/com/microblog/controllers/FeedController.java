package com.microblog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microblog.dto.PostView;
import com.microblog.services.FeedService;

@RestController
@RequestMapping("/feed")
public class FeedController {

  @Autowired
  private FeedService feedService;

  @GetMapping("/public")
  public Page<PostView> getPublicFeed(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return feedService.getPublicFeed(page, size);
  }

  // @GetMapping("/following")
  // public Page<PostView> getFollowingFeed(
  // @RequestParam(defaultValue = "0") int page,
  // @RequestParam(defaultValue = "10") int size) {
  // return feedService.getFollowingFeed(page, size);
  // }
}
