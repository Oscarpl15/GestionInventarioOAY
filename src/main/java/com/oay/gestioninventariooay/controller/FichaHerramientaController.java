package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.exception.ValidacionException;
import com.oay.gestioninventariooay.model.Herramienta;
import com.oay.gestioninventariooay.service.InventarioService;
import com.oay.gestioninventariooay.util.AlertaUI;
import com.oay.gestioninventariooay.util.UtilidadesUI;
import com.oay.gestioninventariooay.util.Validador;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FichaHerramientaController {

    @FXML private Label lblTitulo, lblModo;
    @FXML private ComboBox<String> cmbCategoria, cmbOperacion, cmbMaterial;
    @FXML private TextField txtReferencia, txtEmpresa, txtPrecio;
    @FXML private GridPane gridEspecifico;
    @FXML private VBox panelStock;
    @FXML private HBox boxControlesStock;
    @FXML private Button btnModificar, btnCancelarEdicion, btnCerrar;

    private Herramienta h;
    private boolean modoEdicion = false;
    private InventarioService service = new InventarioService();
    private HerramientasController padreController;
    private TextField txtCantidadPrincipal = new TextField();

    private Map<String, TextField> inputsTexto = new HashMap<>();
    private Map<String, ComboBox<String>> inputsCombo = new HashMap<>();

    @FXML
    public void initialize() {
        cmbCategoria.getItems().addAll("Torno", "Fresa", "Broca", "Macho");

        cmbCategoria.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (modoEdicion && newVal != null && !newVal.equals(oldVal)) {
                h.setCategoria(newVal);
                h.setTipoMecanizado(null); h.setTipoFresa(null); h.setTipoRosca(null);
                actualizarOpcionesOperacion(newVal);
                cargarMedidasEspecificas();
                bloquearCampos(false);
                UtilidadesUI.reajustarVentana(btnModificar);
            }
        });

        cmbOperacion.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (modoEdicion && newVal != null && !newVal.equals(oldVal)) {
                if (h.getCategoria().equals("Torno")) h.setTipoMecanizado(newVal);
                else if (h.getCategoria().equals("Fresa")) h.setTipoFresa(newVal);
                else if (h.getCategoria().equals("Macho")) h.setTipoRosca(newVal);
                cargarMedidasEspecificas();
                bloquearCampos(false);
                UtilidadesUI.reajustarVentana(btnModificar);
            }
        });
    }

    public void initData(Herramienta herramienta, HerramientasController padre) {
        this.h = herramienta;
        this.padreController = padre;

        lblTitulo.setText("Ficha: " + h.getCategoria());
        cmbCategoria.setValue(h.getCategoria());

        actualizarOpcionesOperacion(h.getCategoria());

        if (h.getCategoria().equals("Torno")) cmbOperacion.setValue(h.getTipoMecanizado());
        else if (h.getCategoria().equals("Fresa")) cmbOperacion.setValue(h.getTipoFresa());
        else if (h.getCategoria().equals("Macho")) cmbOperacion.setValue(h.getTipoRosca());

        cmbMaterial.getSelectionModel().select(h.getMaterialMecanizar());
        txtReferencia.setText(h.getReferencia() != null ? h.getReferencia() : "");
        txtEmpresa.setText(h.getEmpresaCompra() != null ? h.getEmpresaCompra() : "");
        txtPrecio.setText(h.getPrecio() != null ? String.valueOf(h.getPrecio()) : "");

        cargarControlesStock();
        cargarMedidasEspecificas();
        bloquearCampos(true);
    }

    private void actualizarOpcionesOperacion(String categoria) {
        cmbOperacion.getItems().clear();
        if ("Torno".equals(categoria)) {
            cmbOperacion.getItems().addAll("Cilindrado exterior", "Cilindrado interior", "Roscado exterior", "Roscado interior", "Ranurado exterior", "Ranurado interior", "Ranurado frontal", "Broca torno");
        } else if ("Fresa".equals(categoria)) {
            cmbOperacion.getItems().addAll("Plana", "Bola", "Grabado", "Plato de cuchillas");
        } else if ("Macho".equals(categoria)) {
            cmbOperacion.getItems().addAll("Métrica", "Withworth", "Gas");
        }
    }

    private void cargarMedidasEspecificas() {
        gridEspecifico.getChildren().clear();
        inputsTexto.clear();
        inputsCombo.clear();
        int row = 0;
        String cat = h.getCategoria();

        if (cat.equals("Torno")) {
            addTxt("portaherramientas", "Portaherramientas:", h.getPortaherramientas(), row++);
            String tipoMec = h.getTipoMecanizado();
            if (tipoMec != null) {
                switch(tipoMec) {
                    case "Cilindrado exterior":
                    case "Cilindrado interior":
                        addTxt("radioPunta", "Radio Punta:", h.getRadioPunta(), row++);
                        addTxt("anguloCorte", "Ángulo Corte:", h.getAnguloCorte(), row++);
                        if (tipoMec.contains("interior")) addTxt("diametroMinimo", "Diámetro Mínimo:", h.getDiametroMinimo(), row++);
                        break;
                    case "Roscado exterior":
                    case "Roscado interior":
                        addTxt("anguloPunta", "Ángulo Punta:", h.getAnguloPunta(), row++);
                        addTxt("pasoMinimo", "Paso Mínimo:", h.getPasoMinimo(), row++);
                        addTxt("pasoMaximo", "Paso Máximo:", h.getPasoMaximo(), row++);
                        break;
                    case "Ranurado exterior":
                    case "Ranurado frontal":
                    case "Ranurado interior":
                        addTxt("ancho", "Ancho:", h.getAncho(), row++);
                        addTxt("profundidadMaxima", "Profundidad Máx:", h.getProfundidadMaxima(), row++);
                        if (tipoMec.contains("interior")) addTxt("diametroMinimo", "Diámetro Mínimo:", h.getDiametroMinimo(), row++);
                        break;
                    case "Broca torno":
                        addTxt("diametro", "Diámetro:", h.getDiametro(), row++);
                        addTxt("profundidadMaxima", "Profundidad Máx:", h.getProfundidadMaxima(), row++);
                        break;
                }
            }
        } else if (cat.equals("Fresa")) {
            addTxt("numLabios", "Num. Labios:", h.getNumLabios(), row++);
            addTxt("diametro", "Diámetro (mm):", h.getDiametro(), row++);
            addTxt("longitudCorte", "Long. Corte (mm):", h.getLongitudCorte(), row++);
            if ("Plato de cuchillas".equals(h.getTipoFresa())) {
                addTxt("referenciaPlaquita", "Ref. Plaquita:", h.getReferenciaPlaquita(), row++);
                addTxt("radioPunta", "Radio Punta:", h.getRadioPunta(), row++);
            }
        } else if (cat.equals("Broca")) {
            addTxt("diametro", "Diámetro (mm):", h.getDiametro(), row++);
            addTxt("longitudCorte", "Long. Corte (mm):", h.getLongitudCorte(), row++);
        } else if (cat.equals("Macho")) {
            String[] opcionesMedida = new String[0];
            if("Métrica".equals(h.getTipoRosca())) opcionesMedida = new String[]{"M3", "M4", "M5", "M6", "M8", "M10", "M12", "M14", "M16", "M20"};
            else if("Withworth".equals(h.getTipoRosca())) opcionesMedida = new String[]{"1/8\"", "1/4\"", "3/8\"", "1/2\"", "5/8\"", "3/4\"", "1\""};
            else if("Gas".equals(h.getTipoRosca())) opcionesMedida = new String[]{"G1/8", "G1/4", "G3/8", "G1/2", "G3/4", "G1"};

            addCombo("medidaRosca", "Medida:", h.getMedidaRosca(), opcionesMedida, row++);
            addCombo("tipoAgujero", "Tipo Agujero:", h.getTipoAgujero(), new String[]{"Ciego", "Pasante"}, row++);
        }
    }

    private void addTxt(String key, String label, Object valorObj, int row) {
        String valor = (valorObj != null) ? String.valueOf(valorObj) : "";
        Label lbl = new Label(label);
        TextField txt = new TextField(valor);
        inputsTexto.put(key, txt);
        gridEspecifico.add(lbl, 0, row);
        gridEspecifico.add(txt, 1, row);
    }

    private void addCombo(String key, String label, String valorActual, String[] opciones, int row) {
        Label lbl = new Label(label);
        ComboBox<String> cmb = new ComboBox<>(FXCollections.observableArrayList(Arrays.asList(opciones)));
        if (valorActual != null) cmb.getSelectionModel().select(valorActual);
        inputsCombo.put(key, cmb);
        gridEspecifico.add(lbl, 0, row);
        gridEspecifico.add(cmb, 1, row);
    }

    private void cargarControlesStock() {
        boxControlesStock.getChildren().clear();
        txtCantidadPrincipal.setText(String.valueOf(h.getCantidad() != null ? h.getCantidad() : 0));
        txtCantidadPrincipal.setPrefWidth(80);

        Button btnMenos = new Button("-1");
        Button btnMas = new Button("+1");

        btnMenos.setOnAction(e -> actualizarStock(-1));
        btnMas.setOnAction(e -> actualizarStock(1));

        boxControlesStock.getChildren().addAll(btnMenos, txtCantidadPrincipal, new Label("Unidades"), btnMas);
    }

    private void actualizarStock(int ajuste) {
        if (!modoEdicion) {
            AlertaUI.mostrarError("Modo Lectura", "Pulsa 'Modificar Datos' para ajustar el stock.");
            return;
        }
        try {
            int actual = Integer.parseInt(txtCantidadPrincipal.getText());
            int nuevo = actual + ajuste;
            if (nuevo < 0) {
                AlertaUI.mostrarError("Stock insuficiente", "No puedes tener unidades negativas.");
                return;
            }
            txtCantidadPrincipal.setText(String.valueOf(nuevo));
        } catch (Exception e) {
            AlertaUI.mostrarError("Error", "Cantidad actual corrupta.");
        }
    }

    private void alternarEstiloBloqueo(Control campo, boolean bloquear) {
        if (bloquear) {
            if (!campo.getStyleClass().contains("campo-bloqueado")) campo.getStyleClass().add("campo-bloqueado");
        } else {
            campo.getStyleClass().remove("campo-bloqueado");
        }
    }

    private void bloquearCampos(boolean bloquear) {
        cmbCategoria.setDisable(bloquear);
        alternarEstiloBloqueo(cmbCategoria, bloquear); // Visualmente siempre luce bloqueada

        cmbOperacion.setDisable(bloquear);
        alternarEstiloBloqueo(cmbOperacion, bloquear);

        cmbMaterial.setDisable(bloquear);
        alternarEstiloBloqueo(cmbMaterial, bloquear);

        txtReferencia.setEditable(!bloquear);
        txtEmpresa.setEditable(!bloquear);
        txtPrecio.setEditable(!bloquear);
        txtCantidadPrincipal.setEditable(!bloquear);

        alternarEstiloBloqueo(txtReferencia, bloquear);
        alternarEstiloBloqueo(txtEmpresa, bloquear);
        alternarEstiloBloqueo(txtPrecio, bloquear);

        for (TextField txt : inputsTexto.values()) {
            txt.setEditable(!bloquear);
            alternarEstiloBloqueo(txt, bloquear);
        }
        for (ComboBox<String> cmb : inputsCombo.values()) {
            cmb.setDisable(bloquear);
            alternarEstiloBloqueo(cmb, bloquear);
        }

        panelStock.setVisible(!bloquear);
        panelStock.setManaged(!bloquear);
    }

    @FXML
    private void toggleEdicion() {
        if (!modoEdicion) {
            modoEdicion = true;
            UtilidadesUI.aplicarModoEdicion(btnModificar, lblModo);

            btnCancelarEdicion.setVisible(true);
            btnCancelarEdicion.setManaged(true);
            btnCerrar.setVisible(false);
            btnCerrar.setManaged(false);

            bloquearCampos(false);
            UtilidadesUI.reajustarVentana(btnModificar);
        } else {
            guardarDatos();
        }
    }

    @FXML
    private void cancelarEdicion() {
        initData(service.obtenerHerramientaPorId(h.getId()), padreController);

        modoEdicion = false;
        UtilidadesUI.aplicarModoLectura(btnModificar, lblModo);

        btnCancelarEdicion.setVisible(false);
        btnCancelarEdicion.setManaged(false);
        btnCerrar.setVisible(true);
        btnCerrar.setManaged(true);

        bloquearCampos(true);
        UtilidadesUI.reajustarVentana(btnModificar);
    }

    private void guardarDatos() {
        try {
            Validador.requerirPresente(cmbCategoria.getValue(), "Categoría");
            Validador.requerirPresente(cmbMaterial.getValue(), "Mat. a Mecanizar");
            if(!"Broca".equals(cmbCategoria.getValue())) Validador.requerirPresente(cmbOperacion.getValue(), "Tipo/Operación");

            h.setCategoria(cmbCategoria.getValue());
            h.setMaterialMecanizar(cmbMaterial.getValue());
            h.setCantidad(Validador.parsearEnteroPositivo(txtCantidadPrincipal.getText(), "Stock"));

            h.setReferencia(txtReferencia.getText().trim()); // Opcional
            h.setEmpresaCompra(txtEmpresa.getText().trim()); // Opcional
            h.setPrecio(Validador.parsearDoubleOpcional(txtPrecio.getText(), "Precio")); // Opcional

            if(h.getCategoria().equals("Torno")) h.setTipoMecanizado(cmbOperacion.getValue());
            else if(h.getCategoria().equals("Fresa")) h.setTipoFresa(cmbOperacion.getValue());
            else if(h.getCategoria().equals("Macho")) h.setTipoRosca(cmbOperacion.getValue());

            // Validación estricta de dinámicos
            if(inputsTexto.containsKey("portaherramientas")) {
                Validador.requerirPresente(inputsTexto.get("portaherramientas").getText(), "Portaherramientas");
                h.setPortaherramientas(inputsTexto.get("portaherramientas").getText().trim());
            }
            if(inputsTexto.containsKey("referenciaPlaquita")) {
                Validador.requerirPresente(inputsTexto.get("referenciaPlaquita").getText(), "Referencia Plaquita");
                h.setReferenciaPlaquita(inputsTexto.get("referenciaPlaquita").getText().trim());
            }

            if(inputsTexto.containsKey("radioPunta")) h.setRadioPunta(Validador.parsearDoublePositivo(inputsTexto.get("radioPunta").getText(), "Radio Punta"));
            if(inputsTexto.containsKey("anguloCorte")) h.setAnguloCorte(Validador.parsearDoublePositivo(inputsTexto.get("anguloCorte").getText(), "Ángulo Corte"));
            if(inputsTexto.containsKey("diametroMinimo")) h.setDiametroMinimo(Validador.parsearDoublePositivo(inputsTexto.get("diametroMinimo").getText(), "Diámetro Mínimo"));
            if(inputsTexto.containsKey("anguloPunta")) h.setAnguloPunta(Validador.parsearDoublePositivo(inputsTexto.get("anguloPunta").getText(), "Ángulo Punta"));
            if(inputsTexto.containsKey("pasoMinimo")) h.setPasoMinimo(Validador.parsearDoublePositivo(inputsTexto.get("pasoMinimo").getText(), "Paso Mínimo"));
            if(inputsTexto.containsKey("pasoMaximo")) h.setPasoMaximo(Validador.parsearDoublePositivo(inputsTexto.get("pasoMaximo").getText(), "Paso Máximo"));
            if(inputsTexto.containsKey("ancho")) h.setAncho(Validador.parsearDoublePositivo(inputsTexto.get("ancho").getText(), "Ancho"));
            if(inputsTexto.containsKey("profundidadMaxima")) h.setProfundidadMaxima(Validador.parsearDoublePositivo(inputsTexto.get("profundidadMaxima").getText(), "Profundidad Máxima"));
            if(inputsTexto.containsKey("diametro")) h.setDiametro(Validador.parsearDoublePositivo(inputsTexto.get("diametro").getText(), "Diámetro"));
            if(inputsTexto.containsKey("longitudCorte")) h.setLongitudCorte(Validador.parsearDoublePositivo(inputsTexto.get("longitudCorte").getText(), "Longitud Corte"));
            if(inputsTexto.containsKey("numLabios")) h.setNumLabios(Validador.parsearEnteroPositivo(inputsTexto.get("numLabios").getText(), "Num. Labios"));

            if(inputsCombo.containsKey("medidaRosca")) {
                Validador.requerirPresente(inputsCombo.get("medidaRosca").getValue(), "Medida Rosca");
                h.setMedidaRosca(inputsCombo.get("medidaRosca").getValue());
            }
            if(inputsCombo.containsKey("tipoAgujero")) {
                Validador.requerirPresente(inputsCombo.get("tipoAgujero").getValue(), "Tipo Agujero");
                h.setTipoAgujero(inputsCombo.get("tipoAgujero").getValue());
            }

            service.guardarHerramienta(h);
            padreController.cargarDatos();

            modoEdicion = false;
            UtilidadesUI.aplicarModoLectura(btnModificar, lblModo);

            btnCancelarEdicion.setVisible(false);
            btnCancelarEdicion.setManaged(false);
            btnCerrar.setVisible(true);
            btnCerrar.setManaged(true);

            bloquearCampos(true);
            UtilidadesUI.reajustarVentana(btnModificar);

            AlertaUI.mostrarInfo("Actualizado", "La herramienta se ha guardado correctamente.");

        } catch (ValidacionException ve) {
            AlertaUI.mostrarError("Faltan Datos Obligatorios", ve.getMessage());
        } catch (Exception e) {
            AlertaUI.mostrarErrorCritico("Error de Base de Datos", "No se pudo actualizar la herramienta. Detalles: " + e.getMessage());
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
