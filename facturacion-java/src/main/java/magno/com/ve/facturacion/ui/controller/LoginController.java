package magno.com.ve.facturacion.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import magno.com.ve.facturacion.App;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.UsuarioService;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> txtUsuario.requestFocus());
    }

    @FXML
    public void handleLogin() {
        lblMensaje.setText(" ");

        try {
            String username = txtUsuario.getText().trim();
            String password = txtPassword.getText();

            System.out.println("═══════════════════════════════════════");
            System.out.println(" LOGIN - Intento de autenticación");
            System.out.println("═══════════════════════════════════════");
            System.out.println("  Username: " + username);

            Usuario usuario = usuarioService.autenticar(username, password);

            System.out.println("  ✅ Autenticado:");
            System.out.println("     Username: " + usuario.getUsername());
            System.out.println("     Nombre:   " + usuario.getNombreCompleto());
            System.out.println("     Rol:      " + usuario.getRol());
            System.out.println("     Rol name: " + (usuario.getRol() != null ? usuario.getRol().name() : "NULL"));
            System.out.println("═══════════════════════════════════════");

            App.cargarVentanaPrincipal(usuario);

        } catch (ValidacionException ex) {
            lblMensaje.setText("✗ " + ex.getMessage());
            txtPassword.clear();
            txtPassword.requestFocus();
        } catch (Exception ex) {
            lblMensaje.setText("✗ Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleCancelar() {
        Platform.exit();
        System.exit(0);
    }
}