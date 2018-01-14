package com.linecorp.bot.client;

import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.profile.MembersIdsResponse;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.richmenu.RichMenu;
import com.linecorp.bot.model.richmenu.RichMenuIdResponse;
import com.linecorp.bot.model.richmenu.RichMenuListResponse;
import com.linecorp.bot.model.richmenu.RichMenuResponse;

import java.util.concurrent.CompletableFuture;

public interface LineMessagingSyncClient<T extends Throwable> {
        /**
         * Reply to messages from users.
         *
         * <p>Webhooks are used to notify you when an event occurs. For events that you can respond to,
         * a replyToken is issued for replying to messages.
         * <p>Because the replyToken becomes invalid after a certain period of time,
         * responses should be sent as soon as a message is received. Reply tokens can only be used once.
         *
         * @see #pushMessage(PushMessage)
         * @see <a href="https://devdocs.line.me?java#reply-message">//devdocs.line.me#reply-message</a>
         */
        BotApiResponse replyMessage(ReplyMessage replyMessage) throws T;

        /**
         * Send messages to users when you want to.
         *
         * <p>INFO: Use of the Push Message API is limited to certain plans.
         *
         * @see #replyMessage(ReplyMessage)
         * @see <a href="https://devdocs.line.me?java#push-message">//devdocs.line.me#push-message</a>
         */
        BotApiResponse pushMessage(PushMessage pushMessage) throws T;

        /**
         * Send messages to multiple users at any time. <strong>IDs of groups or rooms cannot be used.</strong>
         *
         * <p>INFO: Only available for plans which support push messages. Messages cannot be sent to groups or rooms.
         * <p>INFO: Use IDs returned via the webhook event of source users. IDs of groups or rooms cannot be used.
         * Do not use the LINE ID found on the LINE app.</p>
         * @see #pushMessage(PushMessage)
         * @see <a href="https://devdocs.line.me?java#multicast">//devdocs.line.me#multicast</a>
         */
        BotApiResponse multicast(Multicast multicast) throws T;

        /**
         * Download image, video, and audio data sent from users.
         *
         * @see <a href="https://devdocs.line.me?java#get-content">//devdocs.line.me#get-content</a>
         */
        MessageContentResponse getMessageContent(String messageId) throws T;

        /**
         * Get user profile information.
         *
         * @see <a href="https://devdocs.line.me?java#bot-api-get-profile">//devdocs.line.me#bot-api-get-profile</a>
         */
        UserProfileResponse getProfile(String userId) throws T;

        /**
         * Get room member profile.
         *
         * @param groupId Identifier of the group. Can be get by {@link GroupSource#getGroupId()}.
         * @param userId Identifier of the user.
         *
         * @see <a href="https://devdocs.line.me?java#get-group-room-member-profile">//devdocs.line.me#get-group-room-member-profile</a>
         */
        UserProfileResponse getGroupMemberProfile(String groupId, String userId) throws T;

        /**
         * Get group member profile.
         *
         * @param roomId Identifier of the group. Can be get by {@link RoomSource#getRoomId()}.
         * @param userId Identifier of the user.
         *
         * @see <a href="https://devdocs.line.me?java#get-group-room-member-profile">//devdocs.line.me#get-group-room-member-profile</a>
         */
        UserProfileResponse getRoomMemberProfile(String roomId, String userId) throws T;

        /**
         * Get (a part of) group member list.
         *
         * @param start nullable continuationToken which can be get {@link MembersIdsResponse#getNext()}
         *
         * @see MembersIdsResponse#getNext()
         */
        MembersIdsResponse getGroupMembersIds(
                String groupId, String start) throws T;

        /**
         * Get (a part of) room member list.
         *
         * @param start nullable continuationToken which can be get {@link MembersIdsResponse#getNext()}
         *
         * @see MembersIdsResponse#getNext()
         */
        MembersIdsResponse getRoomMembersIds(
                String roomId, String start) throws T;

        /**
         * Leave a group.
         *
         * @see <a href="https://devdocs.line.me?java#leave">//devdocs.line.me#leave</a>
         */
        BotApiResponse leaveGroup(String groupId) throws T;

        /**
         * Leave a room.
         *
         * @see <a href="https://devdocs.line.me?java#leave">//devdocs.line.me#leave</a>
         */
        BotApiResponse leaveRoom(String roomId) throws T;

        /**
         * Get a rich menu.
         *
         * @see <a href="https://developers.line.me/en/docs/messaging-api/reference/#get-rich-menu"
         * >//developers.line.me/en/docs/messaging-api/reference/#get-rich-menu</a>
         */
        RichMenuResponse getRichMenu(String richMenuId) throws T;

        /**
         * Creates a rich menu.
         *
         * <p>Note: You must upload a rich menu image and link the rich menu to a user for the rich menu to be displayed. You can create up to 10 rich menus for one bot.
         *
         * @see <a href="https://developers.line.me/en/docs/messaging-api/reference/#create-rich-menu"
         * >//developers.line.me/en/docs/messaging-api/reference/#create-rich-menu</a>
         */
        RichMenuIdResponse createRichMenu(RichMenu richMenu) throws T;

        /**
         * Deletes a rich menu.
         */
        BotApiResponse deleteRichMenu(String richMenuId) throws T;

        /**
         * Get rich menu ID of user
         */
        RichMenuIdResponse getRichMenuIdOfUser(String userId) throws T;

        /**
         * Link rich menu to user
         *
         * @see <a href="https://developers.line.me/en/docs/messaging-api/reference/#link-rich-menu-to-user"
         * >//developers.line.me/en/docs/messaging-api/reference/#link-rich-menu-to-user</a>
         */
        BotApiResponse linkRichMenuIdToUser(String userId, String richMenuId) throws T;

        /**
         * Unlink rich menu from user
         *
         * @see <a href="https://developers.line.me/en/docs/messaging-api/reference/#unlink-rich-menu-from-user"
         * >//developers.line.me/en/docs/messaging-api/reference/#unlink-rich-menu-from-user</a>
         */
        BotApiResponse unlinkRichMenuIdFromUser(String userId) throws T;

        /**
         * Download rich menu image
         *
         * @see <a href="https://developers.line.me/en/docs/messaging-api/reference/#download-rich-menu-image"
         * >//developers.line.me/en/docs/messaging-api/reference/#download-rich-menu-image</a>
         */
        MessageContentResponse getRichMenuImage(String richMenuId) throws T;

        /**
         * @see <a href="https://developers.line.me/en/docs/messaging-api/reference/#upload-rich-menu-image"
         * >//developers.line.me/en/docs/messaging-api/reference/#upload-rich-menu-image</a>
         */
        BotApiResponse setRichMenuImage(
                String richMenuId, String contentType, byte[] content) throws T;

        /**
         * Gets a list of all uploaded rich menus.
         *
         * @see <a href="https://developers.line.me/en/docs/messaging-api/reference/#get-rich-menu-list"
         * >//developers.line.me/en/docs/messaging-api/reference/#get-rich-menu-list</a>
         */
        RichMenuListResponse getRichMenuList() throws T;
    }
