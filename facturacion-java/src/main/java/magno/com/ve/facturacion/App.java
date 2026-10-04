package magno.com.ve.facturacion;

import atlantafx.base.theme.PrimerLight;   // Tema claro moderno
// import atlantafx.base.theme.PrimerDark;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.ui.controller.PanelAdminController;

import java.util.Objects;

public class App extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;

        // Tema claro moderno (AtlantaFX)
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // Icono de la aplicación
        try {
            stagePrincipal.getIcons().add(
                new Image(Objects.requireNonNull(
                    App.class.getResourceAsStream("/magno/com/ve/facturacion/img/logo.png")
                ))
            );
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono de la aplicación.");
        }

        cargarLogin();
        stagePrincipal.show();
    }

    public static void cargarLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/magno/com/ve/facturacion/view/Login.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // CSS personalizado
        scene.getStylesheets().add(
            Objects.requireNonNull(
                App.class.getResource("/magno/com/ve/facturacion/css/magno.css")
            ).toExternalForm()
        );

        stagePrincipal.setScene(scene);
        stagePrincipal.setTitle(AppConfig.NOMBRE_SISTEMA + " | Iniciar Sesión");

        // Ocupa toda la pantalla
        stagePrincipal.setResizable(true);
        stagePrincipal.setMaximized(true);
        stagePrincipal.setMinWidth(900);
        stagePrincipal.setMinHeight(600);
    }

    public static void cargarVentanaPrincipal(Usuario usuario) throws Exception {
        System.out.println("═══════════════════════════════════════");
        System.out.println(" APP - Cargando ventana principal");
        System.out.println("  Usuario: " + usuario);
        System.out.println("  Rol: " + (usuario != null && usuario.getRol() != null
                ? usuario.getRol().name() : "NULL"));
        System.out.println("═══════════════════════════════════════");

        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/magno/com/ve/facturacion/view/VentanaPrincipal.fxml")
        );
        Parent root = loader.load();

        PanelAdminController controller = loader.getController();
        if (controller != null) {
            controller.setUsuario(usuario);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            Objects.requireNonNull(
                App.class.getResource("/magno/com/ve/facturacion/css/magno.css")
            ).toExternalForm()
        );

        stagePrincipal.setScene(scene);
        stagePrincipal.setTitle(AppConfig.NOMBRE_SISTEMA + " | " + AppConfig.SLOGAN);

        // Ventana principal maximizada
        stagePrincipal.setResizable(true);
        stagePrincipal.setMaximized(true);
        stagePrincipal.setMinWidth(1100);
        stagePrincipal.setMinHeight(700);
    }

    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public static void main(String[] args) {
        // Iniciar el servidor TCP en un hilo de fondo
        new Thread(() -> {
            try {
                int puerto = Integer.parseInt(System.getProperty("tcp.port", "9100"));
                new magno.com.ve.facturacion.api.tcp.TcpServer(puerto).start();
            } catch (Exception e) {
                System.err.println("Error iniciando servidor TCP: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        launch(args);
    }
}