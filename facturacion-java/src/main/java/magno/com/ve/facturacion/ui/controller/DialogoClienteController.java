package magno.com.ve.facturacion.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import magno.com.ve.facturacion.domain.enums.TipoContribuyente;
import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Contacto;
import magno.com.ve.facturacion.domain.model.Direccion;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.repository.RepositoryFactory;
import magno.com.ve.facturacion.service.ClienteService;
import magno.com.ve.facturacion.util.Validaciones;

public class DialogoClienteController {

    @FXML private TextField txtCedula;
    @FXML private Label lblTipoContribuyente;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefonoPrincipal;
    @FXML private TextField txtEmail;

    private final ClienteService clienteService = new ClienteService();
    private Cliente clienteGuardado;

    @FXML
    public void initialize() {
        if (txtCedula != null) {
            txtCedula.textProperty().addListener((obs, old, nuevo) -> actualizarTipo(nuevo));
        }
    }

    public void precargarCedula(String cedula) {
        if (txtCedula != null) {
            txtCedula.setText(cedula);
        }
        if (txtNombres != null) {
            txtNombres.requestFocus();
        }
    }

    private void actualizarTipo(String cedula) {
        if (lblTipoContribuyente == null) return;
        if (cedula == null || cedula.trim().isEmpty()) {
            lblTipoContribuyente.setText("—");
            lblTipoContribuyente.setStyle("-fx-font-weight: bold; -fx-text-fill: #94A3B8;");
            return;
        }
        TipoContribuyente tipo = Validaciones.getTipoContribuyente(cedula);
        if (tipo != null) {
            lblTipoContribuyente.setText(tipo.getDescripcion());
            lblTipoContribuyente.setStyle("-fx-font-weight: bold; -fx-text-fill: #10B981;");
        } else {
            lblTipoContribuyente.setText("Prefijo inválido");
            lblTipoContribuyente.setStyle("-fx-font-weight: bold; -fx-text-fill: #EF4444;");
        }
    }

    @FXML
    public void handleGuardar() {
        try {
            Cliente cliente = construirCliente();
            clienteService.validarYNormalizar(cliente);
            clienteGuardado = RepositoryFactory.getClienteRepository().guardar(cliente);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cliente Registrado");
            alert.setHeaderText("✅ Cliente registrado correctamente");
            alert.setContentText("Cédula: " + cliente.getCedula() + "\nNombre: " + cliente.getNombreCompleto());
            alert.showAndWait();

            cerrarModal();
        } catch (ValidacionException ex) {
            mostrarError("Errores de Validación", ex.getMessage());
        } catch (IllegalStateException ex) {
            mostrarError("Cliente Duplicado", ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleCancelar() {
        clienteGuardado = null;
        cerrarModal();
    }

    private void cerrarModal() {
        if (txtCedula != null && txtCedula.getScene() != null) {
            Stage stage = (Stage) txtCedula.getScene().getWindow();
            stage.close();
        }
    }

    private Cliente construirCliente() {
        Direccion direccion = new Direccion(
            txtDireccion.getText() != null ? txtDireccion.getText().trim() : "",
            "", "", "", "", "", "Venezuela");

        Contacto contacto = new Contacto(
            txtTelefonoPrincipal.getText() != null ? txtTelefonoPrincipal.getText().trim() : "",
            "",
            txtEmail.getText() != null ? txtEmail.getText().trim() : "");

        return new Cliente(
            txtCedula.getText() != null ? txtCedula.getText().trim() : "",
            txtNombres.getText() != null ? txtNombres.getText().trim() : "",
            txtApellidos.getText() != null ? txtApellidos.getText().trim() : "",
            direccion, contacto);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public Cliente getClienteGuardado() {
        return clienteGuardado;
    }
}
