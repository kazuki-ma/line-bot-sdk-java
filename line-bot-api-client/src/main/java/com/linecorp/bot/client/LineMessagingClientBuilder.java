package com.linecorp.bot.client;

import java.net.URI;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Setter
@ToString
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LineMessagingClientBuilder {
    public static final String DEFAULT_API_END_POINT = "https://api.line.me/";
    public static final long DEFAULT_CONNECT_TIMEOUT = 10_000;
    public static final long DEFAULT_READ_TIMEOUT = 10_000;
    public static final long DEFAULT_WRITE_TIMEOUT = 10_000;

    /**
     * channelToken
     */
    String channelToken;

    /**
     * apiEndPoint.
     *
     * <p>
     * Default: {@value #DEFAULT_API_END_POINT}
     */
    URI apiEndPoint = URI.create(DEFAULT_API_END_POINT);

    /**
     * Set connectTimeout in milliseconds.
     *
     * <p>
     * Default: {@value #DEFAULT_CONNECT_TIMEOUT}
     */
    long connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /**
     * Set readTimeout in milliseconds.
     *
     * <p>
     * Default: {@value #DEFAULT_READ_TIMEOUT}
     */
    long readTimeout = DEFAULT_READ_TIMEOUT;

    /**
     * Set writeTimeout in milliseconds.
     *
     * <p>
     * Default: {@value #DEFAULT_WRITE_TIMEOUT}
     */
    long writeTimeout = DEFAULT_WRITE_TIMEOUT;

    /**
     * Creates a new {@link LineMessagingClient}.
     */
    public LineMessagingClient build() {
        Objects.requireNonNull(channelToken, "channelToken should be non null");

        @SuppressWarnings("deprecation")
        final LineMessagingService lineMessagingService =
                LineMessagingServiceBuilder.create(channelToken)
                                           .apiEndPoint(apiEndPoint.toString())
                                           .connectTimeout(connectTimeout)
                                           .readTimeout(readTimeout)
                                           .writeTimeout(writeTimeout)
                                           .build();

        return new LineMessagingClientImpl(lineMessagingService);
    }
}
