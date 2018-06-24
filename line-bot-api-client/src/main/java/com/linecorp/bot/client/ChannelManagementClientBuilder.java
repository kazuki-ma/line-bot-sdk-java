package com.linecorp.bot.client;

import static com.linecorp.bot.client.LineMessagingServiceBuilder.createDefaultRetrofitBuilder;
import static com.linecorp.bot.client.LineMessagingServiceBuilder.defaultInterceptors;

import java.net.URI;

import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Retrofit;

@Setter
@Accessors(fluent = true)
public class ChannelManagementClientBuilder {
    private URI apiEndPoint = URI.create("https://api.line.me/");
    private ChannelTokenSupplier channelTokenSupplier;

    /**
     * Create a new {@link ChannelManagementClientBuilder} with specified given fixed channelToken.
     */
    public static ChannelManagementClientBuilder create(@NonNull String fixedChannelToken) {
        return create(FixedChannelTokenSupplier.of(fixedChannelToken));
    }

    /**
     * Create a new {@link ChannelManagementClientBuilder} with specified {@link ChannelTokenSupplier}.
     */
    public static ChannelManagementClientBuilder create(@NonNull ChannelTokenSupplier channelTokenSupplier) {
        return new ChannelManagementClientBuilder()
                .channelTokenSupplier(channelTokenSupplier);
    }

    public ChannelManagementSyncClient build() {
        final Builder okHttpClientBuilder = new Builder();

        defaultInterceptors(channelTokenSupplier).forEach(okHttpClientBuilder::addInterceptor);

        final OkHttpClient okHttpClient = okHttpClientBuilder.build();

        final Retrofit.Builder retrofitBuilder = createDefaultRetrofitBuilder();

        retrofitBuilder.client(okHttpClient);
        retrofitBuilder.baseUrl(apiEndPoint.toString());
        final Retrofit retrofit = retrofitBuilder.build();

        final ChannelManagementClientRetrofitIface retrofitIface =
                retrofit.create(ChannelManagementClientRetrofitIface.class);
        return ChannelManagementSyncClientImpl.of(retrofitIface);
    }
}
