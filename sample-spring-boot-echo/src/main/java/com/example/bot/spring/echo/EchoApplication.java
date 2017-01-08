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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.restart.Restarter;

import com.example.bot.spring.echo.MapActionRequest.MapAction;
import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.PostbackUtil.PostbackType;
import com.example.bot.spring.echo.SessionStorage.Session;
import com.example.bot.spring.echo.googlemaps.GoogleMapsService;
import com.google.common.collect.ImmutableList;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
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
    public static final PostbackType<MapActionRequest> MAP_ACTION =
            new PostbackType<>("MAP_ACT", MapActionRequest.class);
    private final BotConfiguration botConfiguration;
    private final com.example.bot.spring.echo.fetcher.OGPService OGPService;
    private final GoogleMapsService googleMapsService;
    private final MapRepository mapRepository;
    private final LocationRepository locationRepository;
    private final SessionStorage sessionStorage;
    private final AddHandler addHandler;
    private final ImageMapGenerator imageMapGenerator;
    private final CarouselGenerator carouselGenerator;

    public static void main(String[] args) {
        Restarter.initialize(args, false, ignored -> null, false);
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public List<? extends Message> handleJoinEvent(final JoinEvent event) {
        return ImmutableList.of(new TextMessage("食べログ・Google Map の URL を送信すると、地図を作成します"));
    }

    @EventMapping
    public List<? extends Message> handleTextMessageEvent(
            final MessageEvent<TextMessageContent> event) throws Exception {
        String text = event.getMessage().getText();

        final String context = sessionStorage.getContext(event.getSource());

        if (StringUtils.containsIgnoreCase(text, "goo.gl/maps")
            || StringUtils.containsIgnoreCase(text, "www.google.co.jp/maps/place")
            || text.startsWith("http")) {
            return getMessages(event, text);
        }

        if (text.equalsIgnoreCase("resend") || text.equalsIgnoreCase("地図")) {
            return resentImage(event);
        }

        if (StringUtils.startsWithIgnoreCase(text, "Delete:")) {
            return deleteLocation(event);
        }

        log.info("event: {} from {}", event.getMessage(), event.getSource());

        sessionStorage.delete(event.getSource());

        return emptyList();
    }

    private List<? extends Message> getMessages(MessageEvent<TextMessageContent> event, String text) {
        final Source source = event.getSource();
        final Location locationFromUri;
        final URI uri;

        if (text.contains("tabelog")) {
            uri = URI.create(text.replaceAll("\\?.*", ""));
            locationFromUri = OGPService.getLocationMessage(uri.toString());

//            locationRepository.create(location);
//            log.info("{}", mapRepository.find(map.get_id()));
//
//            final ImagemapMessage imagemapMessage = imagemapMessage(map);
//
//            return ImmutableList.of(new TextMessage(locationFromUri.toString()),
//                                    imagemapMessage,
        } else {
            final Matcher matcher =
                    Pattern.compile("https://(goo.gl|www.google)\\p{ASCII}+")
                           .matcher(event.getMessage().getText());

            matcher.find();

            uri = URI.create(matcher.group(0));
            locationFromUri =
                    uri.getHost().equalsIgnoreCase("goo.gl")
                    ? googleMapsService.getLocationFromShortUri("", uri)
                    : googleMapsService.getLocationFromLongUri("", uri);

        }

        final Map map = getOrCreateMap(source);
        final LocationRepository.Location location = new LocationRepository.Location()
                .setMapId(/* map.get_id() */ null) // null here
                .setTitle(locationFromUri.getTitle())
                .setUrl(uri)
                .setDescription(locationFromUri.getDescription())
                .setImage(locationFromUri.getImage())
                .setLatitude(locationFromUri.getLatitude())
                .setLongitude(locationFromUri.getLongitude());

        locationRepository.create(location);

        final LocationMessage locationMessage
                = new LocationMessage(locationFromUri.getTitle(),
                                      "tokyo",
                                      locationFromUri.getLatitude(),
                                      locationFromUri.getLongitude());

        return asList(locationMessage,
                      addHandler.selectMap(source, location));
    }

    private ImagemapMessage imagemapMessage(Map map) {
        return imageMapGenerator.create(map);
    }

    private List<Message> deleteLocation(MessageEvent<TextMessageContent> event) {
        final String text = event.getMessage().getText();
        final String id = text.replaceFirst("Delete:", "");
        final LocationRepository.Location deletedLocation = locationRepository.delete(id);

        final ButtonsTemplate template =
                new ButtonsTemplate(null, null,
                                    "削除しました:" + deletedLocation.getTitle(),
                                    singletonList(new MessageAction("取り消し",
                                                                    deletedLocation.getUrl().toString())));
        final TemplateMessage deletedMessage = new TemplateMessage("削除しました", template);

        return ImmutableList.of(deletedMessage,
                                imagemapMessage(getOrCreateMap(event.getSource())));
    }

    private List<Message> resentImage(MessageEvent<TextMessageContent> event) {
        final List<Map> maps = mapRepository.findBySource(event.getSource());
        if (maps.isEmpty()) {
            return emptyList();
        }
        if (maps.size() == 1) {
            final Map map = maps.get(0);
            return singletonList(imagemapMessage(map));
        }

        final TemplateMessage templateMessage = getTemplateMessage(maps);
        return singletonList(templateMessage);
    }

    private TemplateMessage getTemplateMessage(List<Map> maps) {
        final List<CarouselColumn> carouselColumns = carouselGenerator.mapToColumn(maps, map -> {
            final PostbackAction nameAction =
                    new PostbackAction(map.getName() + "を確認",
                                       PostbackUtil.encode(MAP_ACTION, new MapActionRequest()
                                               .setMapId(map.get_id())
                                               .setAction(MapAction.LIST)),
                                       "『" + map.getName() + "』を確認");

            final URIAction showMapAction
                    = new URIAction("まとめて地図で確認",
                                    botConfiguration.getHost().resolve("/m/" + map.get_id()).toString());

            final PostbackAction deleteMapAction =
                    new PostbackAction("地図を削除する",
                                       PostbackUtil.encode(MAP_ACTION, new MapActionRequest()
                                               .setMapId(map.get_id())
                                               .setAction(MapAction.DELETE)),
                                       "『" + map.getName() + "』を削除する");

            return asList(nameAction, showMapAction, deleteMapAction);
        });

        return new TemplateMessage("地図の一覧 (SP専用)", new CarouselTemplate(carouselColumns));
    }

    @EventMapping(postbackPrefix = "MAP_ACT")
    public List<Message> handleMapAction(PostbackEvent event) {
        final MapActionRequest mapActionRequest =
                PostbackUtil.decode(MAP_ACTION, event.getPostbackContent().getData());

        final Map map = mapRepository.find(mapActionRequest.getMapId());

        switch (mapActionRequest.getAction()) {
            case LIST:
                return singletonList(imagemapMessage(map));
            case DELETE:
                sessionStorage.set(new Session(event.getSource())
                                           .setContext("DELETE_MAP_CONFIRM")
                                           .setData(singletonMap("mapId", map.get_id())));

                final String confirmPostback =
                        PostbackUtil.encode(MAP_ACTION,
                                            new MapActionRequest().setMapId(map.get_id())
                                                                  .setAction(MapAction.DELETE_CONFIRMED));

                final ConfirmTemplate confirmTemplate =
                        new ConfirmTemplate(map.getName() + "を削除します",
                                            new MessageAction("キャンセル", "キャンセル"),
                                            new PostbackAction("OK", confirmPostback, "OK"));

                return singletonList(
                        new TemplateMessage(map.getName() + "を削除する場合は OK",
                                            confirmTemplate));
            case DELETE_CONFIRMED:
                sessionStorage.delete(event.getSource());
                final Map deletedMap = mapRepository.delete(mapActionRequest.getMapId());

                List<Message> messages = new ArrayList<>();
                messages.add(new TextMessage(deletedMap.getName() + "を削除しました"));

                final List<Map> maps = mapRepository.findBySource(event.getSource());
                if (!maps.isEmpty()) {
                    messages.add(getTemplateMessage(maps));
                }

                return messages;
        }
        sessionStorage.delete(event.getSource());
        return emptyList();
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

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
