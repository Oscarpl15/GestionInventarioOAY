package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.Launcher;
import com.oay.gestioninventariooay.model.Aceite;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AceitesController {

    @FXML private TableView<Aceite> tablaAceites;
    @FXML private TableColumn<Aceite, String> colMaquina;
    @FXML private TableColumn<Aceite, String> colTipo;
    @FXML private TableColumn<Aceite, Double> colCapacidad;
    @FXML private TableColumn<Aceite, Integer> colCantidad;
    @FXML private ComboBox<String> cmbFiltroMaquina;

    private InventarioService service = new InventarioService();
    private ObservableList<Aceite> listaAceites = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMaquina.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaquinaDestino()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoAceite()));
        colCapacidad.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCapacidadLitros()).asObject());
        colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());

        cmbFiltroMaquina.getSelectionModel().selectFirst();

        tablaAceites.setRowFactory(tv -> {
            TableRow<Aceite> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    abrirFichaAceite(row.getItem()); // NUEVO
                }
            });
            return row;
        });

        cargarDatos();
    }

    public void cargarDatos() {
        listaAceites.setAll(service.obtenerAceites());
        tablaAceites.setItems(listaAceites);
    }

    @FXML
    private void nuevoAceite() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/com/oay/gestioninventariooay/view/agregarAceite-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Añadir Aceite");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirFichaAceite(Aceite aceite) {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/com/oay/gestioninventariooay/view/ficha-aceite.fxml"));
            Parent root = loader.load();

            FichaAceiteController controlador = loader.getController();
            controlador.initData(aceite, this);

            Stage stage = new Stage();
            stage.setTitle("Ficha Detalle - " + aceite.getTipoAceite());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
