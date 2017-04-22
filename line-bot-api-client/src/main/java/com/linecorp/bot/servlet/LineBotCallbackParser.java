package com.linecorp.bot.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.CallbackRequest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LineBotCallbackParser {
    private final LineSignatureValidator lineSignatureValidator;
    private final ObjectMapper objectMapper;

    /**
     * Create new instance
     *
     * @param lineSignatureValidator LINE messaging API's signature validator
     */
    public LineBotCallbackParser(
            @NonNull final LineSignatureValidator lineSignatureValidator) {
        this.lineSignatureValidator = lineSignatureValidator;
        this.objectMapper = buildObjectMapper();
    }

    /**
     * Parse request.
     *
     * @param signature X-Line-Signature header.
     * @param payload Request body.
     * @return Parsed result. If there's an error, this method sends response.
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

        final CallbackRequest callbackRequest = objectMapper.readValue(json, CallbackRequest.class);
        if (callbackRequest == null || callbackRequest.getEvents() == null) {
            throw new LineBotCallbackException("Invalid content");
        }
        return callbackRequest;
    }

    private static ObjectMapper buildObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Register JSR-310(java.time.temporal.*) module and read number as millsec.
        objectMapper.registerModule(new JavaTimeModule())
                    .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return objectMapper;
    }
}
