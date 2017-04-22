package com.linecorp.bot.spring.boot.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.annotations.VisibleForTesting;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.spring.boot.EventContext;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${line.bot.handler.path:/callback}")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LineMessageWebhookEndpoint {
    private final LineMessagingClient lineMessagingClient;
    private final LineMessageEventDispatcher lineMessageEventDispatcher;

    @PostMapping
    public void callback(@LineBotMessages final List<Event> events) {
        events.forEach(this::dispatch);
    }

    @VisibleForTesting
    void dispatch(final Event event) {
        lineMessageEventDispatcher.dispatch(new EventContext(event, lineMessagingClient));
    }
}
