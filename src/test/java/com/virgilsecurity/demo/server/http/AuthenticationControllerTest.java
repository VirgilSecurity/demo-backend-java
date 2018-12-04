package com.virgilsecurity.demo.server.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.virgilsecurity.demo.server.model.VirgilToken;

import java.net.URI;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

  @LocalServerPort
  int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String identity;

  @Before
  public void setup() {
    this.identity = UUID.randomUUID().toString();
  }

  @Test
  public void login() {
    URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/auth")
        .queryParam("identity", this.identity).build().encode().toUri();
    String authToken = this.restTemplate.getForObject(uri, String.class);
    assertNotNull(authToken);
    assertTrue(authToken.startsWith(this.identity));
  }

  @Test
  public void generateToken_noLogin() {
    URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/virgil-jwt").build()
        .encode().toUri();
    HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<VirgilToken> responseEntity = this.restTemplate.exchange(uri, HttpMethod.GET,
        requestEntity, VirgilToken.class);
    assertNotNull(responseEntity);
    assertEquals(401, responseEntity.getStatusCode().value());
  }

  @Test
  public void generateToken() {
    URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/auth")
        .queryParam("identity", this.identity).build().encode().toUri();
    String authToken = this.restTemplate.getForObject(uri, String.class);

    uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/virgil-jwt").build()
        .encode().toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.add("auth_token", authToken);
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<VirgilToken> responseEntity = this.restTemplate.exchange(uri, HttpMethod.GET,
        requestEntity, VirgilToken.class);
    assertNotNull(responseEntity);
    assertEquals(200, responseEntity.getStatusCode().value());

    VirgilToken virgilToken = responseEntity.getBody();
    assertNotNull(virgilToken);
    assertNotNull(virgilToken.getJwt());
  }

}
