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
import lombok.AllArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@AllArgsConstructor
class LineMessagingSyncClientImpl<T extends Exception> implements LineMessagingSyncClient<T> {
    private final LineMessagingClient asyncClient;
    private final Function<Exception, T> exceptionConverter;

    @Override
    public BotApiResponse replyMessage(ReplyMessage replyMessage) throws T {
        return wait(asyncClient.replyMessage(replyMessage));
    }

    @Override
    public BotApiResponse pushMessage(PushMessage pushMessage) throws T {
        return wait(asyncClient.pushMessage(pushMessage));
    }

    @Override
    public BotApiResponse multicast(Multicast multicast) throws T {
        return wait(asyncClient.multicast(multicast));
    }

    @Override
    public MessageContentResponse getMessageContent(String messageId) throws T {
        return wait(asyncClient.getMessageContent(messageId));
    }

    @Override
    public UserProfileResponse getProfile(String userId) throws T {
        return wait(asyncClient.getProfile(userId));
    }

    @Override
    public UserProfileResponse getGroupMemberProfile(String groupId, String userId) throws T {
        return wait(asyncClient.getGroupMemberProfile(groupId, userId));
    }

    @Override
    public UserProfileResponse getRoomMemberProfile(String roomId, String userId) throws T {
        return wait(asyncClient.getRoomMemberProfile(roomId,userId));
    }

    @Override
    public MembersIdsResponse getGroupMembersIds(String groupId, String start) throws T {
        return wait(asyncClient.getGroupMembersIds(groupId, start));
    }

    @Override
    public MembersIdsResponse getRoomMembersIds(String roomId, String start) throws T {
        return wait(asyncClient.getRoomMembersIds(roomId, start));
    }

    @Override
    public BotApiResponse leaveGroup(String groupId) throws T {
        return wait(asyncClient.leaveGroup(groupId));
    }

    @Override
    public BotApiResponse leaveRoom(String roomId) throws T {
        return wait(asyncClient.leaveRoom(roomId));
    }

    @Override
    public RichMenuResponse getRichMenu(String richMenuId) throws T {
        return wait(asyncClient.getRichMenu(richMenuId));
    }

    @Override
    public RichMenuIdResponse createRichMenu(RichMenu richMenu) throws T {
        return wait(asyncClient.createRichMenu(richMenu));
    }

    @Override
    public BotApiResponse deleteRichMenu(String richMenuId) throws T {
        return wait(asyncClient.deleteRichMenu(richMenuId));
    }

    @Override
    public RichMenuIdResponse getRichMenuIdOfUser(String userId) throws T {
        return wait(asyncClient.getRichMenuIdOfUser(userId));
    }

    @Override
    public BotApiResponse linkRichMenuIdToUser(String userId, String richMenuId) throws T {
        return wait(asyncClient.linkRichMenuIdToUser(userId, richMenuId));
    }

    @Override
    public BotApiResponse unlinkRichMenuIdFromUser(String userId) throws T {
        return wait(asyncClient.unlinkRichMenuIdFromUser(userId));
    }

    @Override
    public MessageContentResponse getRichMenuImage(String richMenuId) throws T {
        return wait(asyncClient.getRichMenuImage(richMenuId));
    }

    @Override
    public BotApiResponse setRichMenuImage(String richMenuId, String contentType, byte[] content) throws T {
        return wait(asyncClient.setRichMenuImage(richMenuId, contentType, content));
    }

    @Override
    public RichMenuListResponse getRichMenuList() throws T {
        return wait(asyncClient.getRichMenuList());
    }

    public LineMessagingClient async() {
        return asyncClient;
    }

    private <U> U wait(CompletableFuture<U> future) throws T {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw exceptionConverter.apply(e);
        }
    }
}
