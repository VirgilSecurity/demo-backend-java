package com.virgilsecurity.demo.server.model;

public class AuthenticationTokenData {

  private String authToken;

  public AuthenticationTokenData() {
  }

  public AuthenticationTokenData(String token) {
    this.authToken = token;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String token) {
    this.authToken = token;
  }

}
