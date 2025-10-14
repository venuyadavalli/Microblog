package com.microblog.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PingEventSubscriber {

  @Autowired
  private SseService sseService;

  @EventListener
  public void onPingEvent(PingEvent event) {
    sseService.sendPing(event.getMessage());
  }
}
