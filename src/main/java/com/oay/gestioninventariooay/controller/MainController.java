package com.oay.gestioninventariooay.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;

public class MainController {

    @FXML private TabPane tabPrincipal;
    @FXML private ToggleGroup menuGroup;

    @FXML
    public void initialize() {
        if (tabPrincipal != null) {
            tabPrincipal.getSelectionModel().select(0);
        }

        // Evita que el botón activo se pueda deseleccionar al hacerle clic de nuevo
        if (menuGroup != null) {
            menuGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null && oldVal != null) {
                    oldVal.setSelected(true);
                }
            });
        }
    }

    @FXML protected void mostrarMateriales() { tabPrincipal.getSelectionModel().select(0); }
    @FXML protected void mostrarHerramientas() { tabPrincipal.getSelectionModel().select(1); }
    @FXML protected void mostrarAceites() { tabPrincipal.getSelectionModel().select(2); }
}
