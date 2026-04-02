package com.oay.gestioninventariooay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/com/oay/gestioninventariooay/view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Taller OAY - Gestión de Inventario");
        stage.setScene(scene);

        // Calcular pantalla y forzar maximizado
        Rectangle2D limitesPantalla = Screen.getPrimary().getVisualBounds();
        stage.setX(limitesPantalla.getMinX());
        stage.setY(limitesPantalla.getMinY());
        stage.setWidth(limitesPantalla.getWidth());
        stage.setHeight(limitesPantalla.getHeight());
        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
