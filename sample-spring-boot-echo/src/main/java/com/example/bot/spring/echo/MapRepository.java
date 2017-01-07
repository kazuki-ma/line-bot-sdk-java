package com.example.bot.spring.echo;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;

import com.linecorp.bot.model.event.source.Source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MapRepository {
    private final MongoCollection<Map> collection;

    public void create(final Map map) {
        collection.insertOne(map);
    }

    public List<Map> findBySource(final Source source) {
        return ImmutableList.copyOf(
                collection.find(eq("owner", source.getSenderId())).iterator());
    }

    public Map find(final String id) {
        return collection.find(eq("_id", id)).iterator().next();
    }

    public void update(Map map) {
        collection.findOneAndReplace(eq("_id", map.get_id()), map);
    }

    @Data
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Map extends MongoDocument {
        {
            _id = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes());
        }

        String owner;
        String name;
    }
}
