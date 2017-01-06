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
import static java.util.Collections.singletonMap;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.restart.Restarter;

import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.SessionStorage.Session;
import com.example.bot.spring.echo.TabelogService.Location;
import com.google.common.collect.ImmutableList;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapAction;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@LineMessageHandler
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EchoApplication {
    public static final String CHANGE_NAME = "CHANGE_NAME";
    public static final String CHANGE_NAME_CONFIRM = "CHANGE_NAME_CONFIRM";
    public static final String TEXT_CHANGE_NAME = "地図の名前を変える";
    private final BotConfiguration botConfiguration;
    private final TabelogService tabelogService;
    private final MapRepository mapRepository;
    private final LocationRepository locationRepository;
    private final SessionStorage sessionStorage;

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

        final String context = sessionStorage.getContext(event.getSource());

        if (text.startsWith("http")) {
            return handleUrl(event.getSource(), text);
        }
        if (text.equalsIgnoreCase("resend")) {
            return resentImage(event);
        }

        if (StringUtils.startsWithIgnoreCase(text, TEXT_CHANGE_NAME)) {
            return requestNameChange(event);
        }

        if (StringUtils.startsWithIgnoreCase(text, "Delete:")) {
            return deleteLocation(event);
        }

        if (CHANGE_NAME.equalsIgnoreCase(context)) {
            return changeName(event);
        }

        if (CHANGE_NAME_CONFIRM.equalsIgnoreCase(context)) {
            return changeNameConfirm(event);
        }

        System.out.println("event: " + event);
        return singletonList(new TextMessage(text));
    }

    private List<Message> changeNameConfirm(MessageEvent<TextMessageContent> event) {
        final java.util.Map<String, String> context = sessionStorage.getMap(event.getSource());
        sessionStorage.set(new Session(event.getSource())); // reset session

        switch (event.getMessage().getText()) {
            case "OK":
                final String newName = context.get("name");

                final List<Map> source = mapRepository.findBySource(event.getSource());
                final Map map = source.get(0).setName(newName);
                mapRepository.update(map);

                return singletonList(new TextMessage(newName + " に名前を変更しました"));
        }
        return singletonList(new TextMessage("名前の変更をキャンセルしました"));
    }

    private List<Message> requestNameChange(MessageEvent<TextMessageContent> event) {
        sessionStorage.set(new Session(event.getSource())
                                   .setContext(CHANGE_NAME));
        return singletonList(new TextMessage("新しい名前を入力して下さい"));
    }

    private List<Message> changeName(MessageEvent<TextMessageContent> event) {
        final String text = event.getMessage().getText();

        final String message = text + "に名前を変更します";
        final TemplateMessage templateMessage =
                new TemplateMessage(message,
                                    new ConfirmTemplate(message,
                                                        new MessageAction("Cancel", "Cancel"),
                                                        new MessageAction("OK", "OK")));

        sessionStorage.set(new Session(event.getSource())
                                   .setContext(CHANGE_NAME_CONFIRM)
                                   .setData(singletonMap("name", text)));

        return singletonList(templateMessage);
    }

    private List<Message> deleteLocation(MessageEvent<TextMessageContent> event) {
        final String text = event.getMessage().getText();
        final String id = text.replaceFirst("Delete:", "");
        final LocationRepository.Location deletedLocation = locationRepository.delete(id);

        return singletonList(new TextMessage("削除しました:" + deletedLocation.getTitle()));
    }

    private List<Message> resentImage(MessageEvent<TextMessageContent> event) {
        final Map map = getOrCreateMap(event.getSource());
        final List<LocationRepository.Location> locations = locationRepository.read(map._id);
        final ImagemapMessage imagemapMessage = imagemapMessage(map, locations);

        return singletonList(imagemapMessage);
    }

    private List<? extends Message> handleUrl(final Source source, final String text) {
        final URI uri = URI.create(text.replaceAll("\\?.*", ""));
        final Location location = tabelogService.getLocationMessage(uri.toString());

        final Map map = getOrCreateMap(source);
        locationRepository.create(new LocationRepository.Location()
                                          .setMapId(map.get_id())
                                          .setTitle(location.getTitle())
                                          .setUrl(uri)
                                          .setDescription(location.getDescription())
                                          .setImage(location.getImage())
                                          .setLatitude(location.getLatitude())
                                          .setLongitude(location.getLongitude()));
        log.info("{}", mapRepository.find(map.get_id()));

        final List<LocationRepository.Location> locations = locationRepository.read(map._id);

        final ImagemapMessage imagemapMessage = imagemapMessage(map, locations);

        return ImmutableList.of(new TextMessage(location.toString()),
                                imagemapMessage,
                                new LocationMessage(location.getTitle(),
                                                    "tokyo",
                                                    location.getLatitude(),
                                                    location.getLongitude()));
    }

    private Map getOrCreateMap(Source source) {
        final List<Map> bySource = mapRepository.findBySource(source);

        final Map map;
        if (bySource.isEmpty()) {
            map = new Map().setName("新しい地図")
                           .setOwner(source.getSenderId());
            mapRepository.create(map);
        } else {
            map = bySource.get(0);
        }
        return map;
    }

    private ImagemapMessage imagemapMessage(
            final Map map,
            final List<LocationRepository.Location> locations) {
        final String id = map.get_id();

        final List<ImagemapAction> actions = new ArrayList<>();

        actions.add(new MessageImagemapAction(TEXT_CHANGE_NAME, new ImagemapArea(1040 - 300, 0, 300, 176)));

        final int size = locations.size();
        for (int i = 0; i < size; ++i) {
            final LocationRepository.Location location = locations.get(i);
            final int y = 176 + 250 * i;
            final int height = 176;

            actions.add(new URIImagemapAction(location.getUrl().toString(),
                                              new ImagemapArea(0, y, 1040 - 125, height)));
            actions.add(new MessageImagemapAction("Delete:" + location.get_id(),
                                                  new ImagemapArea(1040 - 125, y, 125, height)));
        }

        final String mapUri = botConfiguration.getHost().resolve("/googlemap?id=" + map.get_id()).toString();
        actions.add(new URIImagemapAction(
                mapUri, new ImagemapArea(0, 176 + 250 * size + 186, 1040, 145)));

        return new ImagemapMessage(
                botConfiguration.getHost().resolve("/internal/mapImage?id=" + id + "&_=" + Instant.now())
                                .toString(),
                map.getName(),
                new ImagemapBaseSize(250 * size + 509, 1040),
                actions);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {

        System.out.println("event: " + event);
    }

}
