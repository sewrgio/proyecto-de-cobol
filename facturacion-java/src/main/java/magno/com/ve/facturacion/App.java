package magno.com.ve.facturacion;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.ui.controller.PanelAdminController;

import java.net.URL;
import java.util.Objects;

public class App extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;

        // Tema claro moderno (AtlantaFX)
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // Icono de la aplicación
        cargarIcono(stagePrincipal);

        // Cargar pantalla de login
        cargarLogin();

        // Mostrar la ventana
        stagePrincipal.show();

        // CRÍTICO: aplicar pantalla completa DESPUÉS de show()
        aplicarPantallaCompleta(stagePrincipal);

        // Refuerzo: re-aplicar tras un pequeño delay
        Platform.runLater(() -> aplicarPantallaCompleta(stagePrincipal));
    }

    /**
     * Fuerza la ventana a ocupar TODA la pantalla (sin usar setMaximized(true),
     * que se rompe con los diálogos en Linux/GTK).
     */
    private static void aplicarPantallaCompleta(Stage stage) {
        if (stage == null) return;
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaximized(false);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    /**
     * Se llama cada vez que se cambia de pantalla (login → principal).
     */
    private static void reaplicarPantallaCompleta() {
        Platform.runLater(() -> {
            aplicarPantallaCompleta(stagePrincipal);
            Platform.runLater(() -> aplicarPantallaCompleta(stagePrincipal));
        });
    }

    private void cargarIcono(Stage stage) {
        String[] rutas = {
                "/magno/com/ve/facturacion/img/logo.png",
                "/magno/com/ve/facturacion/img/logo.jpg",
                "/img/logo.png",
                "/logo.png"
        };
        for (String ruta : rutas) {
            URL url = App.class.getResource(ruta);
            if (url != null) {
                try {
                    stage.getIcons().add(new Image(url.toExternalForm()));
                    System.out.println("✅ Icono cargado: " + ruta);
                    return;
                } catch (Exception e) {
                    System.err.println("⚠️ Error cargando " + ruta + ": " + e.getMessage());
                }
            }
        }
        System.err.println("❌ No se encontró el icono. Verifica que exista:");
        System.err.println("   src/main/resources/magno/com/ve/facturacion/img/logo.png");
    }

    public static void cargarLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("/magno/com/ve/facturacion/view/Login.fxml")
        );
        Parent root = loader.load();

        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        App.class.getResource("/magno/com/ve/facturacion/css/magno.css")
                ).toExternalForm()
        );

        stagePrincipal.setScene(scene);
        stagePrincipal.setTitle(AppConfig.NOMBRE_SISTEMA + " | Iniciar Sesión");
        stagePrincipal.setResizable(true);
        stagePrincipal.setMinWidth(900);
        stagePrincipal.setMinHeight(600);

        reaplicarPantallaCompleta();
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
        stagePrincipal.setResizable(true);
        stagePrincipal.setMinWidth(1100);
        stagePrincipal.setMinHeight(700);

        reaplicarPantallaCompleta();
    }

    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public static void main(String[] args) {
        // ✅ Forzar WM_CLASS en Linux para que el dock use el icono correcto
        System.setProperty("glass.gtk.windowClass", "magno-pos");
        System.setProperty("javafx.application.name", "magno-pos");

        // Servidor TCP en hilo aparte
        new Thread(() -> {
            try {
                int puerto = Integer.parseInt(System.getProperty("tcp.port", "9100"));
                new magno.com.ve.facturacion.api.tcp.TcpServer(puerto).start();
            } catch (Exception e) {
                System.err.println("Error iniciando servidor TCP: " + e.getMessage());
                e.printStackTrace();
            }
        }, "tcp-server").start();

        launch(args);
    }
}