package com.linecorp.bot.client;

import static com.linecorp.bot.internal.FutureConverter.toFuture;

import java.util.concurrent.ExecutionException;

import com.linecorp.bot.client.exception.LineMessagingException;
import com.linecorp.bot.liff.LiffView;
import com.linecorp.bot.liff.request.AddLiffAppRequest;
import com.linecorp.bot.liff.response.AddLiffAppResponse;
import com.linecorp.bot.liff.response.LiffAppsResponse;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import retrofit2.Call;

@AllArgsConstructor(staticName = "of")
public class ChannelManagementSyncClientImpl implements ChannelManagementSyncClient {
    ChannelManagementClientRetrofitIface retrofitImpl;

    @Override
    public AddLiffAppResponse addLiffApp(final AddLiffAppRequest addLiffAppRequest) {
        return syncGet(retrofitImpl.addLiffApp(addLiffAppRequest));
    }

    @Override
    public void updateLiffApp(String liffId, LiffView liffView) {
        syncGet(retrofitImpl.updateLiffApp(liffId, liffView));
    }

    @Override
    public LiffAppsResponse getAllLiffApps() {
        return syncGet(retrofitImpl.getAllLiffApps());
    }

    @Override
    public void deleteLiffApp(final String liffId) {
        syncGet(retrofitImpl.deleteLiffApp(liffId));
    }

    @SneakyThrows({ LineMessagingException.class, ExecutionException.class, InterruptedException.class })
    private <T> T syncGet(Call<T> wrap) {
        try {
            return toFuture(wrap).get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof LineMessagingException) {
                throw (LineMessagingException) e.getCause();
            }
            throw e;
        }
    }
}
