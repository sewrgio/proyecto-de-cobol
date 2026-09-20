package magno.com.ve.facturacion;

import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.service.UsuarioService;
import magno.com.ve.facturacion.ui.VentanaPrincipal;
import magno.com.ve.facturacion.ui.dialog.DialogoLogin;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        // Estilo del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar
        }

        SwingUtilities.invokeLater(() -> {

            UsuarioService usuarioService = new UsuarioService();

            // ===== 1. Mostrar Login =====
            Usuario usuario = DialogoLogin.mostrar(null, usuarioService);

            if (usuario == null) {
                // El usuario canceló el login
                System.out.println("Login cancelado. Cerrando aplicación.");
                System.exit(0);
            }

            System.out.println("Usuario autenticado: " + usuario);

            // ===== 2. Mostrar la ventana principal =====
            VentanaPrincipal ventana = new VentanaPrincipal(usuario);
            ventana.setVisible(true);
        });
    }
}