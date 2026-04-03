package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.exception.ValidacionException;
import com.oay.gestioninventariooay.model.Aceite;
import com.oay.gestioninventariooay.service.InventarioService;
import com.oay.gestioninventariooay.util.AlertaUI;
import com.oay.gestioninventariooay.util.Validador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AgregarAceiteController {

    @FXML private ComboBox<String> cmbMaquina;
    @FXML private TextField txtTipoAceite, txtReferencia, txtCapacidad, txtCantidad, txtEmpresa, txtPrecio;

    private InventarioService service = new InventarioService();

    @FXML
    private void guardar(ActionEvent event) {
        try {
            Validador.requerirPresente(cmbMaquina.getValue(), "Máquina Destino");
            Validador.requerirPresente(txtTipoAceite.getText(), "Tipo de Aceite");


            Aceite a = new Aceite();
            a.setMaquinaDestino(cmbMaquina.getValue());
            a.setTipoAceite(txtTipoAceite.getText());
            a.setReferencia(txtReferencia.getText().trim());
            a.setCapacidadLitros(Validador.parsearDoublePositivo(txtCapacidad.getText(), "Capacidad Garrafa"));
            a.setCantidad(Validador.parsearEnteroPositivo(txtCantidad.getText(), "Stock Inicial"));

            a.setEmpresaCompra(txtEmpresa.getText().trim()); // Opcional
            a.setPrecio(Validador.parsearDoubleOpcional(txtPrecio.getText(), "Precio Total")); // Opcional

            service.guardarAceite(a);
            AlertaUI.mostrarInfo("Éxito", "Aceite guardado correctamente.");
            cancelar(event);
        } catch (ValidacionException ve) {
            AlertaUI.mostrarError("Faltan Datos Obligatorios", ve.getMessage());
        } catch (Exception e) {
            AlertaUI.mostrarErrorCritico("Error de Base de Datos", "No se pudo guardar el aceite.");
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}