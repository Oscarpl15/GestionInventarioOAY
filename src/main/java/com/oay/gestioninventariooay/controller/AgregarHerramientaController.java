package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.exception.ValidacionException;
import com.oay.gestioninventariooay.model.Herramienta;
import com.oay.gestioninventariooay.service.InventarioService;
import com.oay.gestioninventariooay.util.AlertaUI;
import com.oay.gestioninventariooay.util.Validador;
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
            // 1. Datos obligatorios generales
            Validador.requerirPresente(cmbTipo.getValue(), "Tipo de Herramienta");
            Validador.requerirPresente(cmbMaterialMecanizar.getValue(), "Material a Mecanizar");

            Herramienta h = new Herramienta();
            h.setCategoria(cmbTipo.getValue());
            h.setMaterialMecanizar(cmbMaterialMecanizar.getValue());
            h.setCantidad(Validador.parsearEnteroPositivo(txtCantidad.getText(), "Cantidad Inicial"));

            // 2. OPCIONALES (Solo estos 3)
            h.setReferencia(txtReferencia.getText().trim());
            h.setEmpresaCompra(txtEmpresa.getText().trim());
            h.setPrecio(Validador.parsearDoubleOpcional(txtPrecio.getText(), "Precio"));

            // 3. OBLIGATORIOS ESPECÍFICOS SEGÚN CATEGORÍA
            String tipo = h.getCategoria();

            if (tipo.equals("Torno")) {
                Validador.requerirPresente(cmbTipoMecanizadoTorno.getValue(), "Tipo Mecanizado");
                h.setTipoMecanizado(cmbTipoMecanizadoTorno.getValue());

                Validador.requerirPresente(txtPortaherramientas.getText(), "Portaherramientas");
                h.setPortaherramientas(txtPortaherramientas.getText().trim());

                String tm = h.getTipoMecanizado();
                if(tm.equals("Cilindrado exterior") || tm.equals("Cilindrado interior")) {
                    h.setRadioPunta(Validador.parsearDoublePositivo(txtRadioPunta.getText(), "Radio Punta"));
                    h.setAnguloCorte(Validador.parsearDoublePositivo(txtAnguloCorte.getText(), "Ángulo Corte"));
                    if(tm.equals("Cilindrado interior")) h.setDiametroMinimo(Validador.parsearDoublePositivo(txtDiametroMinimo.getText(), "Diámetro Mínimo"));
                } else if(tm.equals("Roscado exterior") || tm.equals("Roscado interior")) {
                    h.setAnguloPunta(Validador.parsearDoublePositivo(txtAnguloPunta.getText(), "Ángulo Punta"));
                    h.setPasoMinimo(Validador.parsearDoublePositivo(txtPasoMinimo.getText(), "Paso Mínimo"));
                    h.setPasoMaximo(Validador.parsearDoublePositivo(txtPasoMaximo.getText(), "Paso Máximo"));
                } else if(tm.contains("Ranurado")) {
                    h.setAncho(Validador.parsearDoublePositivo(txtAncho.getText(), "Ancho"));
                    h.setProfundidadMaxima(Validador.parsearDoublePositivo(txtProfundidadMaxima.getText(), "Profundidad Máx"));
                    if(tm.equals("Ranurado interior")) h.setDiametroMinimo(Validador.parsearDoublePositivo(txtDiametroMinimo.getText(), "Diámetro Mínimo"));
                } else if (tm.equals("Broca torno")) {
                    h.setDiametro(Validador.parsearDoublePositivo(txtDiametro.getText(), "Diámetro"));
                    h.setProfundidadMaxima(Validador.parsearDoublePositivo(txtProfundidadMaxima.getText(), "Profundidad Máx"));
                }

            } else if (tipo.equals("Fresa")) {
                Validador.requerirPresente(cmbNumLabios.getValue(), "Num. Labios");
                h.setNumLabios(Validador.parsearEnteroPositivo(cmbNumLabios.getValue().toString(), "Num. Labios"));

                Validador.requerirPresente(cmbTipoFresa.getValue(), "Tipo de Fresa");
                h.setTipoFresa(cmbTipoFresa.getValue());

                h.setDiametro(Validador.parsearDoublePositivo(txtDiametro.getText(), "Diámetro"));
                h.setLongitudCorte(Validador.parsearDoublePositivo(txtLongitudCorte.getText(), "Longitud Corte"));

                if (h.getTipoFresa().equals("Plato de cuchillas")) {
                    Validador.requerirPresente(txtReferenciaPlaquita.getText(), "Referencia Plaquita");
                    h.setReferenciaPlaquita(txtReferenciaPlaquita.getText().trim());
                    h.setRadioPunta(Validador.parsearDoublePositivo(txtRadioPunta.getText(), "Radio Punta"));
                }

            } else if (tipo.equals("Broca")) {
                h.setDiametro(Validador.parsearDoublePositivo(txtDiametro.getText(), "Diámetro"));
                h.setLongitudCorte(Validador.parsearDoublePositivo(txtLongitudCorte.getText(), "Longitud Corte"));

            } else if (tipo.equals("Macho")) {
                Validador.requerirPresente(cmbTipoRosca.getValue(), "Tipo Rosca");
                Validador.requerirPresente(cmbMedidaRosca.getValue(), "Medida Rosca");
                Validador.requerirPresente(cmbTipoAgujero.getValue(), "Tipo Agujero");

                h.setTipoRosca(cmbTipoRosca.getValue());
                h.setMedidaRosca(cmbMedidaRosca.getValue());
                h.setTipoAgujero(cmbTipoAgujero.getValue());
            }

            service.guardarHerramienta(h);
            AlertaUI.mostrarInfo("Éxito", "Herramienta guardada correctamente.");
            cancelar(event);

        } catch (ValidacionException ve) {
            AlertaUI.mostrarError("Faltan Datos Obligatorios", ve.getMessage());
        } catch (Exception e) {
            AlertaUI.mostrarErrorCritico("Error de Base de Datos", "No se pudo guardar la herramienta. Detalles: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}