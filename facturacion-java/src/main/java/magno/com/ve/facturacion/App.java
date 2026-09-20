package magno.com.ve.facturacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.ui.controller.VentanaPrincipalController;

public class App extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;

        stagePrincipal.setTitle(AppConfig.NOMBRE_SISTEMA + " | " + AppConfig.SLOGAN);
        stagePrincipal.setResizable(false);

        try {
            stagePrincipal.getIcons().add(
                new Image(App.class.getResourceAsStream(
                    "/magno/com/ve/facturacion/img/logo.png"))
            );
        } catch (Exception e) {
            // No hay logo, ignorar
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

        scene.getStylesheets().add(
            App.class.getResource("/magno/com/ve/facturacion/css/magno.css").toExternalForm()
        );

        stagePrincipal.setScene(scene);
        stagePrincipal.setTitle(AppConfig.NOMBRE_SISTEMA + " | Iniciar Sesión");
        stagePrincipal.setResizable(false);
        stagePrincipal.centerOnScreen();
        stagePrincipal.sizeToScene();
    }

    public static void cargarVentanaPrincipal(Usuario usuario) throws Exception {
        System.out.println("═══════════════════════════════════════");
        System.out.println(" APP - Cargando ventana principal");
        System.out.println("═══════════════════════════════════════");
        System.out.println("  Usuario recibido: " + usuario);
        System.out.println("  Rol: " + (usuario != null && usuario.getRol() != null
                            ? usuario.getRol().name() : "NULL"));
        System.out.println("═══════════════════════════════════════");

        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/magno/com/ve/facturacion/view/VentanaPrincipal.fxml")
        );
        Parent root = loader.load();

        VentanaPrincipalController controller = loader.getController();
        System.out.println("  Controller obtenido: " + controller);

        controller.setUsuario(usuario);

        Scene scene = new Scene(root, 1280, 850);
        scene.getStylesheets().add(
            App.class.getResource("/magno/com/ve/facturacion/css/magno.css").toExternalForm()
        );

        stagePrincipal.setScene(scene);
        stagePrincipal.setTitle(AppConfig.NOMBRE_SISTEMA + " | " + AppConfig.SLOGAN);
        stagePrincipal.setResizable(true);
        stagePrincipal.setWidth(1280);
        stagePrincipal.setHeight(850);
        stagePrincipal.centerOnScreen();
    }

    public static Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public static void main(String[] args) {
        launch(args);
    }
}