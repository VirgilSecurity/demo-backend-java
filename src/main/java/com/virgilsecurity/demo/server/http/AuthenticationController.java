package com.virgilsecurity.demo.server.http;

import com.virgilsecurity.demo.server.model.VirgilToken;
import com.virgilsecurity.demo.server.service.AuthenticationService;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.Jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

  @Autowired
  AuthenticationService authService;

  @RequestMapping("/auth")
  public String login(@RequestParam("identity") String identity) {
    return authService.login(identity);
  }

  @RequestMapping("/virgil-jwt")
  public ResponseEntity<VirgilToken> getVirgilToken(
      @RequestHeader(name = "auth_token", required = false) String authToken)
      throws CryptoException {
    String identity = authService.getIdentity(authToken);
    if (identity == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    Jwt token = authService.generateVirgilToken(identity);
    return new ResponseEntity<>(new VirgilToken(token.stringRepresentation()), HttpStatus.OK);
  }
}
