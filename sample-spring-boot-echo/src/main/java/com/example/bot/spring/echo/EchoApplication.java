/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.restart.Restarter;

import com.example.bot.spring.echo.TabelogService.Location;
import com.google.common.collect.ImmutableList;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.AllArgsConstructor;

@SpringBootApplication
@LineMessageHandler
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EchoApplication {
    private final TabelogService tabelogService;

    public static void main(String[] args) {
        Restarter.initialize(args, false, ignored -> null, false);
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public List<? extends Message> handleJoinEvent(final JoinEvent event) {
        return ImmutableList.of(new TextMessage(event.toString()),
                                new ImagemapMessage("https://entrnwynqk.localtunnel.me/test",
                                                    "TETS",
                                                    new ImagemapBaseSize(1040, 1040),
                                                    emptyList()));
    }

    @EventMapping
    public List<? extends Message> handleTextMessageEvent(MessageEvent<TextMessageContent> event)
            throws Exception {
        String text = event.getMessage().getText();

        if (text.startsWith("http")) {
            return handleUrl(text);
        }

        System.out.println("event: " + event);
        return singletonList(new TextMessage(text));
    }

    private List<? extends Message> handleUrl(String text) {
        final Location location = tabelogService.getLocationMessage(URI.create(text).toString());

        return ImmutableList.of(new TextMessage(location.toString()),
                                new LocationMessage(location.getTitle(),
                                                    "tokyo",
                                                    location.getLatitude(),
                                                    location.getLongtitude()));
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {

        System.out.println("event: " + event);
    }
}
