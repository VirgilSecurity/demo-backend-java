package com.virgilsecurity.demo.server.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virgilsecurity.demo.server.model.AuthenticationTokenData;
import com.virgilsecurity.demo.server.model.VirgilTokenData;

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
    String token = json.get("authToken").getAsString();
    assertNotNull(token);
    assertTrue(token.startsWith(this.identity));
  }

  @Test
  public void generateToken_noLogin() {
    // Try to obtain Virgil JWT by unauthorized user
    HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<VirgilTokenData> responseEntity = this.restTemplate.exchange(getJwtUrl(),
        HttpMethod.GET, requestEntity, VirgilTokenData.class);
    assertNotNull(responseEntity);
    assertEquals(401, responseEntity.getStatusCode().value());
  }

  @Test
  public void generateToken_wrongAuthToken() {
    // Try to obtain Virgil JWT with invalid authentication token
    HttpHeaders jwtHeaders = new HttpHeaders();
    jwtHeaders.add("Authorization", "Bearer " + UUID.randomUUID().toString());
    HttpEntity<?> jwtRequestEntity = new HttpEntity<>(jwtHeaders);
    ResponseEntity<VirgilTokenData> jwtResponseEntity = this.restTemplate.exchange(getJwtUrl(),
        HttpMethod.GET, jwtRequestEntity, VirgilTokenData.class);
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

    ResponseEntity<AuthenticationTokenData> authResponseEntity = this.restTemplate
        .exchange(getAuthUrl(), HttpMethod.POST, authRequestEntity, AuthenticationTokenData.class);
    assertNotNull(authResponseEntity);
    assertEquals(200, authResponseEntity.getStatusCode().value());

    AuthenticationTokenData authTokenData = authResponseEntity.getBody();
    assertNotNull(authTokenData);
    assertNotNull(authTokenData.getAuthToken());

    HttpHeaders jwtHeaders = new HttpHeaders();
    jwtHeaders.add("Authorization", "Bearer " + authTokenData.getAuthToken());
    HttpEntity<?> jwtRequestEntity = new HttpEntity<>(jwtHeaders);
    ResponseEntity<VirgilTokenData> jwtResponseEntity = this.restTemplate.exchange(getJwtUrl(),
        HttpMethod.GET, jwtRequestEntity, VirgilTokenData.class);
    assertNotNull(jwtResponseEntity);
    assertEquals(200, jwtResponseEntity.getStatusCode().value());

    VirgilTokenData virgilToken = jwtResponseEntity.getBody();
    assertNotNull(virgilToken);
    assertNotNull(virgilToken.getVirgilToken());
  }

  private String getAuthUrl() {
    return "http://localhost:" + port + "/authenticate";
  }

  private String getJwtUrl() {
    return "http://localhost:" + port + "/virgil-jwt";
  }

}
