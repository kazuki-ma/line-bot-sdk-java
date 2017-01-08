package com.example.bot.spring.echo;

import static com.example.bot.spring.echo.MapManagementHandler.TEXT_CHANGE_NAME_PREFIX;
import static com.example.bot.spring.echo.map.MapGenerator.MAP_PATH;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bot.spring.echo.LocationRepository.Location;
import com.example.bot.spring.echo.MapRepository.Map;

import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapAction;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ImageMapGenerator {
    private final BotConfiguration botConfiguration;
    private final LocationRepository locationRepository;

    public ImagemapMessage create(
            final Map map) {
        final String id = map.get_id();
        final List<Location> locations = locationRepository.read(map._id);

        final List<ImagemapAction> actions = new ArrayList<>();

        final String text = TEXT_CHANGE_NAME_PREFIX + "\nid=" + id;
        actions.add(new MessageImagemapAction(text,
                                              new ImagemapArea(1040 - 300, 0, 300, 176)));

        final int size = locations.size();
        for (int i = 0; i < size; ++i) {
            final LocationRepository.Location location = locations.get(i);
            final int y = 176 + 250 * i;
            final int height = 176;

            if (location.getUrl() != null) {
                actions.add(new URIImagemapAction(location.getUrl().toString(),
                                                  new ImagemapArea(0, y, 1040 - 125, height)));
            }
            actions.add(new MessageImagemapAction("Delete:" + location.get_id(),
                                                  new ImagemapArea(1040 - 125, y, 125, height)));
        }

        final String mapUri = botConfiguration.getHost().resolve(MAP_PATH.replace("{id}", id)).toString();
        final ImagemapArea imagemapArea = new ImagemapArea(0, 176 + 250 * size + 186, 1040, 145);
        actions.add(new URIImagemapAction(mapUri, imagemapArea));

        return new ImagemapMessage(
                botConfiguration.getHost().resolve("/internal/mapImage?id=" + id + "&_=" + Instant.now())
                                .toString(),
                map.getName(),
                new ImagemapBaseSize(250 * size + 509, 1040),
                actions);
    }

}
