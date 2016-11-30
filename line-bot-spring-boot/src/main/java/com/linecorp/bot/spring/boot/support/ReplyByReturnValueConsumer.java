package com.linecorp.bot.spring.boot.support;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Internal class to send message as reply.
 *
 * Basically, message contents are from return value of handler method.
 *
 * @see LineMessageHandlerSupport#handleReturnValue(Event, Object)
 */
@Slf4j
@Builder
class ReplyByReturnValueConsumer implements Consumer<Object> {
    private final LineMessagingClient lineMessagingClient;
    private final Event originalEvent;

    @Component
    public static class Factory {
        private final LineMessagingClient lineMessagingClient;

        @Autowired
        public Factory(final LineMessagingClient lineMessagingClient) {
            this.lineMessagingClient = lineMessagingClient;
        }

        ReplyByReturnValueConsumer createForEvent(final Event event) {
            return builder()
                    .lineMessagingClient(lineMessagingClient)
                    .originalEvent(event)
                    .build();
        }
    }

    @Override
    public void accept(final Object returnValue) {
        if (returnValue instanceof CompletableFuture) {
            // accept when future complete.
            ((CompletableFuture<?>) returnValue)
                    .whenComplete(this::whenComplete);
        } else {
            // accept immediately.
            acceptResult(returnValue);
        }
    }

    private void whenComplete(final Object futureResult, final Throwable throwable) {
        if (throwable != null) {
            log.error("Method return value waited but exception occurred in CompletedFuture", throwable);
            return;
        }

        acceptResult(futureResult);
    }

    private void acceptResult(final Object returnValue) {
        final List<?> returnValueAsList;

        if (returnValue instanceof String || returnValue instanceof Message) {
            returnValueAsList = singletonList(returnValue);
        } else if (returnValue instanceof List) {
            returnValueAsList = (List<?>) returnValue;
        } else {
            throw new IllegalArgumentException("Can't handle method return value: " + returnValue);
        }

        if (returnValueAsList.isEmpty()) {
            return;
        }

        reply(toMessageList(returnValueAsList));
    }

    private void reply(final List<Message> messages) {
        final ReplyEvent replyEvent = (ReplyEvent) originalEvent;
        lineMessagingClient.replyMessage(new ReplyMessage(replyEvent.getReplyToken(), messages))
                           .whenComplete(this::logging);
        // DO NOT BLOCK HERE, otherwise, next message processing will be BLOCKED.
    }

    private void logging(final BotApiResponse botApiResponse, final Throwable throwable) {
        if (throwable == null) {
            log.debug("Reply message success. response = {}", botApiResponse);
        } else {
            log.warn("Reply message failed: {}", throwable.getMessage(), throwable);
        }
    }

    private static List<Message> toMessageList(final List<?> list) {
        return list.stream().map(ReplyByReturnValueConsumer::toMessage).collect(toList());
    }

    @VisibleForTesting
    static Message toMessage(Object item) {
        Preconditions.checkNotNull(item, "item is null.");

        if (item instanceof Message) {
            return (Message) item;
        }

        if (item instanceof String) {
            return new TextMessage((String) item);
        }

        throw new IllegalArgumentException("List contains not Message type object. type = " + item.getClass());
    }
}