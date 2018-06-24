package com.linecorp.bot.cli.arguments;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.bot.model.objectmapper.ModelObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

@Lazy
@Data
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class JsonBodyProvider {
    private static final ObjectMapper OBJECT_MAPPER = ModelObjectMapper.createNewObjectMapper();
    private final JsonBodyArguments arguments;

    @SneakyThrows
    public <T> T read(Class<T> clazz) {
        return OBJECT_MAPPER.readValue(readString(), clazz);
    }

    @SneakyThrows
    public String readString() {
        if (arguments.getData() != null) {
            return arguments.getData();
        }

        if (arguments.getJson() != null) {
            return StreamUtils.copyToString(new FileInputStream(arguments.getJson()),
                                            StandardCharsets.UTF_8);
        }

        throw new RuntimeException("--data or --json is mandatory");
    }
}
