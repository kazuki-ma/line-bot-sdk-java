/*
 * Copyright 2017 LINE Corporation
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

package com.linecorp.bot.callback;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.util.StreamUtils;

import com.linecorp.bot.model.event.CallbackRequest;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

public class LineBotCallbackParserTest {
    private static final String VALID_SIGNATURE = "VALID_SIGNATURE";
    private static final String INVALID_SIGNATURE = "INVALID_SIGNATURE";

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    LineSignatureValidator lineSignatureValidator;

    @InjectMocks
    LineBotCallbackParser target;

    @Before
    public void setUp() {
        when(lineSignatureValidator.validateSignature(any(byte[].class), eq(VALID_SIGNATURE)))
                .thenReturn(true);

        when(lineSignatureValidator.validateSignature(any(byte[].class), eq(INVALID_SIGNATURE)))
                .thenReturn(false);
    }

    @Test
    public void testMissingHeaderForNullSignature() throws Exception {
        assertThatThrownBy(() -> target.handle(null, ""))
                .isInstanceOf(LineBotCallbackException.class)
                .hasMessage("Missing 'X-Line-Signature' header");
    }

    @Test
    public void testMissingHeaderForEmptySignature() throws Exception {
        assertThatThrownBy(() -> target.handle("", ""))
                .isInstanceOf(LineBotCallbackException.class)
                .hasMessage("Missing 'X-Line-Signature' header");
    }

    @Test
    public void testInvalidSignature() throws Exception {
        String content = "{}";

        assertThatThrownBy(() -> target.handle(INVALID_SIGNATURE, content))
                .isInstanceOf(LineBotCallbackException.class)
                .hasMessage("Invalid API signature");
    }

    @Test
    public void testNullRequest() throws Exception {
        final String content = "null";

        assertThatThrownBy(() -> target.handle(VALID_SIGNATURE, content))
                .isInstanceOf(LineBotCallbackException.class)
                .hasMessage("Invalid content");
    }

    @Test
    public void testEmptyRequest() throws Exception {
        final String content = "{}";

        assertThatThrownBy(() -> target.handle(VALID_SIGNATURE, content))
                .isInstanceOf(LineBotCallbackException.class)
                .hasMessage("Invalid content");
    }

    @Test
    public void testCallRequest() throws Exception {
        String content = getResourceAsString("callback-request.json");

        CallbackRequest callbackRequest = target.handle(VALID_SIGNATURE, content);

        assertThat(callbackRequest).isNotNull();

        final List<Event> result = callbackRequest.getEvents();

        @SuppressWarnings("rawtypes")
        final MessageEvent messageEvent = (MessageEvent) result.get(0);
        final TextMessageContent text = (TextMessageContent) messageEvent.getMessage();
        assertThat(text.getText()).isEqualTo("Hello, world");

        final String followedUserId = messageEvent.getSource().getUserId();
        assertThat(followedUserId).isEqualTo("u206d25c2ea6bd87c17655609a1c37cb8");
        assertThat(messageEvent.getTimestamp()).isEqualTo(Instant.parse("2016-05-07T13:57:59.859Z"));
    }

    private String getResourceAsString(final String resourceName) throws IOException {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(resourceName);
        return StreamUtils.copyToString(resource, UTF_8);
    }
}
