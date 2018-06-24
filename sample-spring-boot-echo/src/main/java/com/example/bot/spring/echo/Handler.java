package com.example.bot.spring.echo;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@LineMessageHandler
public class Handler {

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {

        System.out.println("event: " + event);
        final String originalMessageText = event.getMessage().getText();

        switch (originalMessageText.toUpperCase()) {
            case "FLEX":
                return new ExampleFlexMessageSupplier().get();
            default:
                return new TextMessage(originalMessageText);
        }
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
