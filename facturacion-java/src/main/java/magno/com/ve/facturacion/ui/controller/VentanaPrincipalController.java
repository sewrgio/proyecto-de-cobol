package magno.com.ve.facturacion.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import magno.com.ve.facturacion.App;
import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.enums.Rol;
import magno.com.ve.facturacion.domain.model.Usuario;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VentanaPrincipalController {

    @FXML private Label lblEmpresa;
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Label lblFechaHora;
    @FXML private Label lblBreadcrumb;
    @FXML private Label lblEstado;
    @FXML private Label lblVersion;
    @FXML private Button btnCerrarSesion;
    @FXML private StackPane contenidoPrincipal;
    @FXML private TextField txtBusquedaGlobal;

    private Usuario usuarioActual;

    @FXML
    public void initialize() { }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        lblUsuario.setText(usuario.getNombreCompleto());
        lblRol.setText(usuario.getRol() != null ? usuario.getRol().getNombre() : "—");
        lblVersion.setText(AppConfig.NOMBRE_SISTEMA + " v" + AppConfig.VERSION);

        // Fecha/hora
        String fecha = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy - HH:mm"));
        lblFechaHora.setText(fecha);

        // Cargar contenido inicial según rol
        cargarContenidoInicial();
    }

    private void cargarContenidoInicial() {
        Rol rol = usuarioActual.getRol();

        if (rol == Rol.ADMINISTRADOR) {
            mostrarAlmacen();
        } else if (rol == Rol.CAJERO) {
            mostrarFacturacion();
        } else if (rol == Rol.ALMACENISTA) {
            mostrarAlmacen();
        }
    }

    @FXML
    public void handleMenuAlmacen() {
        mostrarAlmacen();
    }

    @FXML
    public void handleMenuFacturacion() {
        mostrarFacturacion();
    }

    @FXML
    public void handleMenuClientes() {
        cargarVista("Clientes", "/magno/com/ve/facturacion/view/PanelCliente.fxml");
    }

    @FXML
    public void handleMenuReportes() {
        cargarVista("Reportes", null);
    }

    private void mostrarAlmacen() {
        cargarVista("Almacén", "/magno/com/ve/facturacion/view/PanelAlmacen.fxml");
    }

    private void mostrarFacturacion() {
        cargarVista("Facturación", "/magno/com/ve/facturacion/view/PanelFacturacion.fxml");
    }

    /**
     * Carga una vista en el área de contenido principal.
     */
    private void cargarVista(String nombre, String fxmlPath) {
        lblBreadcrumb.setText("Inicio  ›  " + nombre);
        lblEstado.setText("Módulo: " + nombre);

        if (fxmlPath == null) {
            // Placeholder
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(crearPlaceholder(nombre));
            return;
        }

        URL recurso = getClass().getResource(fxmlPath);
        if (recurso == null) {
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(crearPlaceholder(nombre + " (no implementado)"));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(recurso);
            Parent vista = loader.load();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(vista);
            System.out.println("✅ Vista cargada: " + nombre);
        } catch (Throwable e) {
            System.err.println("❌ Error cargando " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(
                crearPlaceholder("Error: " + e.getMessage()));
        }
    }

    private javafx.scene.layout.VBox crearPlaceholder(String titulo) {
        javafx.scene.layout.VBox box = new javafx.scene.layout.VBox(20);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.setStyle("-fx-background-color: #F8FAFC;");

        Label icono = new Label("🚧");
        icono.setStyle("-fx-font-size: 72px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label lblMensaje = new Label("Este módulo estará disponible próximamente");
        lblMensaje.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        box.getChildren().addAll(icono, lblTitulo, lblMensaje);
        return box;
    }

    @FXML
    public void handleCerrarSesion() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar sesión");
        confirm.setHeaderText("¿Está seguro que desea cerrar sesión?");
        confirm.setContentText("Tendrá que volver a autenticarse.");

        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    App.cargarLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}