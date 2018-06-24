package com.linecorp.bot.liff;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class LiffApp {
    String liffId;
    LiffView view;
}
