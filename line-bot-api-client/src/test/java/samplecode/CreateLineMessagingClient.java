package samplecode;

import com.linecorp.bot.client.LineMessagingClient;

/**
 * If this class have been out-of-date. Please update docs also.
 */
public class CreateLineMessagingClient {
    public static void main(final String... args) {
        LineMessagingClient lineMessagingClient =
                LineMessagingClient.builder()
                                   .channelToken("LineMessagingService")
                                   .build();

        // lineMessagingClient.pushMessage();
    }
}
