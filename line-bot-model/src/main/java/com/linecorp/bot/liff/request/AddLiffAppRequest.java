package com.linecorp.bot.liff.request;

import com.linecorp.bot.liff.LiffView;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddLiffAppRequest {
    LiffView view;
}
