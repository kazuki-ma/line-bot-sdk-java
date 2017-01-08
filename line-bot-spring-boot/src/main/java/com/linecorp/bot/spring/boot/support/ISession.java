package com.linecorp.bot.spring.boot.support;

public interface ISession {
    String getContext();

    java.util.Map<String, String> getData();

    ISession setContext(String context);

    ISession setData(java.util.Map<String, String> data);
}
