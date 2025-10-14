package com.microblog.demo;

import java.util.concurrent.Executors;

// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

// @CrossOrigin(origins = "http://localhost:4200")

@RestController
public class SseController {

	@GetMapping("/sse/stream")
	public SseEmitter stream() {
		SseEmitter emitter = new SseEmitter();
		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				emitter.send(SseEmitter.event().data("hello from backend"));
				Thread.sleep(2000);
				// Close after a short demo.
				emitter.complete();
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return emitter;
	}
}
