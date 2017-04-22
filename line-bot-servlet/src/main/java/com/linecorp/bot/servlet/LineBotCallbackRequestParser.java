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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.model.event.CallbackRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HttpServletRequest}
 */
@Slf4j
@AllArgsConstructor
public class LineBotCallbackRequestParser {
    private final LineBotCallbackParser delegate;

    /**
     * Parse request.
     *
     * @param req HTTP servlet request.
     * @return Parsed result. If there's an error, this method sends response.
     * @throws LineBotCallbackException There's an error around signature.
     */
    public CallbackRequest handle(final HttpServletRequest req) throws LineBotCallbackException, IOException {
        // validate signature
        String signature = req.getHeader("X-Line-Signature");
        final byte[] json = ByteStreams.toByteArray(req.getInputStream());
        return delegate.handle(signature, new String(json, StandardCharsets.UTF_8));
    }
}
