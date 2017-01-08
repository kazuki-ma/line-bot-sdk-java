package com.example.bot.spring.echo;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public Map create(final Map map) {
        collection.insertOne(map);
        return map;
    }

    public List<Map> findBySource(final Source source) {
        return ImmutableList.copyOf(
                collection.find(eq("owner", source.getSenderId())).iterator());
    }

    public Map find(final String id) {
        return collection.find(eq("_id", id)).first();
    }

    public void update(Map map) {
        collection.findOneAndReplace(eq("_id", map.get_id()), map);
    }

    public Map delete(String mapId) {
        return collection.findOneAndDelete(eq("_id", mapId));
    }

    @Data
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Map extends MongoDocument {
        {
            _id = RandomStringUtils.randomAlphanumeric(8);
        }

        String owner;
        String name;
    }
}
