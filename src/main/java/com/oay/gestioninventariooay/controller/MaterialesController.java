package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.Launcher;
import com.oay.gestioninventariooay.model.Material;
import com.oay.gestioninventariooay.service.InventarioService;
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

public class MaterialesController {

    @FXML private TableView<Material> tablaMateriales;
    @FXML private TableColumn<Material, String> colCategoria;
    @FXML private TableColumn<Material, String> colTipo;
    @FXML private TableColumn<Material, String> colTamano;
    @FXML private TableColumn<Material, String> colCantidad;
    @FXML private ComboBox<String> cmbFiltroCategoria;

    private InventarioService service = new InventarioService();
    private ObservableList<Material> listaMateriales = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoMaterial()));

        // LÓGICA DE LA NUEVA COLUMNA: TAMAÑO
        colTamano.setCellValueFactory(cellData -> {
            Material m = cellData.getValue();
            String tamano = "-";
            if ("Redonda".equals(m.getCategoria()) && m.getDiametro() != null) {
                tamano = "Ø " + m.getDiametro();
            } else if ("Cuadrada".equals(m.getCategoria()) && m.getLado() != null) {
                if (m.getLado2() != null) tamano = m.getLado() + " x " + m.getLado2();
                else tamano = m.getLado() + " x " + m.getLado(); // Si es cuadrada perfecta
            } else if ("Plancha".equals(m.getCategoria()) && m.getLargo() != null && m.getAncho() != null && m.getEspesor() != null) {
                tamano = m.getLargo() + " x " + m.getAncho() + " x " + m.getEspesor();
            }
            return new SimpleStringProperty(tamano);
        });

        // Lógica de Cantidad
        colCantidad.setCellValueFactory(cellData -> {
            Material m = cellData.getValue();
            if ("Plancha".equals(m.getCategoria())) {
                return new SimpleStringProperty((m.getUnidades() != null ? m.getUnidades() : 0) + " ud(s)");
            } else {
                return new SimpleStringProperty((m.getLongitud() != null ? m.getLongitud() : 0.0) + " mm");
            }
        });

        cmbFiltroCategoria.getSelectionModel().selectFirst();

        tablaMateriales.setRowFactory(tv -> {
            TableRow<Material> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    abrirFichaMaterial(row.getItem());
                }
            });
            return row;
        });

        cargarDatos();
    }

    public void cargarDatos() {
        listaMateriales.setAll(service.obtenerMateriales());
        tablaMateriales.setItems(listaMateriales);
    }

    @FXML
    private void nuevoMaterial() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/com/oay/gestioninventariooay/view/agregarMaterial-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Añadir Material");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirFichaMaterial(Material material) {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/com/oay/gestioninventariooay/view/ficha-material.fxml"));
            Parent root = loader.load();

            FichaMaterialController controlador = loader.getController();
            controlador.initData(material, this);

            Stage stage = new Stage();
            stage.setTitle("Ficha Detalle - " + material.getTipoMaterial());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}