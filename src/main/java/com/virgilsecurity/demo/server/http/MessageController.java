/*
 * Copyright (c) 2015-2019, Virgil Security, Inc.
 *
 * Lead Maintainer: Virgil Security Inc. <support@virgilsecurity.com>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     (1) Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *     (2) Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *     (3) Neither the name of virgil nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.virgilsecurity.demo.server.http;

import com.virgilsecurity.demo.server.model.AuthResponse;
import com.virgilsecurity.demo.server.model.message.Message;
import com.virgilsecurity.demo.server.model.message.ReceiveMessageResponse;
import com.virgilsecurity.demo.server.model.message.SendMessageRequest;
import com.virgilsecurity.demo.server.service.AuthenticationService;
import com.virgilsecurity.demo.server.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MessageController class.
 */
@RestController
public class MessageController {

  @Autowired
  MessageService messageService;
  @Autowired
  AuthenticationService authService;

  @PostMapping
  @RequestMapping("/sendMessage")
  public ResponseEntity<AuthResponse> login(
//      @RequestHeader(name = "Authorization", required = false) String authToken,
      @RequestBody SendMessageRequest sendMessageRequest
  ) {
//    String identity = authService.getIdentity(authToken);
//    if (identity == null) {
//      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }

    messageService.sendMessage(sendMessageRequest.getSender(),
                               sendMessageRequest.getRecipient(),
                               sendMessageRequest.getBody());

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping("/receiveMessage")
  public ResponseEntity<ReceiveMessageResponse> getVirgilToken(
//      @RequestHeader(name = "Authorization", required = false) String authToken,
      @RequestParam("sendedFor") String sendedFor
  ) {

//    String identity = authService.getIdentity(authToken);
//    if (identity == null) {
//      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//    }

    try {
      Message message = messageService.receiveMessage(sendedFor);
      return new ResponseEntity<>(new ReceiveMessageResponse(message), HttpStatus.OK);
    } catch (Throwable throwable) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
