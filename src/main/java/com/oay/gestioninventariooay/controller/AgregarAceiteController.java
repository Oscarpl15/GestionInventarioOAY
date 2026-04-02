package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.model.Aceite;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AgregarAceiteController {

    @FXML private ComboBox<String> cmbMaquina;
    @FXML private TextField txtTipoAceite, txtUso, txtCapacidad, txtCantidad, txtEmpresa, txtPrecio;

    private InventarioService service = new InventarioService();

    @FXML
    private void guardar(ActionEvent event) {
        try {
            Aceite a = new Aceite();
            a.setMaquinaDestino(cmbMaquina.getValue());
            a.setTipoAceite(txtTipoAceite.getText());
            a.setUsoConcreto(txtUso.getText());
            a.setCapacidadLitros(Double.parseDouble(txtCapacidad.getText()));
            a.setCantidad(Integer.parseInt(txtCantidad.getText()));
            a.setEmpresaCompra(txtEmpresa.getText());
            a.setPrecio(Double.parseDouble(txtPrecio.getText()));

            service.guardarAceite(a);
            cancelar(event);
        } catch (Exception e) {
            System.err.println("Error al guardar aceite: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}