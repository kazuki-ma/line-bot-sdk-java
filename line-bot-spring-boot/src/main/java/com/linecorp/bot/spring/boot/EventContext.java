package com.linecorp.bot.spring.boot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.Event;

import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Immutable single event context.
 */
@Value
@Accessors(fluent = true)
public class EventContext {
    Event event;
    LineMessagingClient client;
}
