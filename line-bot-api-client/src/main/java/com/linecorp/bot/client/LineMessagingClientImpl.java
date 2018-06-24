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

import static com.linecorp.bot.internal.FutureConverter.toBotApiFuture;
import static com.linecorp.bot.internal.FutureConverter.toFuture;
import static com.linecorp.bot.internal.FutureConverter.toMessageContentResponseFuture;

import java.util.concurrent.CompletableFuture;

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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Proxy implementation of {@link LineMessagingClient} to hind internal implementation.
 */
@Slf4j
@AllArgsConstructor(onConstructor = @__(@SuppressWarnings("deprecation")))
public class LineMessagingClientImpl implements LineMessagingClient {
    private static final String ORG_TYPE_GROUP = "group"; // TODO Enum
    private static final String ORG_TYPE_ROOM = "room";

    @SuppressWarnings("deprecation")
    private final LineMessagingService retrofitImpl;

    @Override
    public CompletableFuture<BotApiResponse> replyMessage(final ReplyMessage replyMessage) {
        return toFuture(retrofitImpl.replyMessage(replyMessage));
    }

    @Override
    public CompletableFuture<BotApiResponse> pushMessage(final PushMessage pushMessage) {
        return toFuture(retrofitImpl.pushMessage(pushMessage));
    }

    @Override
    public CompletableFuture<BotApiResponse> multicast(final Multicast multicast) {
        return toFuture(retrofitImpl.multicast(multicast));
    }

    @Override
    public CompletableFuture<MessageContentResponse> getMessageContent(final String messageId) {
        return toMessageContentResponseFuture(retrofitImpl.getMessageContent(messageId));
    }

    @Override
    public CompletableFuture<UserProfileResponse> getProfile(final String userId) {
        return toFuture(retrofitImpl.getProfile(userId));
    }

    @Override
    public CompletableFuture<UserProfileResponse> getGroupMemberProfile(
            final String groupId, final String userId) {
        return toFuture(retrofitImpl.getMemberProfile(ORG_TYPE_GROUP, groupId, userId));
    }

    @Override
    public CompletableFuture<UserProfileResponse> getRoomMemberProfile(
            final String roomId, final String userId) {
        return toFuture(retrofitImpl.getMemberProfile(ORG_TYPE_ROOM, roomId, userId));
    }

    @Override
    public CompletableFuture<MembersIdsResponse> getGroupMembersIds(
            final String groupId, final String start) {
        return toFuture(retrofitImpl.getMembersIds(ORG_TYPE_GROUP, groupId, start));
    }

    @Override
    public CompletableFuture<MembersIdsResponse> getRoomMembersIds(
            final String roomId, final String start) {
        return toFuture(retrofitImpl.getMembersIds(ORG_TYPE_ROOM, roomId, start));
    }

    @Override
    public CompletableFuture<BotApiResponse> leaveGroup(final String groupId) {
        return toFuture(retrofitImpl.leaveGroup(groupId));
    }

    @Override
    public CompletableFuture<BotApiResponse> leaveRoom(final String roomId) {
        return toFuture(retrofitImpl.leaveRoom(roomId));
    }

    @Override
    public CompletableFuture<RichMenuResponse> getRichMenu(final String richMenuId) {
        return toFuture(retrofitImpl.getRichMenu(richMenuId));
    }

    @Override
    public CompletableFuture<RichMenuIdResponse> createRichMenu(final RichMenu richMenu) {
        return toFuture(retrofitImpl.createRichMenu(richMenu));
    }

    @Override
    public CompletableFuture<BotApiResponse> deleteRichMenu(final String richMenuId) {
        return toBotApiFuture(retrofitImpl.deleteRichMenu(richMenuId));
    }

    @Override
    public CompletableFuture<RichMenuIdResponse> getRichMenuIdOfUser(final String userId) {
        return toFuture(retrofitImpl.getRichMenuIdOfUser(userId));
    }

    @Override
    public CompletableFuture<BotApiResponse> linkRichMenuIdToUser(
            final String userId, final String richMenuId) {
        return toBotApiFuture(retrofitImpl.linkRichMenuToUser(userId, richMenuId));
    }

    @Override
    public CompletableFuture<BotApiResponse> unlinkRichMenuIdFromUser(final String userId) {
        return toBotApiFuture(retrofitImpl.unlinkRichMenuIdFromUser(userId));
    }

    @Override
    public CompletableFuture<MessageContentResponse> getRichMenuImage(final String richMenuId) {
        return toMessageContentResponseFuture(retrofitImpl.getRichMenuImage(richMenuId));
    }

    @Override
    public CompletableFuture<BotApiResponse> setRichMenuImage(
            final String richMenuId, final String contentType, final byte[] content) {
        final RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), content);
        return toBotApiFuture(retrofitImpl.uploadRichMenuImage(richMenuId, requestBody));
    }

    @Override
    public CompletableFuture<RichMenuListResponse> getRichMenuList() {
        return toFuture(retrofitImpl.getRichMenuList());
    }
}
