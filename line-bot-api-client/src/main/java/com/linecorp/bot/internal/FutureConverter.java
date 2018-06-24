package com.linecorp.bot.internal;

import static java.util.Collections.emptyList;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.client.exception.GeneralLineMessagingException;
import com.linecorp.bot.model.response.BotApiResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FutureConverter {
    private static final ExceptionConverter EXCEPTION_CONVERTER = new ExceptionConverter();
    private static final BotApiResponse BOT_API_SUCCESS_RESPONSE = new BotApiResponse("", emptyList());
    private static final Function<Void, BotApiResponse>
            VOID_TO_BOT_API_SUCCESS_RESPONSE = ignored -> BOT_API_SUCCESS_RESPONSE;

    public static <T> CompletableFuture<T> toFuture(Call<T> callToWrap) {
        final CallbackAdaptor<T> completableFuture = new CallbackAdaptor<>();
        callToWrap.enqueue(completableFuture);
        return completableFuture;
    }

    public static CompletableFuture<BotApiResponse> toBotApiFuture(Call<Void> callToWrap) {
        final CallbackAdaptor<Void> completableFuture = new CallbackAdaptor<>();
        callToWrap.enqueue(completableFuture);
        return completableFuture.thenApply(VOID_TO_BOT_API_SUCCESS_RESPONSE);
    }

    public static CompletableFuture<MessageContentResponse> toMessageContentResponseFuture(
            final Call<ResponseBody> callToWrap) {
        final ResponseBodyCallbackAdaptor future = new ResponseBodyCallbackAdaptor();
        callToWrap.enqueue(future);
        return future;
    }

    static class CallbackAdaptor<T> extends CompletableFuture<T> implements Callback<T> {
        @Override
        public void onResponse(final Call<T> call, final Response<T> response) {
            if (response.isSuccessful()) {
                complete(response.body());
            } else {
                completeExceptionally(EXCEPTION_CONVERTER.apply(response).fillInStackTrace());
            }
        }

        @Override
        public void onFailure(final Call<T> call, final Throwable t) {
            completeExceptionally(
                    new GeneralLineMessagingException(t.getMessage(), null, t));
        }
    }

    static class ResponseBodyCallbackAdaptor
            extends CompletableFuture<MessageContentResponse>
            implements Callback<ResponseBody> {

        @Override
        public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
            if (!response.isSuccessful()) {
                completeExceptionally(EXCEPTION_CONVERTER.apply(response));
                return;
            }

            try {
                complete(convert(response));
            } catch (RuntimeException exceptionInConvert) {
                completeExceptionally(
                        new GeneralLineMessagingException(exceptionInConvert.getMessage(),
                                                          null, exceptionInConvert));
            }
        }

        @Override
        public void onFailure(final Call<ResponseBody> call, final Throwable t) {
            completeExceptionally(
                    new GeneralLineMessagingException(t.getMessage(), null, t));
        }

        private MessageContentResponse convert(final Response<ResponseBody> response) {
            return MessageContentResponse
                    .builder()
                    .length(response.body().contentLength())
                    .allHeaders(response.headers().toMultimap())
                    .mimeType(response.body().contentType().toString())
                    .stream(response.body().byteStream())
                    .build();
        }
    }
}
