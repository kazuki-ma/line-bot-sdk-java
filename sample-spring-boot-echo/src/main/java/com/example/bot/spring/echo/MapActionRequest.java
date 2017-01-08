package com.example.bot.spring.echo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MapActionRequest {
    enum MapAction {
        DELETE,
        DELETE_CONFIRMED,
        LIST
    }

    String mapId;
    MapAction action;
}
