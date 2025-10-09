package com.microblog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.apache.tomcat.util.http.SameSiteCookies;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  public record RegisterRequest(String email, String password) {
  }

  public record LoginRequest(String idToken) {
  }

  @PostMapping("/register")
  public Map<String, Object> register(@RequestBody RegisterRequest request) throws FirebaseAuthException {
    return authService.register(request.email(), request.password());
  }

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody LoginRequest request, HttpServletResponse response)
      throws FirebaseAuthException {
    String sessionCookie = authService.createSessionCookie(request.idToken());
    ResponseCookie cookie = ResponseCookie.from("jwt", sessionCookie)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(60 * 60)
        .sameSite(SameSiteCookies.LAX.toString())
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    FirebaseToken decoded = FirebaseAuth.getInstance().verifySessionCookie(sessionCookie);
    return Map.of("status", "logged_in", "uid", decoded.getUid(), "email", decoded.getEmail());
  }

  @PostMapping("/logout")
  public Map<String, Object> logout(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("jwt", "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite(SameSiteCookies.LAX.toString())
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return Map.of("status", "logged_out");
  }

  @GetMapping("/me")
  public Map<String, Object> me(@CookieValue(name = "session", required = false) String sessionCookie)
      throws FirebaseAuthException {
    if (sessionCookie == null) {
      return Map.of("error", "Not authenticated");
    }
    FirebaseToken decoded = FirebaseAuth.getInstance().verifySessionCookie(sessionCookie);
    return Map.of("uid", decoded.getUid(), "email", decoded.getEmail());
  }

  @DeleteMapping("/delete")
  public Map<String, Object> delete(@CookieValue(name = "session", required = false) String sessionCookie,
      HttpServletResponse response) throws FirebaseAuthException {
    if (sessionCookie == null) {
      return Map.of("error", "Not authenticated");
    }
    FirebaseToken decoded = FirebaseAuth.getInstance().verifySessionCookie(sessionCookie);
    FirebaseAuth.getInstance().revokeRefreshTokens(decoded.getUid());
    authService.deleteUser(decoded.getUid());
    Cookie clearCookie = new Cookie("session", "");
    clearCookie.setHttpOnly(true);
    clearCookie.setSecure(true);
    clearCookie.setPath("/");
    clearCookie.setMaxAge(0);
    response.addCookie(clearCookie);
    return Map.of("status", "deleted", "uid", decoded.getUid(), "email", decoded.getEmail());
  }
}
