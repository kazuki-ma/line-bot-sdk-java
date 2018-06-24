package com.linecorp.bot.client;

import com.linecorp.bot.liff.LiffView;
import com.linecorp.bot.liff.request.AddLiffAppRequest;
import com.linecorp.bot.liff.response.AddLiffAppResponse;
import com.linecorp.bot.liff.response.LiffAppsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/* package private */
interface ChannelManagementClientRetrofitIface {
    @POST("liff/v1/apps")
    Call<AddLiffAppResponse> addLiffApp(@Body AddLiffAppRequest addLiffAppRequest);

    @PUT("liff/v1/apps/{liffId}/view")
    Call<Void> updateLiffApp(@Path("liffId") String liffId, @Body LiffView liffView);

    @GET("liff/v1/apps")
    Call<LiffAppsResponse> getAllLiffApps();

    @DELETE("liff/v1/apps/{liffId}")
    Call<Void> deleteLiffApp(@Path("liffId") String liffId);
}
