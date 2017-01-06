package com.example.bot.spring.echo;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;

import com.linecorp.bot.model.event.source.Source;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SessionStorage {
    public static final FindOneAndReplaceOptions UPSERT = new FindOneAndReplaceOptions().upsert(true);
    private final MongoCollection<Session> collection;

    public void set(final Session session) {
        collection.findOneAndReplace(eq("_id", session.get_id()), session, UPSERT);
    }

    public String getContext(final Source source) {
        return Optional.ofNullable(collection.find(eq("_id", source.getSenderId())).first())
                       .map(Session::getContext)
                       .orElse("");
    }

    public Map<String, String> getMap(final Source source) {
        return Optional.ofNullable(collection.find(eq("_id", source.getSenderId())).first())
                       .map(Session::getData)
                       .orElse(emptyMap());
    }

    public Session delete(final String id) {
        return collection.findOneAndDelete(eq("_id", id));
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__(@JsonCreator))
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Session extends MongoDocument {
        public Session(final Source source) {
            this._id = source.getSenderId();
        }

        String context;
        Map<String, String> data = emptyMap();
    }
}
