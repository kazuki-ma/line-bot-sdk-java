package com.linecorp.bot.cli;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class Application {
    public static void main(final String... args) throws Exception {
        try (ConfigurableApplicationContext context =
                     new SpringApplicationBuilder(Application.class)
                             .logStartupInfo(false)
                             .run(args)) {
            try {
                context.getBean(Application.class).run(context);
            } catch (Exception e) {
                log.error("Exception in command execution", e);
            }
        }
    }

    void run(final ConfigurableApplicationContext context) throws Exception {
        final Map<String, CliCommand> commandMap = context.getBeansOfType(CliCommand.class);
        checkState(commandMap.size() < 2, "Multiple command matching. Maybe bug.");

        if (commandMap.isEmpty()) {
            printAvailableCommand(context);
            return;
        }

        final CliCommand command = commandMap.values().iterator().next();
        command.execute();
    }

    @SneakyThrows
    void printAvailableCommand(final ConfigurableApplicationContext context) {
        // TODO
    }
}
