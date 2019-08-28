package com.virgilsecurity.demo.server.http;

import com.virgilsecurity.demo.server.model.AuthRequest;
import com.virgilsecurity.demo.server.model.AuthResponse;
import com.virgilsecurity.demo.server.model.VirgilTokenResponse;
import com.virgilsecurity.demo.server.service.AuthenticationService;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

  @Autowired
  AuthenticationService authService;

  @PostMapping
  @RequestMapping("/authenticate")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
    String authToken = authService.login(authRequest.getIdentity());
    return new ResponseEntity<>(new AuthResponse(authToken), HttpStatus.OK);
  }

  @RequestMapping("/virgil-jwt")
  public ResponseEntity<VirgilTokenResponse> getVirgilToken(
      @RequestHeader(name = "Authorization", required = false) String authToken)
      throws CryptoException {
    String identity = authService.getIdentity(authToken);
    if (identity == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    Jwt token = authService.generateVirgilToken(identity);
    return new ResponseEntity<>(new VirgilTokenResponse(token.stringRepresentation()),
                                HttpStatus.OK);
  }
}
