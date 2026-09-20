package magno.com.ve.facturacion.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import magno.com.ve.facturacion.App;
import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.enums.Rol;
import magno.com.ve.facturacion.domain.model.Usuario;

import java.net.URL;

public class VentanaPrincipalController {

    @FXML private Label lblEmpresa;
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Button btnCerrarSesion;
    @FXML private TabPane tabPane;
    @FXML private Label lblEstado;
    @FXML private Label lblVersion;

    private Usuario usuarioActual;

    @FXML
    public void initialize() { }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        System.out.println("═══════════════════════════════════════");
        System.out.println(" USUARIO SETEADO");
        System.out.println("  Username: " + usuario.getUsername());
        System.out.println("  Rol:      " + usuario.getRol());
        System.out.println("═══════════════════════════════════════");

        lblUsuario.setText(usuario.getNombreCompleto());
        lblRol.setText(usuario.getRol() != null ? usuario.getRol().getNombre() : "—");
        lblVersion.setText(AppConfig.NOMBRE_SISTEMA + " v" + AppConfig.VERSION);

        construirPestanas();
    }

    private void construirPestanas() {
        tabPane.getTabs().clear();
        Rol rol = usuarioActual.getRol();

        if (rol == Rol.ADMINISTRADOR) {
            agregarPestanaSegura("Almacén", "/magno/com/ve/facturacion/view/PanelAlmacen.fxml");
            agregarPestanaSegura("Facturación", "/magno/com/ve/facturacion/view/PanelFacturacion.fxml");
            agregarPestanaProximamente("Reportes");
            agregarPestanaProximamente("Configuración");
        }
        else if (rol == Rol.CAJERO) {
            agregarPestanaSegura("Facturación", "/magno/com/ve/facturacion/view/PanelFacturacion.fxml");
        }
        else if (rol == Rol.ALMACENISTA) {
            agregarPestanaSegura("Almacén", "/magno/com/ve/facturacion/view/PanelAlmacen.fxml");
        }
        else {
            agregarPestanaProximamente("Sin acceso");
        }

        // 🔑 Ocultar la barra de pestañas si solo hay 1
        if (tabPane.getTabs().size() <= 1) {
            Platform.runLater(() -> {
                tabPane.lookupAll(".tab-header-area").forEach(node -> {
                    node.setStyle("-fx-pref-height: 0; -fx-max-height: 0; -fx-min-height: 0;");
                    node.setVisible(false);
                    node.setManaged(false);
                });
                tabPane.lookupAll(".tab-header-background").forEach(node -> {
                    node.setStyle("-fx-pref-height: 0; -fx-max-height: 0; -fx-min-height: 0;");
                    node.setVisible(false);
                    node.setManaged(false);
                });
            });
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldTab, newTab) -> {
                if (newTab != null) {
                    actualizarEstado("Módulo: " + newTab.getText());
                }
            }
        );
    }

    private void agregarPestanaSegura(String titulo, String fxmlPath) {
        URL recurso = getClass().getResource(fxmlPath);

        if (recurso == null) {
            Tab tab = new Tab(titulo, crearPanelPlaceholder(titulo,
                "Este módulo aún no está implementado."));
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(recurso);
            Parent contenido = loader.load();
            Tab tab = new Tab(titulo, contenido);
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        } catch (Exception e) {
            Tab tab = new Tab(titulo, crearPanelPlaceholder(titulo,
                "Error al cargar el módulo: " + e.getMessage()));
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
            System.err.println("❌ Error cargando " + fxmlPath + ": " + e.getMessage());
        }
    }

    private void agregarPestanaProximamente(String titulo) {
        Tab tab = new Tab(titulo, crearPanelPlaceholder(titulo,
            "Este módulo estará disponible próximamente"));
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    private VBox crearPanelPlaceholder(String titulo, String mensaje) {
        VBox box = new VBox(20);
        box.setAlignment(javafx.geometry.Pos.CENTER);
        box.setStyle("-fx-background-color: #F8FAFC;");

        Label icono = new Label("🚧");
        icono.setStyle("-fx-font-size: 72px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        box.getChildren().addAll(icono, lblTitulo, lblMensaje);
        return box;
    }

    public void actualizarEstado(String mensaje) {
        if (lblEstado != null) {
            lblEstado.setText(mensaje);
        }
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