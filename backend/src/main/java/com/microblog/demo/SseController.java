package com.microblog.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
public class SseController {

	@Autowired
	private SseService sseService;

	@GetMapping("/subscribe")
	public SseEmitter subscribe() {
		return sseService.createEmitter();
	}
}
