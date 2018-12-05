package com.virgilsecurity.demo.server;

import com.virgilsecurity.sdk.common.TimeSpan;
import com.virgilsecurity.sdk.crypto.AccessTokenSigner;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.VirgilAccessTokenSigner;
import com.virgilsecurity.sdk.crypto.VirgilCrypto;
import com.virgilsecurity.sdk.crypto.exceptions.CryptoException;
import com.virgilsecurity.sdk.jwt.JwtGenerator;
import com.virgilsecurity.sdk.utils.ConvertionUtils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServerApplication {

  @Value("${virgil.app.id}")
  String appId;

  @Value("${virgil.api.private_key}")
  String apiKey;

  @Value("${virgil.api.key_id}")
  String apiKeyIdentifier;

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }

  @Bean
  public JwtGenerator jwtGenerator() throws CryptoException {
    VirgilCrypto crypto = new VirgilCrypto();
    PrivateKey privateKey = crypto.importPrivateKey(ConvertionUtils.base64ToBytes(this.apiKey));
    AccessTokenSigner accessTokenSigner = new VirgilAccessTokenSigner();

    JwtGenerator jwtGenerator = new JwtGenerator(appId, privateKey, apiKeyIdentifier,
        TimeSpan.fromTime(1, TimeUnit.HOURS), accessTokenSigner);

    return jwtGenerator;
  }
}
