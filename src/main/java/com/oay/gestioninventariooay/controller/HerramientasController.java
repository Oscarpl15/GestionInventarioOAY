package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.Launcher;
import com.oay.gestioninventariooay.model.Herramienta;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HerramientasController {

    @FXML private TableView<Herramienta> tablaHerramientas;
    @FXML private TableColumn<Herramienta, String> colTipo;
    @FXML private TableColumn<Herramienta, String> colTipoOperacion;
    @FXML private TableColumn<Herramienta, String> colReferencia;
    @FXML private TableColumn<Herramienta, String> colMaterialMecanizar;
    @FXML private TableColumn<Herramienta, Integer> colCantidad;
    @FXML private ComboBox<String> cmbFiltroTipo;

    private InventarioService service = new InventarioService();
    private ObservableList<Herramienta> listaHerramientas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        colReferencia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReferencia() != null ? cellData.getValue().getReferencia() : ""));
        colMaterialMecanizar.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaterialMecanizar()));
        colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());

        // Lógica para extraer el Tipo de Operación
        colTipoOperacion.setCellValueFactory(cellData -> {
            Herramienta h = cellData.getValue();
            String operacion = "-";
            if ("Torno".equals(h.getCategoria()) && h.getTipoMecanizado() != null) {
                operacion = h.getTipoMecanizado();
            } else if ("Fresa".equals(h.getCategoria()) && h.getTipoFresa() != null) {
                operacion = h.getTipoFresa();
            } else if ("Macho".equals(h.getCategoria()) && h.getTipoRosca() != null) {
                operacion = h.getTipoRosca();
            }
            return new SimpleStringProperty(operacion);
        });

        cmbFiltroTipo.getSelectionModel().selectFirst();

        tablaHerramientas.setRowFactory(tv -> {
            TableRow<Herramienta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    abrirFichaHerramienta(row.getItem()); // NUEVO
                }
            });
            return row;
        });

        cargarDatos();
    }

    public void cargarDatos() {
        listaHerramientas.setAll(service.obtenerHerramientas());
        tablaHerramientas.setItems(listaHerramientas);
    }

    @FXML
    private void nuevaHerramienta() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/com/oay/gestioninventariooay/view/agregarHerramienta-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Añadir Herramienta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirFichaHerramienta(Herramienta herramienta) {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/com/oay/gestioninventariooay/view/ficha-herramienta.fxml"));
            Parent root = loader.load();

            FichaHerramientaController controlador = loader.getController();
            controlador.initData(herramienta, this);

            Stage stage = new Stage();
            stage.setTitle("Ficha Detalle - " + herramienta.getCategoria());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
