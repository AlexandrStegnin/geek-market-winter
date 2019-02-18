package com.geekbrains.geekmarketwinter.maintenance;

import com.geekbrains.geekmarketwinter.maintenance.actions.MaintenanceAction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author stegnin
 */

@Service
public class MaintenanceService {

    private final List<MaintenanceAction> actions;

    public MaintenanceService(List<MaintenanceAction> actions) {
        this.actions = actions;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void maintenanceConfigure() {
        try {
            actions.forEach(MaintenanceAction::execute);
        } catch (Exception ignored) {

        }
    }

}
