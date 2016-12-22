package com.example.bot.spring.echo;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class MongoDocument {
    String _id = UUID.randomUUID().toString();
    Instant created;
    Instant updated;
}
