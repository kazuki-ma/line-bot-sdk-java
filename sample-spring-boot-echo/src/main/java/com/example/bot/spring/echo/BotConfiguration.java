package com.example.bot.spring.echo;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "")
public class BotConfiguration {
    URI host;
    URI hostInternal;
}
