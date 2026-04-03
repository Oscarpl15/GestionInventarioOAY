package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.Launcher;
import com.oay.gestioninventariooay.model.Aceite;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

public class AceitesController {

    @FXML private TableView<Aceite> tablaAceites;
    @FXML private TableColumn<Aceite, String> colMaquina, colTipo, colReferencia;
    @FXML private TableColumn<Aceite, Double> colCapacidad;
    @FXML private TableColumn<Aceite, Integer> colCantidad;

    @FXML private ComboBox<String> cmbFiltroMaquina;
    @FXML private TextField txtBuscar;

    private InventarioService service = new InventarioService();
    private ObservableList<Aceite> listaAceites = FXCollections.observableArrayList();
    private FilteredList<Aceite> filteredData;

    @FXML
    public void initialize() {
        colMaquina.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaquinaDestino()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoAceite()));
        colReferencia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReferencia() != null ? cellData.getValue().getReferencia() : ""));
        colCapacidad.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCapacidadLitros()).asObject());
        colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());

        filteredData = new FilteredList<>(listaAceites, b -> true);
        SortedList<Aceite> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaAceites.comparatorProperty());
        tablaAceites.setItems(sortedData);

        cmbFiltroMaquina.valueProperty().addListener((obs, o, n) -> aplicarFiltros());
        txtBuscar.textProperty().addListener((obs, o, n) -> aplicarFiltros());

        cmbFiltroMaquina.getSelectionModel().selectFirst();

        tablaAceites.setRowFactory(tv -> {
            TableRow<Aceite> row = new TableRow<>();
            // (AQUÍ VA TU CÓDIGO DE ELIMINAR QUE HICIMOS ANTES)
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) abrirFichaAceite(row.getItem());
            });
            return row;
        });

        cargarDatos();
    }

    private void aplicarFiltros() {
        String maqFiltro = cmbFiltroMaquina.getValue();
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";

        filteredData.setPredicate(a -> {
            boolean matchMaq = "Todas".equals(maqFiltro) || maqFiltro.equals(a.getMaquinaDestino());
            boolean matchBusqueda = busqueda.isEmpty() || (a.getReferencia() != null && a.getReferencia().toLowerCase().contains(busqueda));
            return matchMaq && matchBusqueda;
        });
    }

    public void cargarDatos() {
        listaAceites.setAll(service.obtenerAceites());
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

    private void eliminarAceiteDesdeTabla(Aceite a) {
        boolean confirmar = com.oay.gestioninventariooay.util.AlertaUI.pedirConfirmacion("Eliminar Aceite", "¿Deseas eliminar definitivamente el aceite '" + a.getTipoAceite() + "'?");
        if (confirmar) {
            try {
                service.eliminarAceite(a);
                cargarDatos();
            } catch (Exception e) {
                com.oay.gestioninventariooay.util.AlertaUI.mostrarErrorCritico("Error", "No se pudo eliminar.");
            }
        }
    }
}
