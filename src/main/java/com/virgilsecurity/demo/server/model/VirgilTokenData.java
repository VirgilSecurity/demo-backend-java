package com.virgilsecurity.demo.server.model;

public class VirgilTokenData {

  private String virgilToken;

  public VirgilTokenData() {
  }

  public VirgilTokenData(String token) {
    this.virgilToken = token;
  }

  public String getVirgilToken() {
    return virgilToken;
  }

  public void setVirgilToken(String token) {
    this.virgilToken = token;
  }

}
