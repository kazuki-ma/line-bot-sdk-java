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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.linecorp.bot.model.event.CallbackRequest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LineBotCallbackParser {
    private static final ObjectReader READER = buildObjectMapper().readerFor(CallbackRequest.class);

    private final LineSignatureValidator lineSignatureValidator;

    /**
     * Create new instance
     *
     * @param lineSignatureValidator LINE messaging API's signature validator
     */
    public LineBotCallbackParser(
            @NonNull final LineSignatureValidator lineSignatureValidator) {
        this.lineSignatureValidator = lineSignatureValidator;
    }

    /**
     * Verify signature and parse request.
     *
     * @param signature X-Line-Signature header.
     * @param payload Request body.
     * @return Parsed result. You can assume returned value always non-null.
     * @throws LineBotCallbackException There's an error around signature.
     */
    public CallbackRequest handle(final String signature, final String payload)
            throws LineBotCallbackException, IOException {
        // validate signature
        if (signature == null || signature.length() == 0) {
            throw new LineBotCallbackException("Missing 'X-Line-Signature' header");
        }

        log.debug("got: {}", payload);

        final byte[] json = payload.getBytes(StandardCharsets.UTF_8);

        if (!lineSignatureValidator.validateSignature(json, signature)) {
            throw new LineBotCallbackException("Invalid API signature");
        }

        final CallbackRequest callbackRequest = READER.readValue(json);
        if (callbackRequest == null || callbackRequest.getEvents() == null) {
            throw new LineBotCallbackException("Invalid content");
        }
        return callbackRequest;
    }

    private static ObjectMapper buildObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // Register JSR-310(java.time.temporal.*) module and read number as millsec.
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    }
}
