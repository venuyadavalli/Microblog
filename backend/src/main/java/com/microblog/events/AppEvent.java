package com.microblog.events;

import java.time.Instant;

import org.springframework.context.ApplicationEvent;

import com.microblog.models.User;

public abstract class AppEvent<T> extends ApplicationEvent {

  private final String type;
  private User actor;
  private final T payload;
  private final Instant timestamp;
  private final String traceId;

  protected AppEvent(Object source, String type, User actor, T payload, String traceId) {
    super(source);
    this.type = type;
    this.actor = actor;
    this.payload = payload;
    this.traceId = traceId;
    this.timestamp = Instant.now();
  }

  public String getType() {
    return type;
  }

  public User getActor() {
    return actor;
  }

  public void setActor(User actor) {
    this.actor = actor;
  }

  public Instant getEventTimestamp() {
    return timestamp;
  }

  public String getTraceId() {
    return traceId;
  }

  public T getPayload() {
    return payload;
  }
}
