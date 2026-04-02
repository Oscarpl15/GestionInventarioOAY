package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.exception.ValidacionException;
import com.oay.gestioninventariooay.model.Aceite;
import com.oay.gestioninventariooay.service.InventarioService;
import com.oay.gestioninventariooay.util.AlertaUI;
import com.oay.gestioninventariooay.util.UtilidadesUI;
import com.oay.gestioninventariooay.util.Validador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FichaAceiteController {

    @FXML private Label lblTitulo, lblModo;
    @FXML private ComboBox<String> cmbMaquina;
    @FXML private TextField txtTipoAceite, txtUso, txtCapacidad, txtEmpresa, txtPrecio;
    @FXML private VBox panelStock;
    @FXML private HBox boxControlesStock;
    @FXML private Button btnModificar;

    private Aceite aceite;
    private boolean modoEdicion = false;
    private InventarioService service = new InventarioService();
    private AceitesController padreController;
    private TextField txtCantidadPrincipal = new TextField();

    public void initData(Aceite a, AceitesController padre) {
        this.aceite = a;
        this.padreController = padre;

        lblTitulo.setText("Ficha: " + a.getTipoAceite());
        cmbMaquina.getSelectionModel().select(a.getMaquinaDestino());
        txtTipoAceite.setText(a.getTipoAceite());
        txtUso.setText(a.getUsoConcreto() != null ? a.getUsoConcreto() : "");
        txtCapacidad.setText(String.valueOf(a.getCapacidadLitros()));
        txtEmpresa.setText(a.getEmpresaCompra() != null ? a.getEmpresaCompra() : "");
        txtPrecio.setText(a.getPrecio() != null ? String.valueOf(a.getPrecio()) : "");

        cargarControlesStock();
        bloquearCampos(true);
    }

    private void cargarControlesStock() {
        boxControlesStock.getChildren().clear();
        txtCantidadPrincipal.setText(String.valueOf(aceite.getCantidad() != null ? aceite.getCantidad() : 0));
        txtCantidadPrincipal.setPrefWidth(80);

        Button btnMenos = new Button("-1");
        Button btnMas = new Button("+1");

        btnMenos.setOnAction(e -> actualizarStock(-1));
        btnMas.setOnAction(e -> actualizarStock(1));

        boxControlesStock.getChildren().addAll(btnMenos, txtCantidadPrincipal, new Label("Garrafas"), btnMas);
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
        txtTipoAceite.setEditable(!bloquear);
        txtUso.setEditable(!bloquear);
        txtCapacidad.setEditable(!bloquear);
        txtEmpresa.setEditable(!bloquear);
        txtPrecio.setEditable(!bloquear);
        txtCantidadPrincipal.setEditable(!bloquear);

        alternarEstiloBloqueo(txtTipoAceite, bloquear);
        alternarEstiloBloqueo(txtUso, bloquear);
        alternarEstiloBloqueo(txtCapacidad, bloquear);
        alternarEstiloBloqueo(txtEmpresa, bloquear);
        alternarEstiloBloqueo(txtPrecio, bloquear);

        cmbMaquina.setDisable(bloquear);
        alternarEstiloBloqueo(cmbMaquina, bloquear);

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
            Validador.requerirPresente(cmbMaquina.getValue(), "Máquina Destino");
            Validador.requerirPresente(txtTipoAceite.getText(), "Tipo de Aceite");

            aceite.setMaquinaDestino(cmbMaquina.getValue());
            aceite.setTipoAceite(txtTipoAceite.getText());
            aceite.setUsoConcreto(txtUso.getText().trim());
            aceite.setCapacidadLitros(Validador.parsearDoublePositivo(txtCapacidad.getText(), "Capacidad (Litros)"));
            aceite.setCantidad(Validador.parsearEnteroPositivo(txtCantidadPrincipal.getText(), "Stock"));
            aceite.setEmpresaCompra(txtEmpresa.getText().trim());
            aceite.setPrecio(Validador.parsearDoubleOpcional(txtPrecio.getText(), "Precio"));

            service.guardarAceite(aceite);
            padreController.cargarDatos();

            modoEdicion = false;
            UtilidadesUI.aplicarModoLectura(btnModificar, lblModo); // <--- LÍNEA LIMPIA
            bloquearCampos(true);
            UtilidadesUI.reajustarVentana(btnModificar);

        } catch (ValidacionException ve) {
            AlertaUI.mostrarError("Revisa los datos", ve.getMessage());
        } catch (Exception e) {
            AlertaUI.mostrarErrorCritico("Error de Base de Datos", "No se pudo actualizar el aceite.");
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}