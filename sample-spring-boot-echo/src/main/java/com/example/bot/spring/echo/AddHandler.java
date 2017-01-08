package com.example.bot.spring.echo;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bot.spring.echo.LocationRepository.Location;
import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.PostbackUtil.PostbackType;
import com.example.bot.spring.echo.SessionStorage.Session;

import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@LineMessageHandler
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AddHandler {
    public static final String SELECT_MAP_FOR_NEW_LOCATION = "SELECT_MAP_FOR_NEW_LOCATION";
    private static final PostbackType<MapLocationPair> MAP_LOCATION_PAIR_TYPE =
            new PostbackType<>("ML_PAIR", MapLocationPair.class);

    private final BotConfiguration botConfiguration;
    private final LocationRepository locationRepository;
    private final MapRepository mapRepository;
    private final SessionStorage sessionStorage;
    private final ImageMapGenerator imageMapGenerator;
    private final CarouselGenerator carouselGenerator;

    /**
     * Send carousel message before adding location.
     */
    public TemplateMessage selectMap(
            final Source source,
            final Location location) {
        final List<Map> maps = mapRepository.findBySource(source);

        final List<CarouselColumn> columns = carouselGenerator.mapToColumn(maps, map -> {
            final String data =
                    PostbackUtil.encode(MAP_LOCATION_PAIR_TYPE,
                                        new MapLocationPair()
                                                .setMapId(map.get_id())
                                                .setLocationId(location.get_id()));

            final PostbackAction postbackAction =
                    new PostbackAction("この地図に追加",
                                       data,
                                       map.getName() + " に追加");
            return singletonList(postbackAction);
        });

        final String thumbnailImageUrl = botConfiguration.getHost().resolve("/image/new.png").toString();

        final String postbackData =
                PostbackUtil.encode(MAP_LOCATION_PAIR_TYPE,
                                    new MapLocationPair().setMapId("NEW").setLocationId(location.get_id()));

        columns.add(new CarouselColumn(thumbnailImageUrl,
                                       "新しい地図を作成",
                                       "新しい地図を作成して、その中に追加します",
                                       singletonList(new PostbackAction("新しい地図を作って追加",
                                                                        postbackData,
                                                                        "新しい地図を作って追加"))));
        sessionStorage.set(new Session(source).setContext(SELECT_MAP_FOR_NEW_LOCATION));

        return new TemplateMessage("地図を選択して下さい",
                                   new CarouselTemplate(columns));
    }

    @EventMapping(context = SELECT_MAP_FOR_NEW_LOCATION)
    public List<Message> fixMapForNewLocation(final PostbackEvent event) {
        final String data = event.getPostbackContent().getData();

        final MapLocationPair mapLocationPair =
                PostbackUtil.decode(MAP_LOCATION_PAIR_TYPE,
                                    data);

        final Location location = locationRepository.get(mapLocationPair.getLocationId());
        final String mapId = "NEW".equalsIgnoreCase(mapLocationPair.getMapId())
                             ? mapRepository.create(new Map().setName("新しい地図")
                                                             .setOwner(event.getSource().getSenderId()))
                                            .get_id()
                             : mapLocationPair.getMapId();

        locationRepository.setMap(location, mapId);

        return asList(
                new TextMessage("追加しました"),
                imageMapGenerator.create(mapRepository.find(mapId)));
    }

    /**
     * Actual adding of location.
     */
    public void addLocation(
            final Map map,
            final Location location) {

    }

    @Data
    @Accessors(chain = true)
    public static class MapLocationPair {
        String mapId;
        String locationId;
    }
}
