package com.linecorp.bot.spring.boot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.linecorp.bot.model.message.Message;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class MessageAndContext {
    List<Message> messages;
    String context;
    boolean clearContext;
    Map<String, String> contextModel = new TreeMap<>();

    public MessageAndContext message(final Message message) {
        messages = new ArrayList<>();
        messages.add(message);
        return this;
    }
}
