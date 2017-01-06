package com.example.bot.spring.echo.image;

import static com.example.bot.spring.echo.image.HtmlMessageConposer.writeImageToResponse;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.bot.spring.echo.BotConfiguration;
import com.example.bot.spring.echo.LocationRepository;
import com.example.bot.spring.echo.LocationRepository.Location;
import com.example.bot.spring.echo.MapRepository;
import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.ViewModel;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MapImageGenerator {
    private final BotConfiguration botConfiguration;
    private final MapRepository mapRepository;
    private final LocationRepository locationRepository;
    private final SnapShooter snapShooter;

    @GetMapping(path = "/internal/map")
    public ModelAndView map(
            @RequestParam("id") final String mapId) {
        final Map map = mapRepository.find(mapId);
        final List<Location> locations = locationRepository.read(mapId);

        return new ModelAndView("test")
                .addObject(new ViewModel()
                                   .setMap(map)
                                   .setLocationList(locations));
    }

    @GetMapping(path = "/internal/mapImage", produces = MediaType.IMAGE_PNG_VALUE)
    public void mapImage(
            @RequestParam("id") final String mapId,
            final HttpServletResponse response) {
        final URI uri = botConfiguration.getHost().resolve("/internal/map?id=" + mapId);
        final BufferedImage snap = snapShooter.snap(uri);
        writeImageToResponse(snap, response);
    }
}
