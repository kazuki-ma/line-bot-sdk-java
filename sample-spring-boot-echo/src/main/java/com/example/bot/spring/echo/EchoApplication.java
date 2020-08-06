/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import com.linecorp.bot.client.ChannelTokenSupplier;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.AllArgsConstructor;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.example.bot.spring.echo.ChannelTokenHolder.CHANNEL_TOKEN_HOLDER;
import static java.util.Collections.singletonList;

class ChannelTokenHolder {
    public static final ThreadLocal<String> CHANNEL_TOKEN_HOLDER = new ThreadLocal<>();
}

@Configuration(proxyBeanMethods = false)
class InThreadDispatchConfiguration {
    @Bean
    @Primary
    public LineMessagingClient lineMessagingClient(
            ChannelTokenSupplier channelTokenSupplier
    ) {
        // Create special executors for passing token from thread local and pass it to dispatching thread.
        final ExecutorService tokenPassingExecutorService = new AbstractExecutorService() {
            private final ExecutorService delegate = Executors.newCachedThreadPool();

            @Override
            public void execute(Runnable command) {
                final String tokenInCurrentThread = CHANNEL_TOKEN_HOLDER.get();

                delegate.submit(() -> {
                    CHANNEL_TOKEN_HOLDER.set(tokenInCurrentThread);
                    try {
                        command.run();
                    } finally {
                        CHANNEL_TOKEN_HOLDER.remove();
                    }
                });
            }

            @Override
            public void shutdown() {
                delegate.shutdown();
            }

            @Override
            public List<Runnable> shutdownNow() {
                return delegate.shutdownNow();
            }

            @Override
            public boolean isShutdown() {
                return delegate.isShutdown();
            }

            @Override
            public boolean isTerminated() {
                return delegate.isTerminated();
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return delegate.awaitTermination(timeout, unit);
            }
        };

        // Create dispatchers using with the executor service and infinite concurrent requests.
        // Note: Actual throttling still possible by executor service.
        Dispatcher inthreadDispatcher = new Dispatcher(tokenPassingExecutorService);
        inthreadDispatcher.setMaxRequests(Integer.MAX_VALUE);
        inthreadDispatcher.setMaxRequestsPerHost(Integer.MAX_VALUE);

        // Create OkHttpClient builder using this dispatcher.
        OkHttpClient.Builder inThreadOkHttpClient = new OkHttpClient.Builder()
                .dispatcher(inthreadDispatcher);

        // Create LineMessagingClient with custom OkHttpClient.Builder.
        return LineMessagingClient
                .builder(channelTokenSupplier)
                .okHttpClientBuilder(inThreadOkHttpClient, true)
                .build();
    }

    /**
     * @return ChannelTokenSupplier based on thread local element.
     */
    @Bean
    public ChannelTokenSupplier channelTokenSupplier() {
        return () -> {
            return CHANNEL_TOKEN_HOLDER.get();
        };
    }
}

@SpringBootApplication
@LineMessageHandler
@AllArgsConstructor
public class EchoApplication {
    private final Logger log = LoggerFactory.getLogger(EchoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    private final LineMessagingClient lineMessagingClient;


    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        log.info("event: " + event);

        CHANNEL_TOKEN_HOLDER.set("TEST");

        final String originalMessageText = event.getMessage().getText();
        lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), singletonList(new TextMessage(originalMessageText))));
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
