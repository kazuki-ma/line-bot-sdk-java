package com.example.bot.spring.echo;

import java.net.URI;
import java.util.function.Function;

import com.linecorp.bot.model.message.LocationMessage;

public class LocationExtractor implements Function<URI, LocationMessage> {
    @Override
    public LocationMessage apply(URI uri) {
        return null;
    }
}
