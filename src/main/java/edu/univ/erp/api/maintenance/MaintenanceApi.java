package edu.univ.erp.api.maintenance;

import edu.univ.erp.data.SettingsStore;

public class MaintenanceApi {
    private static final SettingsStore store = new SettingsStore();

    // Static method so it can be called easily from anywhere
    public static boolean isMaintenanceOn() {
        return store.isMaintenanceMode();
    }

    public static void setMaintenance(boolean on) {
        store.setMaintenanceMode(on);
    }
}