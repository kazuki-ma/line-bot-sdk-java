package com.example.bot.spring.echo;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Repository
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class Storage {
    private final MongoCollection<Location> collection;

    @Data
    @Accessors(fluent = true)
    public static class Location {
        ObjectId _id;

        String groupId;
        String title;
        double latitude;
        double longtitude;
    }
}
