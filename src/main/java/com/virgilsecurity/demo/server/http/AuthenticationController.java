package com.virgilsecurity.demo.server.http;

import com.virgilsecurity.demo.server.model.AuthenticationData;
import com.virgilsecurity.demo.server.model.TokenData;
import com.virgilsecurity.demo.server.service.AuthenticationService;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.Jwt;
import com.virgilsecurity.sdk.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

  @Autowired
  AuthenticationService authService;

  @CrossOrigin(origins = "*")
  @RequestMapping(path = "/authenticate", method = RequestMethod.POST)
  public TokenData login(@RequestBody(required = false) AuthenticationData body) {
    return new TokenData(authService.login(body.getIdentity()));
  }

  @CrossOrigin(origins = "*")
  @RequestMapping("/virgil-jwt")
  public ResponseEntity<TokenData> getVirgilToken(
      @RequestHeader(name = "Authorization", required = false) String authHeader)
      throws CryptoException {
    String identity = authService.getIdentity(extractToken(authHeader));
    if (identity == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    Jwt token = authService.generateVirgilToken(identity);
    return new ResponseEntity<>(new TokenData(token.stringRepresentation()), HttpStatus.OK);
  }

  private String extractToken(String authHeader) {
    if (StringUtils.isBlank(authHeader)) {
      return null;
    }
    return authHeader.replace("Bearer ", "");
  }
}
