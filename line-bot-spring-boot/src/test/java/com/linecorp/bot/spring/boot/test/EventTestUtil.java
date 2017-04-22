package com.linecorp.bot.spring.boot.test;

import java.time.Instant;

import org.mockito.Mockito;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.spring.boot.EventContext;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EventTestUtil {
    public static EventContext createTextMessageEventContext(final String text) {
        final LineMessagingClient lineMessagingClient = Mockito.mock(LineMessagingClient.class);
        return createTextMessageEventContext(text, lineMessagingClient);
    }

    private static EventContext createTextMessageEventContext(
            final String text,
            final LineMessagingClient lineMessagingClient) {
        final MessageEvent<TextMessageContent> event =
                new MessageEvent<>("replyToken",
                                   new UserSource("userId"),
                                   new TextMessageContent("id", text),
                                   Instant.parse("2016-11-19T00:00:00.000Z"));
        return new EventContext(event, lineMessagingClient);
    }

    public static MessageEvent<TextMessageContent> createTextMessage(final String text) {
        return new MessageEvent<>("replyToken", new UserSource("userId"),
                                  new TextMessageContent("id", text),
                                  Instant.parse("2016-11-19T00:00:00.000Z"));
    }
}
