package it.unisalento.faro.dto.responseDTO;

import it.unisalento.faro.dto.main.list.WorkersListDTO;

public class RegistrationResponseDTO extends BaseResponseDTO {
    public static final int EMAIL_ALREADY_EXIST = 1;

    private WorkersListDTO users;

    public WorkersListDTO getUsers() { return users; }
    public void setUsers(WorkersListDTO users) { this.users = users; }
}