package com.example.bot.spring.echo;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bot.spring.echo.LocationRepository.Location;
import com.example.bot.spring.echo.MapRepository.Map;

import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.message.template.CarouselColumn;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CarouselGenerator {
    private final LocationRepository locationRepository;

    public List<CarouselColumn> mapToColumn(
            List<Map> maps,
            Function<Map, List<Action>> postbackActions) {
        return maps.stream().map(map -> {
            final List<Location> read = locationRepository.read(map.get_id());
            if (read.isEmpty()) {
                return null;
            }

            final URI image = read.get(0).getImage();

            return new CarouselColumn(
                    image.toString(),
                    map.getName(),
                    read.size() + "個のスポット",
                    postbackActions.apply(map));
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
