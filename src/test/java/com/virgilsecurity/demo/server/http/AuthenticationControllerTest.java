package com.virgilsecurity.demo.server.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.virgilsecurity.demo.server.model.AuthRequest;
import com.virgilsecurity.demo.server.model.AuthResponse;
import com.virgilsecurity.demo.server.model.VirgilTokenResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
    public void login() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + port + "/authenticate";
        URI uri = new URI(baseUrl);
        AuthRequest authRequest = new AuthRequest(identity);

        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(uri, request, AuthResponse.class);

        assertEquals(200, response.getStatusCodeValue());
        String authToken = response.getBody().getAuthToken();
        assertNotNull(authToken);
        assertTrue(authToken.startsWith(this.identity));
    }

    @Test
    public void generateToken_noLogin() {
        URI uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/virgil-jwt").build().encode().toUri();
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<VirgilTokenResponse> responseEntity = this.restTemplate.exchange(uri,
                                                                                        HttpMethod.GET,
                                                                                        requestEntity,
                                                                                        VirgilTokenResponse.class);
        assertNotNull(responseEntity);
        assertEquals(401, responseEntity.getStatusCode().value());
    }

    @Test
    public void generateToken() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + port + "/authenticate";
        URI uri = new URI(baseUrl);
        AuthRequest authRequest = new AuthRequest(identity);

        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(uri, request, AuthResponse.class);

        assertEquals(200, response.getStatusCodeValue());
        String authToken = response.getBody().getAuthToken();

        uri = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/virgil-jwt").build()
                                  .encode().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authToken);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<VirgilTokenResponse> responseEntity = this.restTemplate.exchange(uri,
                                                                                        HttpMethod.GET,
                                                                                        requestEntity,
                                                                                        VirgilTokenResponse.class);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());

        VirgilTokenResponse virgilTokenResponse = responseEntity.getBody();
        assertNotNull(virgilTokenResponse);
        assertNotNull(virgilTokenResponse.getVirgilToken());
    }

}
