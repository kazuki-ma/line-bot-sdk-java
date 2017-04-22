package com.linecorp.bot.spring.boot.support;

import static com.linecorp.bot.spring.boot.test.EventTestUtil.createTextMessage;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import java.lang.reflect.InvocationTargetException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.EventContext;

public class LineMessageWebhookEndpointTest {
    static final MessageEvent<TextMessageContent> EVENT = createTextMessage("Message");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Mock
    private LineMessageEventDispatcher dispatcher;

    @Mock
    private LineMessagingClient lineMessagingClient;

    @InjectMocks
    private LineMessageWebhookEndpoint target;

    @Test
    public void callbackTest() throws Exception {
        // Do
        target.callback(singletonList(EVENT));

        // Verify
        Mockito.verify(dispatcher, Mockito.only())
               .dispatch(new EventContext(EVENT, lineMessagingClient));
    }

    @Test
    public void invocationExceptionInDispatchTest() throws Exception {
        doThrow(new InvocationTargetException(new RuntimeException("Message should be contained in log.")))
                .when(dispatcher).dispatch(any());

        // Do
        target.dispatch(EVENT);

        // Verify
        assertThat(systemOutRule.getLogWithNormalizedLineSeparator())
                .contains("Message should be contained in log.");
    }

    @Test
    public void runtimeExceptionInDispatchTest() throws Exception {
        doThrow(new RuntimeException("Message should be contained in log."))
                .when(dispatcher).dispatch(any());

        // Do
        target.dispatch(EVENT);

        // Verify
        assertThat(systemOutRule.getLogWithNormalizedLineSeparator())
                .contains("Message should be contained in log.");
    }
}
