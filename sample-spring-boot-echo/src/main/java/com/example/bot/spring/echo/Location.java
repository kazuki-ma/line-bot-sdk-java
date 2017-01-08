package com.example.bot.spring.echo;

import java.net.URI;

import lombok.Value;

@Value
public class Location {
    String title;
    String description;
    URI image;
    double latitude;
    double longitude;
}
