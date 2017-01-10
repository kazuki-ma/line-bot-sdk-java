package com.example.bot.spring.echo;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component("config")
@Data
@ConfigurationProperties(prefix = "")
public class BotConfiguration {
    String lineId;
    URI host;
    URI hostInternal;
}
