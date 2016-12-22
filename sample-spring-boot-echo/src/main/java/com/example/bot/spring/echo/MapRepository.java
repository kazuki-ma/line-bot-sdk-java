package com.example.bot.spring.echo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

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
                collection.find(Filters.eq("owner", source.getSenderId())).iterator());
    }

    public Map find(final String id) {
        return collection.find(Filters.eq("_id", id)).iterator().next();
    }

    @Data
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Map extends MongoDocument {
        String owner;
        String name;
    }
}
