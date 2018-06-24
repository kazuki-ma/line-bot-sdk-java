package com.linecorp.bot.cli;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linecorp.bot.cli.arguments.Arguments;
import com.linecorp.bot.cli.arguments.JsonBodyProvider;
import com.linecorp.bot.client.ChannelManagementSyncClient;
import com.linecorp.bot.liff.LiffView;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// @ConditionalOnProperty("command=liff-delete")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LiffDeleteCommand implements CliCommand {
    private ChannelManagementSyncClient channelManagementClient;
    private JsonBodyProvider bodyProvider;
    private Arguments arguments;

    @Override
    public void execute() {
        checkNotNull(arguments.getLiffId(), "--liff-id=");
        final LiffView liffView = bodyProvider.read(LiffView.class);

        try {
            channelManagementClient.updateLiffApp(arguments.getLiffId(), liffView);
        } catch (Exception e) {
            log.error("Failed : {}", e.getMessage());
            return;
        }
        log.info("Successfully finished.");
    }
}
