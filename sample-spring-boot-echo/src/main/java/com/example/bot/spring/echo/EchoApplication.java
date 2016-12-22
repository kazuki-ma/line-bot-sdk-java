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
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.restart.Restarter;

import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.TabelogService.Location;
import com.google.common.collect.ImmutableList;

import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapAction;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@LineMessageHandler
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EchoApplication {
    private final TabelogService tabelogService;
    private final MapRepository mapRepository;
    private final LocationRepository locationRepository;

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
            return handleUrl(event.getSource(), text);
        }

        System.out.println("event: " + event);
        return singletonList(new TextMessage(text));
    }

    private List<? extends Message> handleUrl(final Source source, final String text) {
        final Location location = tabelogService.getLocationMessage(URI.create(text).toString());

        final List<Map> bySource = mapRepository.findBySource(source);

        final Map map;
        if (bySource.isEmpty()) {
            map = new Map().setName("新しい地図")
                           .setOwner(source.getSenderId());
            mapRepository.create(map);
        } else {
            map = bySource.get(0);
        }
        locationRepository.create(new LocationRepository.Location()
                                          .setMapId(map.get_id())
                                          .setTitle(location.getTitle())
                                          .setDescription(location.getDescription())
                                          .setImage(location.getImage())
                                          .setLatitude(location.getLatitude())
                                          .setLongitude(location.getLongitude()));
        log.info("{}", mapRepository.find(map.get_id()));

        final List<LocationRepository.Location> locations = locationRepository.read(map._id);

        final ImagemapMessage imagemapMessage = imagemapMessage(map, locations);

        return ImmutableList.of(new TextMessage(location.toString()),
                                new LocationMessage(location.getTitle(),
                                                    "tokyo",
                                                    location.getLatitude(),
                                                    location.getLongitude()));
    }

    private ImagemapMessage imagemapMessage(
            final Map map,
            final List<LocationRepository.Location> locations) {
        final String id = map.get_id();

        final List<ImagemapAction> actions = new ArrayList<>();

        for (int i = 0; i < locations.size(); ++i) {
            final LocationRepository.Location location = locations.get(i);

            new MessageImagemapAction("delete" + location.get_id(),
                                      new ImagemapArea(17));
            actions.add(new PostbackAction("delete", location.get_id()));
        }

        new ImagemapMessage("", "", new ImagemapBaseSize(1040, 1040)
                , )
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {

        System.out.println("event: " + event);
    }

}
