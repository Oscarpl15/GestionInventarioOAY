package com.oay.gestioninventariooay.util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UtilidadesUI {

    public static void aplicarModoEdicion(Button btnModificar, Label lblModo) {
        btnModificar.setText("Guardar Cambios");
        btnModificar.getStyleClass().remove("boton-lectura-activo");
        if (!btnModificar.getStyleClass().contains("boton-edicion-activo")) {
            btnModificar.getStyleClass().add("boton-edicion-activo");
        }

        lblModo.setText("[MODO EDICIÓN]");
        lblModo.getStyleClass().remove("label-modo-lectura");
        if (!lblModo.getStyleClass().contains("label-modo-edicion")) {
            lblModo.getStyleClass().add("label-modo-edicion");
        }
    }

    public static void aplicarModoLectura(Button btnModificar, Label lblModo) {
        btnModificar.setText("Modificar Datos");
        btnModificar.getStyleClass().remove("boton-edicion-activo");
        if (!btnModificar.getStyleClass().contains("boton-lectura-activo")) {
            btnModificar.getStyleClass().add("boton-lectura-activo");
        }

        lblModo.setText("[SOLO LECTURA]");
        lblModo.getStyleClass().remove("label-modo-edicion");
        if (!lblModo.getStyleClass().contains("label-modo-lectura")) {
            lblModo.getStyleClass().add("label-modo-lectura");
        }
    }

    public static void reajustarVentana(Button btn) {
        // Obliga a la ventana modal a encogerse o estirarse según el contenido visible
        if (btn.getScene() != null && btn.getScene().getWindow() != null) {
            btn.getScene().getWindow().sizeToScene();
        }
    }
}