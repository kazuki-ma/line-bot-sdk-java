package com.example.bot.spring.echo;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.SessionStorage.Session;
import com.google.common.collect.ImmutableMap;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.AllArgsConstructor;

@LineMessageHandler
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MapManagementHandler {
    public static final String CHANGE_NAME = "CHANGE_NAME";
    public static final String CHANGE_NAME_CONFIRM = "CHANGE_NAME_CONFIRM";
    public static final String TEXT_CHANGE_NAME_PREFIX = "地図の名前を変える";
    public static final String TEXT_CHANGE_NAME = TEXT_CHANGE_NAME_PREFIX + "\nid=(.*)";

    private final MapRepository mapRepository;
    private final SessionStorage sessionStorage;
    private final ImageMapGenerator imageMapGenerator;

    @EventMapping(text = TEXT_CHANGE_NAME)
    public List<? extends Message> handleChangeNameRequest(final MessageEvent<TextMessageContent> event) {
        final Matcher matcher = Pattern.compile(TEXT_CHANGE_NAME).matcher(event.getMessage().getText());
        final String mapId;
        if (matcher.find()) {
            mapId = matcher.group(1);
        } else {
            mapId = mapRepository.findBySource(event.getSource()).get(0).get_id();
        }
        sessionStorage.set(new Session(event.getSource())
                                   .setContext(CHANGE_NAME)
                                   .setData(singletonMap("mapId", mapId)));
        return singletonList(new TextMessage("新しい名前を入力して下さい"));
    }

    @EventMapping(context = CHANGE_NAME)
    public List<? extends Message> handleChangeName(
            final MessageEvent<TextMessageContent> event) {
        final String text = event.getMessage().getText();

        final String message = text + "に名前を変更します";
        final TemplateMessage templateMessage =
                new TemplateMessage(message + "\nよろしければ、OK  と入力して下さい",
                                    new ConfirmTemplate(message,
                                                        new MessageAction("Cancel", "Cancel"),
                                                        new MessageAction("OK", "OK")));

        final String mapId = getMapId(event);
        sessionStorage.set(new Session(event.getSource())
                                   .setContext(CHANGE_NAME_CONFIRM)
                                   .setData(ImmutableMap.of("mapId", mapId,
                                                            "name", text)));

        return singletonList(templateMessage);
    }

    @EventMapping(context = CHANGE_NAME_CONFIRM)
    public List<? extends Message> handleChangeNameConfirm(
            final MessageEvent<TextMessageContent> event) {
        final java.util.Map<String, String> context = sessionStorage.getMap(event.getSource());
        final String mapId = getMapId(event);
        final String newName = context.get("name");

        switch (event.getMessage().getText().trim().toUpperCase()) {
            case "OK":
                final Map map = mapRepository.find(mapId);
                map.setName(newName);
                mapRepository.update(map);

                sessionStorage.set(new Session(event.getSource())); // reset session

                return asList(new TextMessage(newName + " に名前を変更しました"),
                              imageMapGenerator.create(map));
        }

        sessionStorage.set(new Session(event.getSource())); // reset session

        return singletonList(new TextMessage("名前の変更をキャンセルしました"));
    }

    private String getMapId(MessageEvent<TextMessageContent> event) {
        return sessionStorage.getMap(event.getSource()).get("mapId");
    }
}
