package com.linecorp.bot.liff.response;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class AddLiffAppResponse {
    /** LIFF app ID */
    String liffId;
}
