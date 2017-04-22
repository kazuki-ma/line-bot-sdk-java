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

package com.linecorp.bot.servlet;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import com.linecorp.bot.callback.LineBotCallbackParser;
import com.linecorp.bot.model.event.CallbackRequest;

@RunWith(MockitoJUnitRunner.class)
public class LineBotCallbackRequestParserTest {
    static final CallbackRequest MARKER_INSTANCE = new CallbackRequest(emptyList());

    private MockHttpServletRequest request;

    @Mock
    private LineBotCallbackParser lineBotCallbackParser;

    @InjectMocks
    private LineBotCallbackRequestParser target;

    @Before
    public void before() throws IOException {
        request = new MockHttpServletRequest();
    }

    @Test
    public void handleRequestTest() throws Exception {
        request.addHeader("X-Line-Signature", "SIGNATURE");
        request.setContent("CONTENT".getBytes(StandardCharsets.UTF_8));

        when(lineBotCallbackParser.handle(any(), any())).thenReturn(MARKER_INSTANCE);

        // Do
        CallbackRequest result = target.handle(request);

        // Verify
        verify(lineBotCallbackParser, only()).handle("SIGNATURE", "CONTENT");
        assertThat(result).isSameAs(MARKER_INSTANCE);
    }

    @Test
    public void handleStringTest() throws Exception {
        when(lineBotCallbackParser.handle(any(), any())).thenReturn(MARKER_INSTANCE);

        // Do
        CallbackRequest result = target.handle("SIGNATURE", "CONTENT");

        // Verify
        verify(lineBotCallbackParser, only()).handle("SIGNATURE", "CONTENT");
        assertThat(result).isSameAs(MARKER_INSTANCE);
    }
}
