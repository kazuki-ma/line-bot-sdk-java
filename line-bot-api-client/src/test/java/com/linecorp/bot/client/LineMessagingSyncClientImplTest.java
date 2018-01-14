package com.linecorp.bot.client;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;


public class LineMessagingSyncClientImplTest {
    @Test
    public void methodSignatureTest() {
        List<Method> sync = Stream.of(LineMessagingSyncClient.class.getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .sorted(comparing(Method::getName))
                .collect(toList());

        List<Method> aSync = Stream.of(LineMessagingClient.class.getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .sorted(comparing(Method::getName))
                .collect(toList());

        assertThat(sync).hasSameSizeAs(aSync);

        for (int i = 0; i < sync.size(); ++i) {
            Method syncMethod = sync.get(i);
            Method aSyncMethod = aSync.get(i);

            ParameterizedType aSyncReturnType = (ParameterizedType) aSyncMethod.getGenericReturnType();

            assertThat(syncMethod.getGenericReturnType())
                    .isEqualTo(aSyncReturnType.getActualTypeArguments()[0]);
            assertThat(syncMethod.getTypeParameters())
                    .isEqualTo(aSyncMethod.getTypeParameters());
        }
    }
}
