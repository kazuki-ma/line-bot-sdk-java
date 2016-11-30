package com.example.bot.spring.echo;

import java.util.concurrent.CompletableFuture;

import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface TabelogApi {
    @GET("{path}")
    @Headers({
            "user-agent: Crawler",
            "Referer:https://www.google.co.jp/",
            "Accept-Language:ja"
    })
    CompletableFuture<ResponseBody> get(
            @Path(value = "path", encoded = true) String path);

    @SneakyThrows
    default String getSync(String path) {
        return get(path).get().string();
    }
}
