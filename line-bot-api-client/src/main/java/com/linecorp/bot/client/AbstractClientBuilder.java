package com.linecorp.bot.client;

import static com.linecorp.bot.client.LineMessagingServiceBuilder.createDefaultRetrofitBuilder;
import static com.linecorp.bot.client.LineMessagingServiceBuilder.defaultInterceptors;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class AbstractClientBuilder<SELF extends AbstractClientBuilder> {
    protected ChannelTokenSupplier channelTokenSupplier;
    /** Whenever add default interceptors or not. */
    protected boolean addDefaultInterceptors = true;
    protected URI apiEndPoint = LineClientStats.DEFAULT_API_END_POINT;
    protected Duration connectTimeout = LineClientStats.DEFAULT_CONNECT_TIMEOUT;
    protected Duration readTimeout = LineClientStats.DEFAULT_READ_TIMEOUT;
    protected Duration writeTimeout = LineClientStats.DEFAULT_WRITE_TIMEOUT;
    protected List<Interceptor> interceptors = new ArrayList<>();
    protected OkHttpClient.Builder okHttpClientBuilder;
    protected Retrofit.Builder retrofitBuilder;

    @SuppressWarnings("unchecked")
    private SELF self() {
        return (SELF) this;
    }

    /**
     * Set #channelTokenSupplier.
     */
    public SELF channelTokenSupplier(final ChannelTokenSupplier channelTokenSupplier) {
        this.channelTokenSupplier = channelTokenSupplier;
        return self();
    }

    /**
     * Set apiEndPoint.
     */
    public SELF apiEndPoint(@NonNull final URI apiEndPoint) {
        this.apiEndPoint = apiEndPoint;
        return self();
    }

    /**
     * Set connectTimeout.
     */
    public SELF connectTimeout(final Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        return self();
    }

    /**
     * Set readTimeout.
     */
    public SELF readTimeout(final Duration readTimeout) {
        this.readTimeout = readTimeout;
        return self();
    }

    /**
     * Add interceptor.
     */
    public SELF addInterceptor(final Interceptor interceptor) {
        this.interceptors.add(interceptor);
        return self();
    }

    /**
     * Set writeTimeout.
     */
    public SELF writeTimeout(final Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
        return self();
    }

    /**
     * Add interceptor first.
     */
    public SELF addInterceptorFirst(final Interceptor interceptor) {
        this.interceptors.add(0, interceptor);
        return self();
    }

    /**
     * Remove all interceptors.
     */
    public SELF removeAllInterceptors() {
        this.interceptors.clear();
        return self();
    }

    /**
     * <p>If you want to use your own setting, specify {@link OkHttpClient.Builder} instance.</p>
     */
    public SELF okHttpClientBuilder(
            @NonNull final OkHttpClient.Builder okHttpClientBuilder) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        return self();
    }

    /**
     *
     */
    public SELF addDefaultInterceptors(final boolean addDefaultInterceptors) {
        this.addDefaultInterceptors = addDefaultInterceptors;
        return self();
    }

    /**
     * <p>If you want to use your own setting, specify {@link Retrofit.Builder} instance.</p>
     *
     * <p>ref: {@link LineMessagingServiceBuilder#createDefaultRetrofitBuilder()} ()}.</p>
     */
    public SELF retrofitBuilder(@NonNull final Retrofit.Builder retrofitBuilder) {
        this.retrofitBuilder = retrofitBuilder;
        return self();
    }

    /**
     * Creates a new instance with configuration.
     */
    protected <T> T buildRetrofitClient(final Class<T> clazz) {
        if (okHttpClientBuilder == null) {
            okHttpClientBuilder = new OkHttpClient.Builder();
        }

        if (addDefaultInterceptors) {
            defaultInterceptors(channelTokenSupplier).forEach(okHttpClientBuilder::addInterceptor);
        }

        interceptors.forEach(okHttpClientBuilder::addInterceptor);
        okHttpClientBuilder
                .connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout.toMillis(), TimeUnit.MILLISECONDS);

        final OkHttpClient okHttpClient = okHttpClientBuilder.build();

        if (retrofitBuilder == null) {
            retrofitBuilder = createDefaultRetrofitBuilder();
        }
        retrofitBuilder.client(okHttpClient);
        retrofitBuilder.baseUrl(apiEndPoint.toString());
        final Retrofit retrofit = retrofitBuilder.build();

        return retrofit.create(clazz);
    }
}
