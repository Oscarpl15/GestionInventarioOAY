package com.oay.gestioninventariooay.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {

    @FXML
    private TabPane tabPrincipal;

    @FXML
    public void initialize() {
        if (tabPrincipal != null) {
            tabPrincipal.getSelectionModel().select(0);
        }
    }

    @FXML
    protected void mostrarMateriales() {
        tabPrincipal.getSelectionModel().select(0);
    }

    @FXML
    protected void mostrarHerramientas() {
        tabPrincipal.getSelectionModel().select(1);
    }

    @FXML
    protected void mostrarAceites() {
        tabPrincipal.getSelectionModel().select(2);
    }
}
