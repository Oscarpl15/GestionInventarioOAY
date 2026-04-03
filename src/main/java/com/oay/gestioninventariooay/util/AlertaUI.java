package com.oay.gestioninventariooay.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class AlertaUI {

    private static void aplicarEstilo(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        try {
            // Le inyectamos el CSS del programa para que no parezca una alerta genérica de Windows
            dialogPane.getStylesheets().add(AlertaUI.class.getResource("/com/oay/gestioninventariooay/css/estilos.css").toExternalForm());
            dialogPane.getStyleClass().add("alerta-custom");
        } catch (Exception e) {
            System.err.println("No se pudo cargar el CSS de las alertas.");
        }
    }

    public static void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Warning tiene un icono más suave que el Error
        alert.setTitle("Atención");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    public static void mostrarErrorCritico(String titulo, String detalle) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Grave");
        alert.setHeaderText(titulo);
        alert.setContentText(detalle);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    public static void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    public static boolean pedirConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Acción");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        aplicarEstilo(alert);

        ButtonType btnSi = new ButtonType("Sí, eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnSi, btnNo);

        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == btnSi;
    }
}