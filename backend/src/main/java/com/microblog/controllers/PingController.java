package com.microblog.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ping")
public class PingController {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @GetMapping
  public String sayPong() {
    return "pong";
  }

  @GetMapping(value = "/db")
  public String pingDb() {
    try {
      jdbcTemplate.execute("SELECT 1");
      return "pong (DB OK)";
    } catch (Exception e) {
      return "pong (DB FAIL): " + e.getMessage();
    }
  }

}
