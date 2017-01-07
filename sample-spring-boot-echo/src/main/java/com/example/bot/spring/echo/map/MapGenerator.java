package com.example.bot.spring.echo.map;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.bot.spring.echo.LocationRepository;
import com.example.bot.spring.echo.LocationRepository.Location;
import com.example.bot.spring.echo.MapRepository;
import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.ViewModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MapGenerator {
    public static final String MAP_PATH = "/map";
    private final MapRepository mapRepository;
    private final LocationRepository locationRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @GetMapping(MAP_PATH)
    public ModelAndView generateGoogleMap(@RequestParam("id") final String id) {
        final Map map = mapRepository.find(id);
        final List<Location> locationList = locationRepository.read(id);
        return new ModelAndView("google_map")
                .addObject("viewModel", new ViewModel()
                        .setMap(map)
                        .setLocationList(locationList));
    }

    @Value
    @Builder
    public static class Model {
        Map map;
        List<Map> locationList;
    }
}
