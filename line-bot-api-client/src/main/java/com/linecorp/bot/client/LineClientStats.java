package com.linecorp.bot.client;

import java.net.URI;
import java.time.Duration;

public enum LineClientStats {
    ;

    public static final URI DEFAULT_API_END_POINT = URI.create("https://api.line.me/");
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofMillis(10_000);
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofMillis(10_000);
    public static final Duration DEFAULT_WRITE_TIMEOUT = Duration.ofMillis(10_000);
}
