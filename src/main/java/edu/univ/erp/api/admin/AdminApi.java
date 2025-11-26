package edu.univ.erp.api.admin;

import edu.univ.erp.data.AdminStore;
import edu.univ.erp.domain.UserAdminItem;
import java.util.List;

public class AdminApi {

    private final AdminStore adminStore = new AdminStore();

    public List<UserAdminItem> getUsers() {
        return adminStore.getAllUsers();
    }

    public boolean addUser(String username, String password, String role, String extra1, String extra2) {
        return adminStore.addUser(username, password, role, extra1, extra2);
    }
}