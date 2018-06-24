package com.linecorp.bot.client;

import com.linecorp.bot.liff.LiffView;
import com.linecorp.bot.liff.request.AddLiffAppRequest;
import com.linecorp.bot.liff.response.AddLiffAppResponse;
import com.linecorp.bot.liff.response.LiffAppsResponse;

import retrofit2.http.Body;
import retrofit2.http.Path;

public interface ChannelManagementSyncClient {
    static ChannelManagementClientBuilder builder(final ChannelTokenSupplier channelTokenSupplier) {
        return ChannelManagementClientBuilder.create(channelTokenSupplier);
    }

    AddLiffAppResponse addLiffApp(@Body AddLiffAppRequest liffView);

    void updateLiffApp(@Path("liffId") String liffId, @Body LiffView liffView);

    LiffAppsResponse getAllLiffApps();

    void deleteLiffApp(@Path("liffId") String liffId);
}
