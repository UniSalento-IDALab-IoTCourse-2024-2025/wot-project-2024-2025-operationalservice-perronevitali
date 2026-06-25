package it.unisalento.faro.domain;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

import java.util.ArrayList;

@MongoEntity(collection = "user")
public class Task {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;
    private String nome;
    private String descrizione;
    private ArrayList<User> addetti;

}