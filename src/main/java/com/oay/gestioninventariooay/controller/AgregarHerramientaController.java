package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.model.Herramienta;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AgregarHerramientaController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private ComboBox<String> cmbMaterialMecanizar;
    @FXML private TextField txtReferencia, txtCantidad, txtEmpresa, txtPrecio;
    @FXML private VBox panelIzquierdo, panelDerecho;

    // Componentes Dinámicos Izquierda
    private ComboBox<String> cmbTipoMecanizadoTorno = new ComboBox<>();
    private TextField txtPortaherramientas = new TextField();

    private ComboBox<Integer> cmbNumLabios = new ComboBox<>();
    private ComboBox<String> cmbTipoFresa = new ComboBox<>();
    private TextField txtDiametro = new TextField();
    private TextField txtLongitudCorte = new TextField();

    private ComboBox<String> cmbTipoRosca = new ComboBox<>();
    private ComboBox<String> cmbMedidaRosca = new ComboBox<>();
    private ComboBox<String> cmbTipoAgujero = new ComboBox<>();

    // Componentes Dinámicos Derecha
    private TextField txtRadioPunta = new TextField();
    private TextField txtAnguloCorte = new TextField();
    private TextField txtDiametroMinimo = new TextField();
    private TextField txtAnguloPunta = new TextField();
    private TextField txtPasoMinimo = new TextField();
    private TextField txtPasoMaximo = new TextField();
    private TextField txtAncho = new TextField();
    private TextField txtProfundidadMaxima = new TextField();
    private TextField txtReferenciaPlaquita = new TextField();

    private InventarioService service = new InventarioService();

    @FXML
    public void initialize() {
        panelDerecho.setVisible(false); // Ocultar hasta que se requiera

        // Opciones Fijas
        cmbTipoMecanizadoTorno.getItems().addAll("Cilindrado exterior", "Cilindrado interior", "Roscado exterior", "Roscado interior", "Ranurado exterior", "Ranurado interior", "Ranurado frontal", "Broca torno");
        cmbNumLabios.getItems().addAll(1, 2, 3, 4);
        cmbTipoFresa.getItems().addAll("Plana", "Bola", "Grabado", "Plato de cuchillas");
        cmbTipoRosca.getItems().addAll("Métrica", "Withworth", "Gas");
        cmbTipoAgujero.getItems().addAll("Ciego", "Pasante");

        // Listeners Principales
        cmbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            construirPanelIzquierdo(newVal);
        });

        // Listeners Secundarios (Despliegan el panel derecho)
        cmbTipoMecanizadoTorno.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> construirPanelDerechoTorno(n));
        cmbTipoFresa.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> construirPanelDerechoFresa(n));
        cmbTipoRosca.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> actualizarMedidasMacho(n));
    }

    private void construirPanelIzquierdo(String tipo) {
        panelIzquierdo.getChildren().clear();
        panelDerecho.getChildren().clear();
        panelDerecho.setVisible(false);
        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        int row = 0;

        if (tipo.equals("Torno")) {
            addFila(grid, "Tipo Mecanizado:", cmbTipoMecanizadoTorno, row++);
            addFila(grid, "Portaherramientas:", txtPortaherramientas, row++);
        } else if (tipo.equals("Fresa")) {
            addFila(grid, "Num. Labios:", cmbNumLabios, row++);
            addFila(grid, "Tipo Fresa:", cmbTipoFresa, row++);
            addFila(grid, "Diámetro (mm):", txtDiametro, row++);
            addFila(grid, "Long. Corte (mm):", txtLongitudCorte, row++);
        } else if (tipo.equals("Broca")) {
            addFila(grid, "Diámetro (mm):", txtDiametro, row++);
            addFila(grid, "Long. Corte (mm):", txtLongitudCorte, row++);
        } else if (tipo.equals("Macho")) {
            addFila(grid, "Tipo Rosca:", cmbTipoRosca, row++);
            addFila(grid, "Medida:", cmbMedidaRosca, row++);
            addFila(grid, "Tipo Agujero:", cmbTipoAgujero, row++);
        }
        panelIzquierdo.getChildren().add(grid);
    }

    private void construirPanelDerechoTorno(String tipoMecanizado) {
        if(tipoMecanizado == null) return;
        panelDerecho.getChildren().clear();
        panelDerecho.setVisible(true);
        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        int row = 0;

        panelDerecho.getChildren().add(new Label("Atributos Específicos:"));

        switch (tipoMecanizado) {
            case "Cilindrado exterior":
                addFila(grid, "Radio Punta:", txtRadioPunta, row++);
                addFila(grid, "Ángulo Corte:", txtAnguloCorte, row++);
                break;
            case "Cilindrado interior":
                addFila(grid, "Radio Punta:", txtRadioPunta, row++);
                addFila(grid, "Ángulo Corte:", txtAnguloCorte, row++);
                addFila(grid, "Diámetro Mínimo:", txtDiametroMinimo, row++);
                break;
            case "Roscado exterior":
            case "Roscado interior":
                addFila(grid, "Ángulo Punta:", txtAnguloPunta, row++);
                addFila(grid, "Paso Mínimo:", txtPasoMinimo, row++);
                addFila(grid, "Paso Máximo:", txtPasoMaximo, row++);
                break;
            case "Ranurado exterior":
            case "Ranurado frontal":
                addFila(grid, "Ancho:", txtAncho, row++);
                addFila(grid, "Profundidad Máx:", txtProfundidadMaxima, row++);
                break;
            case "Ranurado interior":
                addFila(grid, "Ancho:", txtAncho, row++);
                addFila(grid, "Profundidad Máx:", txtProfundidadMaxima, row++);
                addFila(grid, "Diámetro Mínimo:", txtDiametroMinimo, row++);
                break;
            case "Broca torno":
                addFila(grid, "Diámetro:", txtDiametro, row++);
                addFila(grid, "Profundidad Máx:", txtProfundidadMaxima, row++);
                break;
        }
        panelDerecho.getChildren().add(grid);
    }

    private void construirPanelDerechoFresa(String tipoFresa) {
        panelDerecho.getChildren().clear();
        if ("Plato de cuchillas".equals(tipoFresa)) {
            panelDerecho.setVisible(true);
            GridPane grid = new GridPane();
            grid.setVgap(10); grid.setHgap(10);

            panelDerecho.getChildren().add(new Label("Datos de Plaquita:"));
            addFila(grid, "Ref. Plaquita:", txtReferenciaPlaquita, 0);
            addFila(grid, "Radio Punta:", txtRadioPunta, 1);
            panelDerecho.getChildren().add(grid);
        } else {
            panelDerecho.setVisible(false);
        }
    }

    private void actualizarMedidasMacho(String tipoRosca) {
        cmbMedidaRosca.getItems().clear();
        if ("Métrica".equals(tipoRosca)) {
            cmbMedidaRosca.getItems().addAll("M3", "M4", "M5", "M6", "M8", "M10", "M12", "M14", "M16", "M20");
        } else if ("Withworth".equals(tipoRosca)) {
            cmbMedidaRosca.getItems().addAll("1/8\"", "1/4\"", "3/8\"", "1/2\"", "5/8\"", "3/4\"", "1\"");
        } else if ("Gas".equals(tipoRosca)) {
            cmbMedidaRosca.getItems().addAll("G1/8", "G1/4", "G3/8", "G1/2", "G3/4", "G1");
        }
    }

    private void addFila(GridPane grid, String label, Node input, int row) {
        grid.add(new Label(label), 0, row);
        grid.add(input, 1, row);
    }

    @FXML
    private void guardar(ActionEvent event) {
        try {
            Herramienta h = new Herramienta();
            h.setCategoria(cmbTipo.getValue());
            h.setMaterialMecanizar(cmbMaterialMecanizar.getValue());
            h.setCantidad(Integer.parseInt(txtCantidad.getText()));

            // Opcionales
            if(!txtReferencia.getText().isEmpty()) h.setReferencia(txtReferencia.getText());
            if(!txtEmpresa.getText().isEmpty()) h.setEmpresaCompra(txtEmpresa.getText());
            if(!txtPrecio.getText().isEmpty()) h.setPrecio(Double.parseDouble(txtPrecio.getText()));

            // Asignación rápida de atributos según tipo (Aquí iría la lógica de recolección de los textfields, como if(h.getCategoria().equals("Torno") && cmbTipoMecanizadoTorno.getValue() != null) h.setTipoMecanizado(cmbTipoMecanizadoTorno.getValue()); ...etc. )

            service.guardarHerramienta(h);
            cancelar(event);
        } catch (Exception e) {
            System.err.println("Error al guardar herramienta: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}