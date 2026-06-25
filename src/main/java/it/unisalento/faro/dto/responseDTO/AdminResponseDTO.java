package it.unisalento.faro.dto.responseDTO;

import it.unisalento.faro.dto.main.AdminsListDTO;

public class AdminResponseDTO extends BaseResponseDTO {
    public static final int ADMIN_NOT_FOUND = 1;
    public static final int EMAIL_ALREADY_EXISTS = 2;

    private AdminsListDTO admins;

    public AdminsListDTO getAdmins() { return admins; }
    public void setAdmins(AdminsListDTO admins) { this.admins = admins; }
}