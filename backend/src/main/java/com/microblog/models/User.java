package com.microblog.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class User {

  @Id
  @Column(name = "id", nullable = false, unique = true)
  private String id;

  @Column(name = "username", nullable = false, length = 64, unique = true)
  private String username;

  public User() {
  }

  public User(String id, String username) {
    this.id = id;
    this.username = username;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
