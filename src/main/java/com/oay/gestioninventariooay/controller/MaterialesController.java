package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.Launcher;
import com.oay.gestioninventariooay.model.Material;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MaterialesController {

    @FXML private TableView<Material> tablaMateriales;
    @FXML private TableColumn<Material, String> colCategoria, colTipo, colTamano, colCantidad;
    @FXML private ComboBox<String> cmbFiltroCategoria, cmbFiltroMaterial;
    @FXML private TextField txtBuscar;

    private InventarioService service = new InventarioService();
    private ObservableList<Material> listaMateriales = FXCollections.observableArrayList();
    private FilteredList<Material> filteredData; // NUEVO: Para el filtrado en vivo

    @FXML
    public void initialize() {
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoMaterial()));

        colTamano.setCellValueFactory(cellData -> {
            Material m = cellData.getValue();
            String tamano = "-";
            if ("Redonda".equals(m.getCategoria()) && m.getDiametro() != null) tamano = "Ø " + m.getDiametro();
            else if ("Cuadrada".equals(m.getCategoria()) && m.getLado() != null) tamano = (m.getLado2() != null) ? m.getLado() + " x " + m.getLado2() : m.getLado() + " x " + m.getLado();
            else if ("Plancha".equals(m.getCategoria()) && m.getLargo() != null && m.getAncho() != null && m.getEspesor() != null) tamano = m.getLargo() + " x " + m.getAncho() + " x " + m.getEspesor();
            return new SimpleStringProperty(tamano);
        });

        colCantidad.setCellValueFactory(cellData -> {
            Material m = cellData.getValue();
            if ("Plancha".equals(m.getCategoria())) return new SimpleStringProperty((m.getUnidades() != null ? m.getUnidades() : 0) + " ud(s)");
            else return new SimpleStringProperty((m.getLongitud() != null ? m.getLongitud() : 0.0) + " mm");
        });

        // Configuración de los desplegables
        cmbFiltroMaterial.getItems().addAll("Todos", "Aluminio", "Acero", "Acero inoxidable", "Duraluminio", "Cobre", "Bronce", "Latón", "PVC", "Teflón");
        cmbFiltroCategoria.getSelectionModel().selectFirst();
        cmbFiltroMaterial.getSelectionModel().selectFirst();

        // Enlace del Filtrado Dinámico
        filteredData = new FilteredList<>(listaMateriales, b -> true);
        SortedList<Material> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaMateriales.comparatorProperty());
        tablaMateriales.setItems(sortedData);

        // Listeners para que actualice al instante si tocas algo
        cmbFiltroCategoria.valueProperty().addListener((obs, o, n) -> aplicarFiltros());
        cmbFiltroMaterial.valueProperty().addListener((obs, o, n) -> aplicarFiltros());
        txtBuscar.textProperty().addListener((obs, o, n) -> aplicarFiltros());

        tablaMateriales.setRowFactory(tv -> {
            TableRow<Material> row = new TableRow<>();
            // (AQUÍ DEBES DEJAR TU CÓDIGO DE ELIMINAR CON CLIC DERECHO QUE HICIMOS AYER)
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) abrirFichaMaterial(row.getItem());
            });
            return row;
        });

        cargarDatos();
    }

    private void aplicarFiltros() {
        String cat = cmbFiltroCategoria.getValue();
        String mat = cmbFiltroMaterial.getValue();
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";

        filteredData.setPredicate(m -> {
            boolean matchCat = "Todos".equals(cat) || cat.equals(m.getCategoria());
            boolean matchMat = "Todos".equals(mat) || mat.equals(m.getTipoMaterial());
            boolean matchBusq = busqueda.isEmpty() ||
                    (m.getTipoMaterial() != null && m.getTipoMaterial().toLowerCase().contains(busqueda)) ||
                    (m.getEmpresaCompra() != null && m.getEmpresaCompra().toLowerCase().contains(busqueda));
            return matchCat && matchMat && matchBusq;
        });
    }

    public void cargarDatos() {
        // Solo actualizamos la lista base, el FilteredList hace el resto
        listaMateriales.setAll(service.obtenerMateriales());
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
        } catch (Exception e) { e.printStackTrace(); }
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
        } catch (Exception e) { e.printStackTrace(); }
    }
}