package com.faraimunashe.superpos.Context;

import com.faraimunashe.superpos.Controllers.PosController;

public class AppContext {
    private static AppContext instance;
    private PosController dashboardController;

    private AppContext() {
    }

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public PosController getPosController() {
        return dashboardController;
    }

    public void setPosController(PosController dashboardController) {
        this.dashboardController = dashboardController;
    }
}
