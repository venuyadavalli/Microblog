package com.microblog.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ping")
public class PingController {

  @GetMapping
  public String sayPong() {
    return "pong";
  }

}
