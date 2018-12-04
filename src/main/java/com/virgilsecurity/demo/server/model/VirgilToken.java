package com.virgilsecurity.demo.server.model;

public class VirgilToken {

  private String jwt;

  public VirgilToken() {
  }

  public VirgilToken(String jwt) {
    this.jwt = jwt;
  }

  public String getJwt() {
    return jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

}
