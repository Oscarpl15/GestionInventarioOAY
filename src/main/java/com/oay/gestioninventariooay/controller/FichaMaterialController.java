package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.exception.ValidacionException;
import com.oay.gestioninventariooay.model.Material;
import com.oay.gestioninventariooay.service.InventarioService;
import com.oay.gestioninventariooay.util.AlertaUI;
import com.oay.gestioninventariooay.util.UtilidadesUI;
import com.oay.gestioninventariooay.util.Validador;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FichaMaterialController {

    @FXML private Label lblTitulo, lblModo;
    @FXML private ComboBox<String> cmbCategoria, cmbTipoMaterial;
    @FXML private TextField txtEmpresa, txtPrecio;
    @FXML private GridPane gridMedidas;
    @FXML private VBox panelStock;
    @FXML private HBox boxControlesStock;
    @FXML private Button btnModificar, btnCancelarEdicion, btnEliminar, btnCerrar;

    private Material material;
    private boolean modoEdicion = false;
    private InventarioService service = new InventarioService();
    private MaterialesController padreController;

    private List<TextField> camposDinamicos = new ArrayList<>();
    private TextField txtCantidadPrincipal = new TextField();

    @FXML
    public void initialize() {
        cmbCategoria.getItems().addAll("Redonda", "Cuadrada", "Plancha");
        cmbTipoMaterial.getItems().addAll("Aluminio", "Acero", "Acero inoxidable", "Duraluminio", "Cobre", "Bronce", "Latón", "PVC", "Teflón");

        // Magia: Si en modo edición cambias de formato, reestructura los campos en tiempo real
        cmbCategoria.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (modoEdicion && newVal != null && !newVal.equals(oldVal)) {
                material.setCategoria(newVal);
                cargarMedidasEspecificas();
                cargarControlesStock();
                bloquearCampos(false);
                UtilidadesUI.reajustarVentana(btnModificar);
            }
        });
    }

    public void initData(Material m, MaterialesController padre) {
        this.material = m;
        this.padreController = padre;

        lblTitulo.setText("Ficha: " + m.getTipoMaterial());
        cmbCategoria.setValue(m.getCategoria());
        cmbTipoMaterial.setValue(m.getTipoMaterial());
        txtEmpresa.setText(m.getEmpresaCompra() != null ? m.getEmpresaCompra() : "");
        txtPrecio.setText(m.getPrecio() != null ? String.valueOf(m.getPrecio()) : "");

        cargarMedidasEspecificas();
        cargarControlesStock();
        bloquearCampos(true);
    }

    private void cargarMedidasEspecificas() {
        gridMedidas.getChildren().clear();
        camposDinamicos.clear();
        int row = 0;
        String cat = material.getCategoria();

        if (cat.equals("Redonda")) {
            addDinamico(gridMedidas, "Diámetro (mm):", String.valueOf(material.getDiametro() != null ? material.getDiametro() : ""), row++);
        } else if (cat.equals("Cuadrada")) {
            addDinamico(gridMedidas, "Lado 1 (mm):", String.valueOf(material.getLado() != null ? material.getLado() : ""), row++);
            if (material.getLado2() != null || modoEdicion) addDinamico(gridMedidas, "Lado 2 (mm):", String.valueOf(material.getLado2() != null ? material.getLado2() : ""), row++);
        } else if (cat.equals("Plancha")) {
            addDinamico(gridMedidas, "Largo (mm):", String.valueOf(material.getLargo() != null ? material.getLargo() : ""), row++);
            addDinamico(gridMedidas, "Ancho (mm):", String.valueOf(material.getAncho() != null ? material.getAncho() : ""), row++);
            addDinamico(gridMedidas, "Espesor (mm):", String.valueOf(material.getEspesor() != null ? material.getEspesor() : ""), row++);
        }
    }

    private void addDinamico(GridPane grid, String label, String valor, int row) {
        Label lbl = new Label(label);
        TextField txt = new TextField(valor != null && !valor.equals("null") ? valor : "");
        camposDinamicos.add(txt);
        grid.add(lbl, 0, row);
        grid.add(txt, 1, row);
    }

    private void cargarControlesStock() {
        boxControlesStock.getChildren().clear();

        if (material.getCategoria().equals("Plancha")) {
            txtCantidadPrincipal.setText(String.valueOf(material.getUnidades() != null ? material.getUnidades() : 0));
            txtCantidadPrincipal.setPrefWidth(80);

            Button btnMenos = new Button("-1");
            Button btnMas = new Button("+1");

            btnMenos.setOnAction(e -> actualizarStockUnidades(-1));
            btnMas.setOnAction(e -> actualizarStockUnidades(1));

            boxControlesStock.getChildren().addAll(btnMenos, txtCantidadPrincipal, new Label("Unidades"), btnMas);
        } else {
            txtCantidadPrincipal.setText(String.valueOf(material.getLongitud() != null ? material.getLongitud() : 0.0));
            txtCantidadPrincipal.setPrefWidth(100);

            TextField txtAjuste = new TextField();
            txtAjuste.setPromptText("Ej: 500");
            txtAjuste.setPrefWidth(80);

            Button btnRestar = new Button("Restar");
            Button btnSumar = new Button("Sumar");

            btnRestar.setOnAction(e -> actualizarStockLongitud(txtAjuste, false));
            btnSumar.setOnAction(e -> actualizarStockLongitud(txtAjuste, true));

            boxControlesStock.getChildren().addAll(
                    new Label("Total:"), txtCantidadPrincipal, new Label("mm"),
                    new Separator(javafx.geometry.Orientation.VERTICAL),
                    txtAjuste, new Label("mm"), btnRestar, btnSumar
            );
        }
    }

    private void actualizarStockUnidades(int cantidad) {
        if (!modoEdicion) {
            AlertaUI.mostrarError("Modo de solo lectura", "Debes pulsar 'Modificar Datos' antes de poder ajustar el stock.");
            return;
        }
        try {
            int actual = Integer.parseInt(txtCantidadPrincipal.getText());
            int nuevo = actual + cantidad;
            if (nuevo < 0) {
                AlertaUI.mostrarError("Stock insuficiente", "No puedes tener unidades negativas.\nActualmente tienes " + actual + " unidades.");
                return;
            }
            txtCantidadPrincipal.setText(String.valueOf(nuevo));
        } catch (Exception e) {
            AlertaUI.mostrarError("Error en la cantidad", "El valor actual del stock está corrupto o no es un número.");
        }
    }

    private void actualizarStockLongitud(TextField campoAjuste, boolean esSuma) {
        if (!modoEdicion) {
            AlertaUI.mostrarError("Modo de solo lectura", "Debes pulsar 'Modificar Datos' antes de poder ajustar el stock.");
            return;
        }
        try {
            double ajuste = Validador.parsearAjusteStock(campoAjuste.getText());
            double actual = Double.parseDouble(txtCantidadPrincipal.getText().replace(",", "."));
            if (!esSuma) Validador.comprobarResta(actual, ajuste, "mm");

            double nuevo = esSuma ? (actual + ajuste) : (actual - ajuste);
            txtCantidadPrincipal.setText(String.valueOf(nuevo));
            campoAjuste.clear();

        } catch (ValidacionException ve) {
            AlertaUI.mostrarError("Ajuste Inválido", ve.getMessage());
        } catch (Exception e) {
            AlertaUI.mostrarError("Error", "Revisa que la cantidad principal no contenga letras.");
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
        alternarEstiloBloqueo(cmbCategoria, bloquear);

        cmbTipoMaterial.setDisable(bloquear);
        alternarEstiloBloqueo(cmbTipoMaterial, bloquear);

        txtEmpresa.setEditable(!bloquear);
        txtPrecio.setEditable(!bloquear);
        txtCantidadPrincipal.setEditable(!bloquear);

        for (TextField txt : camposDinamicos) {
            txt.setEditable(!bloquear);
            alternarEstiloBloqueo(txt, bloquear);
        }

        alternarEstiloBloqueo(txtEmpresa, bloquear);
        alternarEstiloBloqueo(txtPrecio, bloquear);

        panelStock.setVisible(!bloquear);
        panelStock.setManaged(!bloquear);
    }

    @FXML
    private void toggleEdicion() {
        if (!modoEdicion) {
            modoEdicion = true;
            UtilidadesUI.aplicarModoEdicion(btnModificar, lblModo);

            // Intercambiamos botones
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
        // Volvemos a pedirle los datos originales a la base de datos para borrar lo que haya escrito
        initData(service.obtenerMaterialPorId(material.getId()), padreController);

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
            // Validación ESTRICTA
            Validador.requerirPresente(cmbCategoria.getValue(), "Formato");
            Validador.requerirPresente(cmbTipoMaterial.getValue(), "Tipo de Material");

            material.setCategoria(cmbCategoria.getValue());
            material.setTipoMaterial(cmbTipoMaterial.getValue());
            material.setEmpresaCompra(txtEmpresa.getText().trim());
            material.setPrecio(Validador.parsearDoubleOpcional(txtPrecio.getText(), "Precio"));

            if (material.getCategoria().equals("Plancha")) {
                material.setUnidades(Validador.parsearEnteroPositivo(txtCantidadPrincipal.getText(), "Unidades (Stock)"));
                if (camposDinamicos.size() >= 3) {
                    material.setLargo(Validador.parsearDoublePositivo(camposDinamicos.get(0).getText(), "Largo"));
                    material.setAncho(Validador.parsearDoublePositivo(camposDinamicos.get(1).getText(), "Ancho"));
                    material.setEspesor(Validador.parsearDoublePositivo(camposDinamicos.get(2).getText(), "Espesor"));
                }
            } else {
                material.setLongitud(Validador.parsearDoublePositivo(txtCantidadPrincipal.getText(), "Longitud Total (Stock)"));
                if (material.getCategoria().equals("Redonda")) {
                    if (!camposDinamicos.isEmpty()) {
                        material.setDiametro(Validador.parsearDoublePositivo(camposDinamicos.get(0).getText(), "Diámetro"));
                    }
                } else if (material.getCategoria().equals("Cuadrada")) {
                    if (!camposDinamicos.isEmpty()) {
                        material.setLado(Validador.parsearDoublePositivo(camposDinamicos.get(0).getText(), "Lado 1"));
                        // Opcionalidad controlada: Si dejas el Lado 2 vacío, es un cuadrado perfecto
                        if (camposDinamicos.size() > 1 && !camposDinamicos.get(1).getText().isEmpty()) {
                            material.setLado2(Validador.parsearDoublePositivo(camposDinamicos.get(1).getText(), "Lado 2"));
                        } else {
                            material.setLado2(null);
                        }
                    }
                }
            }

            service.guardarMaterial(material);
            padreController.cargarDatos();

            // Volver a modo lectura SIN CERRAR la ventana
            modoEdicion = false;
            UtilidadesUI.aplicarModoLectura(btnModificar, lblModo);

            btnCancelarEdicion.setVisible(false);
            btnCancelarEdicion.setManaged(false);
            btnCerrar.setVisible(true);
            btnCerrar.setManaged(true);

            bloquearCampos(true);
            UtilidadesUI.reajustarVentana(btnModificar);

            AlertaUI.mostrarInfo("Actualizado", "Los datos del material se han guardado correctamente.");

        } catch (ValidacionException ve) {
            AlertaUI.mostrarError("Faltan Datos Obligatorios", ve.getMessage());
        } catch (Exception e) {
            AlertaUI.mostrarErrorCritico("Error de Base de Datos", "No se pudo actualizar. Detalles: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar(ActionEvent event) {
        boolean confirmar = AlertaUI.pedirConfirmacion("Eliminar Material", "¿Estás seguro de que deseas eliminar este material?\nEsta acción borrará el elemento permanentemente.");
        if (confirmar) {
            try {
                service.eliminarMaterial(material);
                padreController.cargarDatos();
                cerrar(event);
            } catch (Exception e) {
                AlertaUI.mostrarErrorCritico("Error", "No se pudo eliminar el material.");
            }
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}