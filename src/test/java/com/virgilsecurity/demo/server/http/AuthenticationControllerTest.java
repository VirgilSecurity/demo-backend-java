package com.virgilsecurity.demo.server.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virgilsecurity.demo.server.model.TokenData;

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
import org.springframework.util.MimeTypeUtils;

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
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);

    JsonObject body = new JsonObject();
    body.addProperty("identity", this.identity);
    HttpEntity<?> requestEntity = new HttpEntity<>(body.toString(), headers);

    ResponseEntity<String> responseEntity = this.restTemplate.exchange(getAuthUrl(),
        HttpMethod.POST, requestEntity, String.class);
    assertNotNull(responseEntity);
    assertEquals(200, responseEntity.getStatusCode().value());

    JsonObject json = (JsonObject) new JsonParser().parse(responseEntity.getBody());
    String token = json.get("token").getAsString();
    assertNotNull(token);
    assertTrue(token.startsWith(this.identity));
  }

  @Test
  public void generateToken_noLogin() {
    // Try to obtain Virgil JWT by unauthorized user
    HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<TokenData> responseEntity = this.restTemplate.exchange(getJwtUrl(),
        HttpMethod.GET, requestEntity, TokenData.class);
    assertNotNull(responseEntity);
    assertEquals(401, responseEntity.getStatusCode().value());
  }

  @Test
  public void generateToken_wrongAuthToken() {
    // Try to obtain Virgil JWT with invalid authentication token
    HttpHeaders jwtHeaders = new HttpHeaders();
    jwtHeaders.add("Authorization", "Bearer " + UUID.randomUUID().toString());
    HttpEntity<?> jwtRequestEntity = new HttpEntity<>(jwtHeaders);
    ResponseEntity<TokenData> jwtResponseEntity = this.restTemplate.exchange(getJwtUrl(),
        HttpMethod.GET, jwtRequestEntity, TokenData.class);
    assertNotNull(jwtResponseEntity);
    assertEquals(401, jwtResponseEntity.getStatusCode().value());
  }

  @Test
  public void generateToken() {
    HttpHeaders authHeaders = new HttpHeaders();
    authHeaders.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);

    JsonObject authBody = new JsonObject();
    authBody.addProperty("identity", this.identity);
    HttpEntity<?> authRequestEntity = new HttpEntity<>(authBody.toString(), authHeaders);

    ResponseEntity<TokenData> authResponseEntity = this.restTemplate.exchange(getAuthUrl(),
        HttpMethod.POST, authRequestEntity, TokenData.class);
    assertNotNull(authResponseEntity);
    assertEquals(200, authResponseEntity.getStatusCode().value());

    TokenData tokenData = authResponseEntity.getBody();
    assertNotNull(tokenData);
    assertNotNull(tokenData.getToken());

    HttpHeaders jwtHeaders = new HttpHeaders();
    jwtHeaders.add("Authorization", "Bearer " + tokenData.getToken());
    HttpEntity<?> jwtRequestEntity = new HttpEntity<>(jwtHeaders);
    ResponseEntity<TokenData> jwtResponseEntity = this.restTemplate.exchange(getJwtUrl(),
        HttpMethod.GET, jwtRequestEntity, TokenData.class);
    assertNotNull(jwtResponseEntity);
    assertEquals(200, jwtResponseEntity.getStatusCode().value());

    TokenData virgilToken = jwtResponseEntity.getBody();
    assertNotNull(virgilToken);
    assertNotNull(virgilToken.getToken());
  }

  private String getAuthUrl() {
    return "http://localhost:" + port + "/authenticate";
  }

  private String getJwtUrl() {
    return "http://localhost:" + port + "/virgil-jwt";
  }

}
