package com.example.bot.spring.echo;

import static com.mongodb.client.model.Filters.eq;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LocationRepository {
    private final MongoCollection<Location> collection;

    public void create(final Location location) {
        collection.insertOne(location);
    }

    public Location get(final String locationId) {
        return collection.find(eq("_id", locationId)).first();
    }

    public void setMap(final Location location, final String mapId) {
        collection.updateMany(eq("_id", location.get_id()),
                              Updates.set("mapId", mapId));
    }

    public List<Location> read(final String mapId) {
        return ImmutableList.copyOf(collection.find(eq("mapId", mapId)));
    }

    public Location delete(final String id) {
        return collection.findOneAndDelete(eq("_id", id));
    }

    @Data
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class Location extends MongoDocument {
        String mapId;
        String title;
        String description;
        URI url;
        URI image;
        double latitude;
        double longitude;
    }
}
