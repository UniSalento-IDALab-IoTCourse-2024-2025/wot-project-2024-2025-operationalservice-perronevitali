package it.unisalento.faro.dto.responseDTO;

import it.unisalento.faro.dto.main.list.UsersListDTO;

public class UserResponseDTO extends BaseResponseDTO {
    public static final int USER_NOT_FOUND = 1;
    public static final int CANT_EDIT_EMAIL = 2;

    private UsersListDTO users;

    public UsersListDTO getUsers() {
        return users;
    }

    public void setUsers(UsersListDTO users) {
        this.users = users;
    }
}