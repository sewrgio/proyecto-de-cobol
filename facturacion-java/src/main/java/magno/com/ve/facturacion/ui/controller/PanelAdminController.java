package magno.com.ve.facturacion.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import magno.com.ve.facturacion.domain.model.Usuario;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador del panel de administración (Dashboard).
 */
public class PanelAdminController implements Initializable {

    // ==================== USUARIO ACTUAL ====================
    private Usuario usuarioActual;

    // ==================== HEADER ====================
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;

    // ==================== KPIs ====================
    @FXML private Label lblVentasDia;
    @FXML private Label lblTicketProm;
    @FXML private Label lblProdVendidos;
    @FXML private Label lblUsuariosAct;

    // ==================== GRÁFICO ====================
    @FXML private LineChart<String, Number> chartVentas;
    @FXML private ComboBox<String> cmbRango;

    // ==================== TABLA ====================
    @FXML private TableView<Movimiento> tblMovimientos;
    @FXML private TableColumn<Movimiento, String> colId;
    @FXML private TableColumn<Movimiento, String> colFecha;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, String> colDesc;
    @FXML private TableColumn<Movimiento, String> colUsuario;
    @FXML private TableColumn<Movimiento, String> colTotal;
    @FXML private TableColumn<Movimiento, String> colEstado;

    private final ObservableList<Movimiento> movimientos = FXCollections.observableArrayList();

    // ==================== INICIALIZACIÓN ====================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Fecha / hora actual
        lblFecha.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
        lblHora.setText("🕐 " + LocalTime.now().format(
                DateTimeFormatter.ofPattern("hh:mm a")));

        // Combo rango del gráfico
        cmbRango.setItems(FXCollections.observableArrayList(
                "Últimos 7 días", "Últimos 30 días", "Este año"));
        cmbRango.getSelectionModel().selectFirst();

        // Configurar tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarMovimientosDemo();
        tblMovimientos.setItems(movimientos);

        // KPIs demo
        lblVentasDia.setText("$ 4,250.75");
        lblTicketProm.setText("$ 156.80");
        lblProdVendidos.setText("286");
        lblUsuariosAct.setText("128");

        // Gráfico
        cargarGraficoVentas();
    }

    // ==================== INYECCIÓN DEL USUARIO ====================
    /**
     * Llamado desde App.java después de cargar el FXML.
     * Actualiza los Labels del header con el usuario logueado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (usuario == null) return;

        if (lblNombreUsuario != null) {
            lblNombreUsuario.setText(usuario.getNombreCompleto());
        }

        if (lblRolUsuario != null && usuario.getRol() != null) {
            // getRol() devuelve un enum Rol → usamos .name() para texto legible
            lblRolUsuario.setText("✔ " + usuario.getRol().name());
        }
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // ==================== DATOS DEMO ====================
    private void cargarGraficoVentas() {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ventas");
        serie.getData().add(new XYChart.Data<>("15 may", 2000));
        serie.getData().add(new XYChart.Data<>("16 may", 3200));
        serie.getData().add(new XYChart.Data<>("17 may", 2800));
        serie.getData().add(new XYChart.Data<>("18 may", 3600));
        serie.getData().add(new XYChart.Data<>("19 may", 3400));
        serie.getData().add(new XYChart.Data<>("20 may", 4250));
        serie.getData().add(new XYChart.Data<>("21 may", 4250));

        chartVentas.getData().add(serie);
    }

    private void cargarMovimientosDemo() {
        movimientos.addAll(
                new Movimiento("#V-20250521-001", "21/05/2025 10:21 AM", "Venta",
                        "Venta #V-20250521-001", "Ana López", "$ 235.50", "✔ Completado"),
                new Movimiento("#V-20250521-002", "21/05/2025 10:15 AM", "Venta",
                        "Venta #V-20250521-002", "Juan Pérez", "$ 89.90", "✔ Completado"),
                new Movimiento("#V-20250521-003", "21/05/2025 10:08 AM", "Venta",
                        "Venta #V-20250521-003", "María García", "$ 412.00", "✔ Completado"),
                new Movimiento("#D-20250521-001", "21/05/2025 09:58 AM", "Devolución",
                        "Devolución producto #P-1050", "Carlos Mendoza", "-$ 45.00", "⟳ Procesado"),
                new Movimiento("#V-20250521-004", "21/05/2025 09:52 AM", "Venta",
                        "Venta #V-20250521-004", "Ana López", "$ 168.75", "✔ Completado")
        );
    }

    // ==================== ACCIONES · NAVEGACIÓN ====================
    @FXML private void handleNavDashboard() { /* ya estamos en el dashboard */ }

    @FXML private void handleMenuUsuarios()     { mostrarInfo("Usuarios",     "Aquí irá la gestión de usuarios."); }
    @FXML private void handleMenuProductos()    { mostrarInfo("Productos",    "Aquí irá la gestión de productos."); }
    @FXML private void handleMenuReportes()     { mostrarInfo("Reportes",     "Aquí irán los reportes del sistema."); }
    @FXML private void handleMenuFiscal()       { mostrarInfo("Configuración","Parámetros fiscales y del sistema."); }
    @FXML private void handleMenuCajas()        { mostrarInfo("Cajas",        "Aquí irá la gestión de cajas."); }
    @FXML private void handleMenuInventario()   { mostrarInfo("Inventario",   "Aquí irá el inventario."); }

    // ==================== ACCIONES · RÁPIDAS ====================
    @FXML private void handleNuevoUsuario()     { mostrarInfo("Nuevo usuario",   "Formulario para crear un nuevo usuario."); }
    @FXML private void handleNuevoProducto()    { mostrarInfo("Nuevo producto",  "Formulario para crear un nuevo producto."); }
    @FXML private void handleGenerarReporte()   { mostrarInfo("Generar reporte", "Selecciona el tipo de reporte a generar."); }
    @FXML private void handleVerTodos()         { mostrarInfo("Movimientos",     "Aquí se verá la lista completa de movimientos."); }

    // ==================== ACCIONES · SESIÓN ====================
    @FXML
    private void handleCerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea cerrar la sesión actual?",
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.setTitle("Cerrar sesión");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                System.out.println("Cerrando sesión...");
                // TODO: volver al Login
            }
        });
    }

    // ==================== UTILIDADES ====================
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // ==================== DTO ====================
    public static class Movimiento {
        private final String id, fecha, tipo, descripcion, usuario, total, estado;

        public Movimiento(String id, String fecha, String tipo, String descripcion,
                          String usuario, String total, String estado) {
            this.id = id;
            this.fecha = fecha;
            this.tipo = tipo;
            this.descripcion = descripcion;
            this.usuario = usuario;
            this.total = total;
            this.estado = estado;
        }

        public String getId()          { return id; }
        public String getFecha()       { return fecha; }
        public String getTipo()        { return tipo; }
        public String getDescripcion() { return descripcion; }
        public String getUsuario()     { return usuario; }
        public String getTotal()       { return total; }
        public String getEstado()      { return estado; }
    }
}