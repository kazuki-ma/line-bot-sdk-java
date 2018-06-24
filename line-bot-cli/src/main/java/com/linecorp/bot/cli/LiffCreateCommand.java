package com.linecorp.bot.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.linecorp.bot.cli.arguments.JsonBodyProvider;
import com.linecorp.bot.client.ChannelManagementSyncClient;
import com.linecorp.bot.liff.LiffView;
import com.linecorp.bot.liff.request.AddLiffAppRequest;
import com.linecorp.bot.liff.response.AddLiffAppResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty("command=liff-create")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LiffCreateCommand implements CliCommand {
    private ChannelManagementSyncClient channelManagementClient;
    private JsonBodyProvider jsonBodyProvider;

    @Override
    public void execute() {
        final LiffView liffView = jsonBodyProvider.read(LiffView.class);
        log.info("Request View : {}", liffView);

        final AddLiffAppRequest addLiffAppRequest = AddLiffAppRequest.builder().view(liffView).build();

        final AddLiffAppResponse addLiffAppResponse;
        try {
            addLiffAppResponse = channelManagementClient.addLiffApp(addLiffAppRequest);
        } catch (Exception e) {
            log.error("Failed : {}", e.getMessage());
            return;
        }
        log.info("Successfully finished. Response : {}", addLiffAppResponse);
    }
}
