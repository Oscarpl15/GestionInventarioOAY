package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.Launcher;
import com.oay.gestioninventariooay.model.Herramienta;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.beans.property.SimpleIntegerProperty;
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

public class HerramientasController {

    @FXML private TableView<Herramienta> tablaHerramientas;
    @FXML private TableColumn<Herramienta, String> colTipo, colTipoOperacion, colDimension, colReferencia, colMaterialMecanizar;
    @FXML private TableColumn<Herramienta, Integer> colCantidad;

    @FXML private ComboBox<String> cmbFiltroTipo, cmbFiltroOperacion;
    @FXML private TextField txtBuscar;

    private InventarioService service = new InventarioService();
    private ObservableList<Herramienta> listaHerramientas = FXCollections.observableArrayList();
    private FilteredList<Herramienta> filteredData;

    @FXML
    public void initialize() {
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        colReferencia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReferencia() != null ? cellData.getValue().getReferencia() : ""));
        colMaterialMecanizar.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaterialMecanizar()));
        colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());

        colTipoOperacion.setCellValueFactory(cellData -> {
            Herramienta h = cellData.getValue();
            if ("Torno".equals(h.getCategoria()) && h.getTipoMecanizado() != null) return new SimpleStringProperty(h.getTipoMecanizado());
            else if ("Fresa".equals(h.getCategoria()) && h.getTipoFresa() != null) return new SimpleStringProperty(h.getTipoFresa());
            else if ("Macho".equals(h.getCategoria()) && h.getTipoRosca() != null) return new SimpleStringProperty(h.getTipoRosca());
            return new SimpleStringProperty("-");
        });

        colDimension.setCellValueFactory(cellData -> {
            Herramienta h = cellData.getValue();
            if ("Fresa".equals(h.getCategoria()) || "Broca".equals(h.getCategoria())) return new SimpleStringProperty(h.getDiametro() != null ? "Ø " + h.getDiametro() : "-");
            else if ("Macho".equals(h.getCategoria())) return new SimpleStringProperty(h.getMedidaRosca() != null ? h.getMedidaRosca() : "-");
            return new SimpleStringProperty("-");
        });

        filteredData = new FilteredList<>(listaHerramientas, b -> true);
        SortedList<Herramienta> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaHerramientas.comparatorProperty());
        tablaHerramientas.setItems(sortedData);

        cmbFiltroTipo.valueProperty().addListener((obs, o, n) -> {
            actualizarFiltroOperacion(n);
            aplicarFiltros();
        });
        cmbFiltroOperacion.valueProperty().addListener((obs, o, n) -> aplicarFiltros());
        txtBuscar.textProperty().addListener((obs, o, n) -> aplicarFiltros());

        cmbFiltroTipo.getSelectionModel().selectFirst();

        tablaHerramientas.setRowFactory(tv -> {
            TableRow<Herramienta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) abrirFichaHerramienta(row.getItem());
            });
            return row;
        });

        cargarDatos();
    }

    private void actualizarFiltroOperacion(String tipo) {
        cmbFiltroOperacion.getItems().clear();
        if (tipo == null || "Todos".equals(tipo) || "Broca".equals(tipo)) {
            cmbFiltroOperacion.setVisible(false);
            cmbFiltroOperacion.setManaged(false);
        } else {
            cmbFiltroOperacion.setVisible(true);
            cmbFiltroOperacion.setManaged(true);
            cmbFiltroOperacion.getItems().add("Todas");
            if ("Torno".equals(tipo)) cmbFiltroOperacion.getItems().addAll("Cilindrado exterior", "Cilindrado interior", "Roscado exterior", "Roscado interior", "Ranurado exterior", "Ranurado interior", "Ranurado frontal", "Broca torno");
            else if ("Fresa".equals(tipo)) cmbFiltroOperacion.getItems().addAll("Plana", "Bola", "Grabado", "Plato de cuchillas");
            else if ("Macho".equals(tipo)) cmbFiltroOperacion.getItems().addAll("Métrica", "Withworth", "Gas");
            cmbFiltroOperacion.getSelectionModel().selectFirst();
        }
    }

    private void aplicarFiltros() {
        String tipoFiltro = cmbFiltroTipo.getValue();
        String opFiltro = cmbFiltroOperacion.getValue();
        String busqueda = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";

        filteredData.setPredicate(h -> {
            boolean matchTipo = "Todos".equals(tipoFiltro) || tipoFiltro.equals(h.getCategoria());
            boolean matchOp = true;
            if (!"Todos".equals(tipoFiltro) && !"Broca".equals(tipoFiltro) && opFiltro != null && !"Todas".equals(opFiltro)) {
                if ("Torno".equals(tipoFiltro)) matchOp = opFiltro.equals(h.getTipoMecanizado());
                else if ("Fresa".equals(tipoFiltro)) matchOp = opFiltro.equals(h.getTipoFresa());
                else if ("Macho".equals(tipoFiltro)) matchOp = opFiltro.equals(h.getTipoRosca());
            }
            boolean matchBusqueda = busqueda.isEmpty() || (h.getReferencia() != null && h.getReferencia().toLowerCase().contains(busqueda));

            return matchTipo && matchOp && matchBusqueda;
        });
    }


    public void cargarDatos() {
        listaHerramientas.setAll(service.obtenerHerramientas());
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
