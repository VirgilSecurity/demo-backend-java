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

package com.virgilsecurity.demo.server.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.virgilsecurity.demo.server.model.message.Message;

import org.springframework.stereotype.Service;

/**
 * MessageService class.
 */
@Service
public class MessageService {

  // Only last message for each identity
  private List<Message> messages;

  public MessageService() {
    this.messages = new CopyOnWriteArrayList<>();
  }

  public void sendMessage(String sender, String recipient, String body) {
    Message messageNew = new Message(sender, recipient, body);
    for (Message message : this.messages) {
      if (message.getSender().equals(sender) && message.getRecipient().equals(recipient)) {
        // Hold only last message
        this.messages.remove(message);
        this.messages.add(messageNew);
      }
    }

    // If the old one hasn't been replaced with the new one
    if (!this.messages.contains(messageNew)) {
      this.messages.add(messageNew);
    }
  }

  public Message receiveMessage(String sender) {
    for (Message message : this.messages) {
      if (message.getSender().equals(sender)) {
        return message;
      }
    }

    return null;
  }
}
