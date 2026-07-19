package it.unisalento.faro.dto.login_and_registration;

public class AdminRegistrationDTO {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String managedAreaId;

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

    public String getManagedAreaId() {
        return managedAreaId;
    }

    public void setManagedAreaId(String managedAreaId) {
        this.managedAreaId = managedAreaId;
    }
}