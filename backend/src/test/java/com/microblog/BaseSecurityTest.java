package com.microblog;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.firebase.auth.FirebaseToken;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSecurityTest {

  @LocalServerPort
  protected int port;

  @Value("${microblog.api.baseurl:http://localhost}")
  protected String baseUrl;

  @Autowired
  protected TestRestTemplate restTemplate;

  protected FirebaseToken mockToken;
  protected String accessToken;

  @BeforeEach
  void setupMockAuthentication() {
    String uid = "rEHQ70MjKud1obRiJc0QItQGEFk1";
    String email = "jake@gmail.com";

    mockToken = mock(FirebaseToken.class);
    when(mockToken.getUid()).thenReturn(uid);
    when(mockToken.getEmail()).thenReturn(email);
    when(mockToken.getName()).thenReturn("na-jake");
    when(mockToken.isEmailVerified()).thenReturn(true);

    

    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    FirebaseAuthenticationToken authenticationToken = new FirebaseAuthenticationToken(mockToken, authorities);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  }
}
