package it.unisalento.faro.domain;

import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import io.quarkus.mongodb.panache.common.MongoEntity;

@BsonDiscriminator
@MongoEntity(collection = "users")
public class User {

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private Role role;
    private String currentAreaId;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCognome() {
        return cognome;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public String getCurrentAreaId() {
        return currentAreaId;
    }
    public void setCurrentAreaId(String currentAreaId) {
        this.currentAreaId = currentAreaId;
    }
}