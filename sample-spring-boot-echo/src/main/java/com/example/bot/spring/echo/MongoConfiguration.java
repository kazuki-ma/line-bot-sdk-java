package com.example.bot.spring.echo;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static fr.javatic.mongo.jacksonCodec.ObjectMapperFactory.createObjectMapper;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.bot.spring.echo.LocationRepository.Location;
import com.example.bot.spring.echo.MapRepository.Map;
import com.example.bot.spring.echo.SessionStorage.Session;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;

@Configuration
public class MongoConfiguration {
    @Bean
    public MongoClient mongoClient(final MongoProperties mongoProperties) {
        final ServerAddress address = new ServerAddress(mongoProperties.getHost());
        final CodecRegistry codecRegistry =
                fromRegistries(getDefaultCodecRegistry(),
                               fromProviders(new JacksonCodecProvider(createObjectMapper())));

        final MongoClientOptions clientOptions =
                MongoClientOptions.builder()
                                  .codecRegistry(codecRegistry)
                                  .build();

        return new MongoClient(address, clientOptions);
    }

    @Bean
    public MongoCollection<Location> locationMongoCollection(MongoClient mongoClient) {
        return mongoClient.getDatabase("group").getCollection("location", Location.class);
    }

    @Bean
    public MongoCollection<Map> mapMongoCollection(MongoClient mongoClient) {
        return mongoClient.getDatabase("group").getCollection("map", Map.class);
    }

    @Bean
    public MongoCollection<Session> sessionMongoCollection(MongoClient mongoClient) {
        return mongoClient.getDatabase("group").getCollection("session", Session.class);
    }
}
