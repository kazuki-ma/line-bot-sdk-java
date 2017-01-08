package com.example.bot.spring.echo;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.bot.spring.echo.SessionStorage.Session;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;

import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.spring.boot.support.ISession;
import com.linecorp.bot.spring.boot.support.SessionStorageIface;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SessionStorage implements SessionStorageIface<Session> {
    public static final FindOneAndReplaceOptions UPSERT = new FindOneAndReplaceOptions().upsert(true);
    private final MongoCollection<Session> collection;

    @Override
    public void set(final Session session) {
        collection.findOneAndReplace(eq("_id", session.get_id()), session, UPSERT);
    }

    @Override
    public Session getSession(Source source) {
        return Optional.ofNullable(collection.find(eq("_id", source.getSenderId())).first())
                       .orElse(new Session());
    }

    @Override
    public String getContext(final Source source) {
        return Optional.ofNullable(collection.find(eq("_id", source.getSenderId())).first())
                       .map(Session::getContext)
                       .orElse("");
    }

    @Override
    public Map<String, String> getMap(final Source source) {
        return Optional.ofNullable(collection.find(eq("_id", source.getSenderId())).first())
                       .map(Session::getData)
                       .orElse(emptyMap());
    }

    @Override
    public Session delete(final Source source) {
        return collection.findOneAndDelete(eq("_id", source.getSenderId()));
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__(@JsonCreator))
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Session extends MongoDocument implements ISession {
        public Session(final Source source) {
            this._id = source.getSenderId();
        }

        String context;
        Map<String, String> data = emptyMap();
    }
}
