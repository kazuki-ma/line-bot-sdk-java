package com.example.bot.spring.echo;

import static java.util.Collections.emptyList;

import java.util.List;

import com.example.bot.spring.echo.LocationRepository.Location;
import com.example.bot.spring.echo.MapRepository.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ViewModel {
    Map map;
    List<Location> locationList = emptyList();
}
