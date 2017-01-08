package com.linecorp.bot.spring.boot.support;

import java.util.Map;

import com.linecorp.bot.model.event.source.Source;

public interface SessionStorageIface<T extends ISession> {
    void set(T session);

    T getSession(Source source);

    String getContext(Source source);

    Map<String, String> getMap(Source source);

    T delete(Source source);
}
