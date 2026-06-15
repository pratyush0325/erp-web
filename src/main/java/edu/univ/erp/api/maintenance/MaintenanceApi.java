package edu.univ.erp.api.maintenance;

import edu.univ.erp.data.SettingsStore;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceApi {

    private final SettingsStore store;

    public MaintenanceApi(SettingsStore store) {
        this.store = store;
    }

    public boolean isMaintenanceOn() {
        return store.isMaintenanceMode();
    }

    public void setMaintenance(boolean on) {
        store.setMaintenanceMode(on);
    }
}
