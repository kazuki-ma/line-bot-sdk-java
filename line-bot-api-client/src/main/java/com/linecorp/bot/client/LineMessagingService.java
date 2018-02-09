/*
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.bot.client;

import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.profile.MembersIdsResponse;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.richmenu.RichMenu;
import com.linecorp.bot.model.richmenu.RichMenuIdResponse;
import com.linecorp.bot.model.richmenu.RichMenuListResponse;
import com.linecorp.bot.model.richmenu.RichMenuResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * @deprecated Please use {@link LineMessagingClient} instead.
 */
@Deprecated
interface LineMessagingService {
    /**
     * @see LineMessagingClient#replyMessage(ReplyMessage)
     */
    @POST("v2/bot/message/reply")
    Call<BotApiResponse> replyMessage(@Body ReplyMessage replyMessage);

    /**
     * @see LineMessagingClient#pushMessage(PushMessage)
     */
    @POST("v2/bot/message/push")
    Call<BotApiResponse> pushMessage(@Body PushMessage pushMessage);

    /**
     * @see LineMessagingClient#multicast(Multicast)
     */
    @POST("v2/bot/message/multicast")
    Call<BotApiResponse> multicast(@Body Multicast multicast);

    /**
     * @see LineMessagingClient#getMessageContent(String)
     */
    @Streaming
    @GET("v2/bot/message/{messageId}/content")
    Call<ResponseBody> getMessageContent(@Path("messageId") String messageId);

    /**
     * @see LineMessagingClient#getProfile(String)
     */
    @GET("v2/bot/profile/{userId}")
    Call<UserProfileResponse> getProfile(@Path("userId") String userId);

    /**
     * @see LineMessagingClient#getRoomMemberProfile(String, String)
     * @see LineMessagingClient#getGroupMemberProfile(String, String)
     */
    @GET("v2/bot/{sourceType}/{senderId}/member/{userId}")
    Call<UserProfileResponse> getMemberProfile(
            @Path("sourceType") String sourceType,
            @Path("senderId") String senderId,
            @Path("userId") String userId);

    /**
     * @see LineMessagingClient#getRoomMemberProfile(String, String)
     * @see LineMessagingClient#getGroupMembersIds(String, String)
     */
    @GET("v2/bot/{sourceType}/{senderId}/members/ids")
    Call<MembersIdsResponse> getMembersIds(
            @Path("sourceType") String sourceType,
            @Path("senderId") String senderId,
            @Query("start") String start);

    /**
     * @see LineMessagingClient#leaveGroup(String)
     */
    @POST("v2/bot/group/{groupId}/leave")
    Call<BotApiResponse> leaveGroup(@Path("groupId") String groupId);

    /**
     * @see LineMessagingClient#leaveRoom(String)
     */
    @POST("v2/bot/room/{roomId}/leave")
    Call<BotApiResponse> leaveRoom(@Path("roomId") String roomId);

    /**
     * @see LineMessagingClient#getRichMenu(String)
     */
    @GET("v2/bot/richmenu/{richMenuId}")
    Call<RichMenuResponse> getRichMenu(@Path("richMenuId") String richMenuId);

    /**
     * @see LineMessagingClient#createRichMenu(RichMenu)
     */
    @POST("v2/bot/richmenu")
    Call<RichMenuIdResponse> createRichMenu(@Body RichMenu richMenu);

    /**
     * @see LineMessagingClient#deleteRichMenu(String)
     */
    @DELETE("v2/bot/richmenu/{richMenuId}")
    Call<Void> deleteRichMenu(@Path("richMenuId") String richMenuId);

    /**
     * @see LineMessagingClient#getRichMenuIdOfUser(String)
     */
    @GET("v2/bot/user/{userId}/richmenu")
    Call<RichMenuIdResponse> getRichMenuIdOfUser(@Path("userId") String userId);

    /**
     * @see LineMessagingClient#linkRichMenuIdToUser(String, String)
     */
    @POST("v2/bot/user/{userId}/richmenu/{richMenuId}")
    Call<Void> linkRichMenuIdToUser(
            @Path("userId") String userId,
            @Path("richMenuId") String richMenuId);

    /**
     * @see LineMessagingClient#unlinkRichMenuIdFromUser(String)
     */
    @DELETE("v2/bot/user/{userId}/richmenu")
    Call<Void> unlinkRichMenuIdFromUser(@Path("userId") String userId);

    /**
     * @see LineMessagingClient#getRichMenuImage(String)
     */
    @GET("v2/bot/richmenu/{richMenuId}/content")
    Call<ResponseBody> getRichMenuImage(@Path("richMenuId") String richMenuId);

    /**
     * @see LineMessagingClient#setRichMenuImage(String, String, byte[])
     */
    @POST("v2/bot/richmenu/{richMenuId}/content")
    Call<Void> setRichMenuImage(
            @Path("richMenuId") String richMenuId,
            @Body RequestBody requestBody);

    /**
     * @see LineMessagingClient#getRichMenuList()
     */
    @GET("v2/bot/richmenu/list")
    Call<RichMenuListResponse> getRichMenuList();
}
