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
    @FXML private TextField txtCategoriaFija, txtOperacionFija, txtReferencia, txtEmpresa, txtPrecio;
    @FXML private ComboBox<String> cmbMaterial;
    @FXML private GridPane gridEspecifico;
    @FXML private VBox panelStock;
    @FXML private HBox boxControlesStock;
    @FXML private Button btnModificar;

    private Herramienta h;
    private boolean modoEdicion = false;
    private InventarioService service = new InventarioService();
    private HerramientasController padreController;
    private TextField txtCantidadPrincipal = new TextField();

    // Mapas para recuperar datos generados dinámicamente
    private Map<String, TextField> inputsTexto = new HashMap<>();
    private Map<String, ComboBox<String>> inputsCombo = new HashMap<>();

    public void initData(Herramienta herramienta, HerramientasController padre) {
        this.h = herramienta;
        this.padreController = padre;

        lblTitulo.setText("Ficha: " + h.getCategoria());
        txtCategoriaFija.setText(h.getCategoria());

        // Bloqueamos la operación principal para no desestructurar la tabla en edición
        if (h.getCategoria().equals("Torno")) txtOperacionFija.setText(h.getTipoMecanizado());
        else if (h.getCategoria().equals("Fresa")) txtOperacionFija.setText(h.getTipoFresa());
        else if (h.getCategoria().equals("Macho")) txtOperacionFija.setText(h.getTipoRosca());
        else txtOperacionFija.setText("-");

        cmbMaterial.getSelectionModel().select(h.getMaterialMecanizar());
        txtReferencia.setText(h.getReferencia() != null ? h.getReferencia() : "");
        txtEmpresa.setText(h.getEmpresaCompra() != null ? h.getEmpresaCompra() : "");
        txtPrecio.setText(h.getPrecio() != null ? String.valueOf(h.getPrecio()) : "");

        cargarControlesStock();
        cargarMedidasEspecificas();
        bloquearCampos(true);
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
            // Lógica de ComboBox dependiente del TipoRosca
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

    private void bloquearCampos(boolean bloquear) {
        txtReferencia.setEditable(!bloquear);
        txtEmpresa.setEditable(!bloquear);
        txtPrecio.setEditable(!bloquear);
        txtCantidadPrincipal.setEditable(!bloquear);

        for (TextField txt : inputsTexto.values()) {
            txt.setEditable(!bloquear);
            alternarEstiloBloqueo(txt, bloquear);
        }
        for (ComboBox<String> cmb : inputsCombo.values()) {
            cmb.setDisable(bloquear);
            alternarEstiloBloqueo(cmb, bloquear);
        }

        alternarEstiloBloqueo(txtCategoriaFija, bloquear);
        alternarEstiloBloqueo(txtOperacionFija, bloquear);
        alternarEstiloBloqueo(txtReferencia, bloquear);
        alternarEstiloBloqueo(txtEmpresa, bloquear);
        alternarEstiloBloqueo(txtPrecio, bloquear);

        cmbMaterial.setDisable(bloquear);
        alternarEstiloBloqueo(cmbMaterial, bloquear);

        panelStock.setVisible(!bloquear);
        panelStock.setManaged(!bloquear);
    }

    private void alternarEstiloBloqueo(Control campo, boolean bloquear) {
        if (bloquear) {
            if (!campo.getStyleClass().contains("campo-bloqueado")) campo.getStyleClass().add("campo-bloqueado");
        } else {
            campo.getStyleClass().remove("campo-bloqueado");
        }
    }

    @FXML
    private void toggleEdicion() {
        if (!modoEdicion) {
            modoEdicion = true;
            UtilidadesUI.aplicarModoEdicion(btnModificar, lblModo);
            bloquearCampos(false);
            UtilidadesUI.reajustarVentana(btnModificar);
        } else {
            guardarDatos();
        }
    }

    private void guardarDatos() {
        try {
            Validador.requerirPresente(cmbMaterial.getValue(), "Mat. a Mecanizar");

            h.setMaterialMecanizar(cmbMaterial.getValue());
            h.setReferencia(txtReferencia.getText().trim());
            h.setEmpresaCompra(txtEmpresa.getText().trim());
            h.setPrecio(Validador.parsearDoubleOpcional(txtPrecio.getText(), "Precio"));
            h.setCantidad(Validador.parsearEnteroPositivo(txtCantidadPrincipal.getText(), "Stock"));

            // RECUPERAR DATOS DINÁMICOS CON VALIDADOR
            if(inputsTexto.containsKey("portaherramientas")) h.setPortaherramientas(inputsTexto.get("portaherramientas").getText());
            if(inputsTexto.containsKey("referenciaPlaquita")) h.setReferenciaPlaquita(inputsTexto.get("referenciaPlaquita").getText());

            if(inputsTexto.containsKey("radioPunta")) h.setRadioPunta(Validador.parsearDoubleOpcional(inputsTexto.get("radioPunta").getText(), "Radio Punta"));
            if(inputsTexto.containsKey("anguloCorte")) h.setAnguloCorte(Validador.parsearDoubleOpcional(inputsTexto.get("anguloCorte").getText(), "Ángulo Corte"));
            if(inputsTexto.containsKey("diametroMinimo")) h.setDiametroMinimo(Validador.parsearDoubleOpcional(inputsTexto.get("diametroMinimo").getText(), "Diámetro Mínimo"));
            if(inputsTexto.containsKey("anguloPunta")) h.setAnguloPunta(Validador.parsearDoubleOpcional(inputsTexto.get("anguloPunta").getText(), "Ángulo Punta"));
            if(inputsTexto.containsKey("pasoMinimo")) h.setPasoMinimo(Validador.parsearDoubleOpcional(inputsTexto.get("pasoMinimo").getText(), "Paso Mínimo"));
            if(inputsTexto.containsKey("pasoMaximo")) h.setPasoMaximo(Validador.parsearDoubleOpcional(inputsTexto.get("pasoMaximo").getText(), "Paso Máximo"));
            if(inputsTexto.containsKey("ancho")) h.setAncho(Validador.parsearDoubleOpcional(inputsTexto.get("ancho").getText(), "Ancho"));
            if(inputsTexto.containsKey("profundidadMaxima")) h.setProfundidadMaxima(Validador.parsearDoubleOpcional(inputsTexto.get("profundidadMaxima").getText(), "Profundidad Máxima"));
            if(inputsTexto.containsKey("diametro")) h.setDiametro(Validador.parsearDoubleOpcional(inputsTexto.get("diametro").getText(), "Diámetro"));
            if(inputsTexto.containsKey("longitudCorte")) h.setLongitudCorte(Validador.parsearDoubleOpcional(inputsTexto.get("longitudCorte").getText(), "Longitud Corte"));
            if(inputsTexto.containsKey("numLabios")) h.setNumLabios(Validador.parsearEnteroOpcional(inputsTexto.get("numLabios").getText(), "Num. Labios"));

            // Recuperar Combos
            if(inputsCombo.containsKey("medidaRosca")) h.setMedidaRosca(inputsCombo.get("medidaRosca").getValue());
            if(inputsCombo.containsKey("tipoAgujero")) h.setTipoAgujero(inputsCombo.get("tipoAgujero").getValue());

            service.guardarHerramienta(h);
            padreController.cargarDatos();

            modoEdicion = false;
            UtilidadesUI.aplicarModoLectura(btnModificar, lblModo); // <--- LÍNEA LIMPIA
            bloquearCampos(true);
            UtilidadesUI.reajustarVentana(btnModificar);

        } catch (ValidacionException ve) {
            AlertaUI.mostrarError("Revisa los datos", ve.getMessage());
        } catch (Exception e) {
            AlertaUI.mostrarErrorCritico("Error de Base de Datos", "No se pudo actualizar la herramienta.");
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
