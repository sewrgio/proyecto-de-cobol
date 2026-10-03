package magno.com.ve.facturacion.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import magno.com.ve.facturacion.security.SesionUsuario;
import magno.com.ve.facturacion.domain.model.Usuario;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private void handleLogin() {
        String usuarioStr = txtUsuario.getText().trim();
        String passwordStr = txtPassword.getText();

        if (lblError != null) {
            lblError.setText("");
        }

        if (usuarioStr.isEmpty() || passwordStr.isEmpty()) {
            mostrarError("Por favor, complete todos los campos.");
            return;
        }

        String rol = validarCredenciales(usuarioStr, passwordStr);

        if (rol != null) {
            try {
                Usuario usuarioObj = new Usuario();
                usuarioObj.setActivo(true);
                SesionUsuario.getInstancia().iniciarSesion(usuarioObj);

                // ===== NAVEGACIÓN SEGÚN EL ROL =====
                String fxmlPath;
                String titulo;

                switch (rol) {
                    case "ADMIN":
                        fxmlPath = "/magno/com/ve/facturacion/view/VentanaPrincipal.fxml";
                        titulo = "MAGNO - Administración";
                        break;

                    case "CAJERO":
                        fxmlPath = "/magno/com/ve/facturacion/view/PanelFacturacion.fxml";
                        titulo = "MAGNO - Punto de Venta";
                        break;

                    case "ALMACEN":
                        fxmlPath = "/magno/com/ve/facturacion/view/PanelAlmacen.fxml";
                        titulo = "MAGNO - Almacén";
                        break;

                    default:
                        fxmlPath = "/magno/com/ve/facturacion/view/VentanaPrincipal.fxml";
                        titulo = "MAGNO";
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                Stage stage = (Stage) txtUsuario.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(titulo);

                // Pantalla completa
                stage.setMaximized(true);
                stage.setMinWidth(1100);
                stage.setMinHeight(700);
                stage.centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("Error al cargar la ventana: " + e.getMessage());
            }
        } else {
            mostrarError("Credenciales inválidas.");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    private String validarCredenciales(String usuario, String password) {
        if ("admin".equalsIgnoreCase(usuario) && "admin123".equals(password)) {
            return "ADMIN";
        }
        if ("cajero".equalsIgnoreCase(usuario) && "cajero123".equals(password)) {
            return "CAJERO";
        }
        if ("almacen".equalsIgnoreCase(usuario) && "almacen123".equals(password)) {
            return "ALMACEN";
        }
        return null;
    }

    private void mostrarError(String mensaje) {
        if (lblError != null) {
            lblError.setText(mensaje);
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }
}