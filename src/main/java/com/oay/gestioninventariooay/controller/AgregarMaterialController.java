package com.oay.gestioninventariooay.controller;

import com.oay.gestioninventariooay.model.Material;
import com.oay.gestioninventariooay.service.InventarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.oay.gestioninventariooay.exception.ValidacionException;
import com.oay.gestioninventariooay.util.AlertaUI;
import com.oay.gestioninventariooay.util.Validador;

public class AgregarMaterialController {

    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtEmpresa, txtPrecio;
    @FXML private VBox contenedorDinamico;

    // Reemplazamos el TextField por ComboBox
    @FXML private ComboBox<String> cmbTipoMaterial;

    // Campos dinámicos
    private TextField txtDiametro = new TextField();
    private TextField txtLado = new TextField();
    private TextField txtLado2 = new TextField(); // Añadido
    private TextField txtLargo = new TextField();
    private TextField txtAncho = new TextField();
    private TextField txtEspesor = new TextField();

    // Cambiamos de TextField fijo a dinámico para poder mostrar Longitud o Unidades
    private TextField txtMedidaPrincipal = new TextField();
    private Label lblMedidaPrincipal = new Label("Longitud Total (mm):");

    private InventarioService service = new InventarioService();

    @FXML
    public void initialize() {
        // Cargar materiales
        cmbTipoMaterial.getItems().addAll("Aluminio", "Acero", "Acero inoxidable", "Duraluminio", "Cobre", "Bronce", "Latón", "PVC", "Teflón");

        cmbCategoria.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            construirFormularioDinamico(newVal);
        });
    }

    private void construirFormularioDinamico(String categoria) {
        contenedorDinamico.getChildren().clear();
        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        int row = 0;

        // Mostrar Longitud o Unidades
        if (categoria.equals("Plancha")) {
            lblMedidaPrincipal.setText("Unidades:");
            txtMedidaPrincipal.setPromptText("Ej: 2");
        } else {
            lblMedidaPrincipal.setText("Longitud Total (mm):");
            txtMedidaPrincipal.setPromptText("Suma total");
        }
        addFila(grid, lblMedidaPrincipal, txtMedidaPrincipal, row++);

        // Campos específicos
        if (categoria.equals("Redonda")) {
            addFila(grid, new Label("Diámetro (mm):"), txtDiametro, row++);
        } else if (categoria.equals("Cuadrada")) {
            addFila(grid, new Label("Lado 1 (mm):"), txtLado, row++);
            addFila(grid, new Label("Lado 2 (mm) - Rectangular:"), txtLado2, row++);
        } else if (categoria.equals("Plancha")) {
            addFila(grid, new Label("Largo (mm):"), txtLargo, row++);
            addFila(grid, new Label("Ancho (mm):"), txtAncho, row++);
            addFila(grid, new Label("Espesor (mm):"), txtEspesor, row++);
        }
        contenedorDinamico.getChildren().add(grid);
    }

    private void addFila(GridPane grid, Label label, Node input, int row) {
        grid.add(label, 0, row);
        grid.add(input, 1, row);
    }

    @FXML
    private void guardar(ActionEvent event) {
        try {
            // 1. Validaciones Generales
            Validador.requerirPresente(cmbCategoria.getValue(), "Formato");
            Validador.requerirPresente(cmbTipoMaterial.getValue(), "Tipo de Material");

            Material m = new Material();
            m.setCategoria(cmbCategoria.getValue());
            m.setTipoMaterial(cmbTipoMaterial.getValue());

            // Textos opcionales
            m.setEmpresaCompra(txtEmpresa.getText().trim());
            m.setPrecio(Validador.parsearDoubleOpcional(txtPrecio.getText(), "Precio"));

            // 2. Validación de Cantidad Principal
            if (m.getCategoria().equals("Plancha")) {
                m.setUnidades(Validador.parsearEnteroPositivo(txtMedidaPrincipal.getText(), "Unidades"));
            } else {
                m.setLongitud(Validador.parsearDoublePositivo(txtMedidaPrincipal.getText(), "Longitud Total"));
            }

            // 3. Validaciones Dinámicas según Categoría
            if (m.getCategoria().equals("Redonda")) {
                m.setDiametro(Validador.parsearDoublePositivo(txtDiametro.getText(), "Diámetro"));
            }
            else if (m.getCategoria().equals("Cuadrada")) {
                m.setLado(Validador.parsearDoublePositivo(txtLado.getText(), "Lado 1"));
                if (!txtLado2.getText().isEmpty()) {
                    m.setLado2(Validador.parsearDoublePositivo(txtLado2.getText(), "Lado 2"));
                }
            }
            else if (m.getCategoria().equals("Plancha")) {
                m.setLargo(Validador.parsearDoublePositivo(txtLargo.getText(), "Largo"));
                m.setAncho(Validador.parsearDoublePositivo(txtAncho.getText(), "Ancho"));
                m.setEspesor(Validador.parsearDoublePositivo(txtEspesor.getText(), "Espesor"));
            }

            // 4. Guardar en Base de Datos
            service.guardarMaterial(m);
            AlertaUI.mostrarInfo("Éxito", "Material guardado correctamente.");
            cancelar(event);

        } catch (ValidacionException ve) {
            // Error de usuario (ha metido letras en un campo numérico o dejado algo vacío)
            AlertaUI.mostrarError("El campo no contiene formato numerico correcto",ve.getMessage());
        } catch (Exception e) {
            // Error crítico (Base de datos desconectada, fallo de Hibernate, etc)
            AlertaUI.mostrarErrorCritico("Error interno al guardar en base de datos", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}