package com.example.bot.spring.echo;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

public class PostbackUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    interface PostbackIface<T> {
        String getName();

        Class<T> getClassType();
    }

    @Value
    @AllArgsConstructor
    public static class PostbackType<T> implements PostbackIface<T> {
        String name;
        Class<T> classType;

        PostbackType(Class<T> clazz) {
            this.name = clazz.getSimpleName();
            this.classType = clazz;
        }
    }

    @SneakyThrows
    public static <T> String encode(PostbackIface<T> type, T data) {
        return type.getName() + ':' + OBJECT_MAPPER.writeValueAsString(data);
    }

    @SneakyThrows
    public static <T> T decode(PostbackIface<T> type, String postback) {
        final String[] split = StringUtils.split(postback, ":", 2);

        if (!split[0].equals(type.getName())) {
            throw new IllegalArgumentException(postback);
        }

        return OBJECT_MAPPER.readValue(split[1], type.getClassType());
    }
}
