package it.unisalento.faro.dto.main.list;

import it.unisalento.faro.dto.main.UserDTO;

import java.util.List;

public class UsersListDTO {

    private List<UserDTO> usersList;

    public List<UserDTO> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<UserDTO> usersList) {
        this.usersList = usersList;
    }
}
