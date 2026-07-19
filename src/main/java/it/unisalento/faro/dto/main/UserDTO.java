package it.unisalento.faro.dto.main;

public class UserDTO {

    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCurrentAreaId() {
        return currentAreaId;
    }

    public void setCurrentAreaId(String currentAreaId) {
        this.currentAreaId = currentAreaId;
    }
}