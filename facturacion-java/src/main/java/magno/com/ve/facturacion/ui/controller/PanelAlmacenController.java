package magno.com.ve.facturacion.ui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import magno.com.ve.facturacion.domain.model.Usuario;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PanelAlmacenController implements Initializable {

    // ==================== HEADER ====================
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblUsuario;
    @FXML private Label lblRolUsuario;

    // ==================== KPIs ====================
    @FXML private Label lblProdTotales;
    @FXML private Label lblProdBajo;
    @FXML private Label lblValorInv;
    @FXML private Label lblMovDia;

    // ==================== BUSCADOR ====================
    @FXML private TextField txtBuscar;

    // ==================== TABLA ====================
    @FXML private TableView<Producto> tblInventario;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colProducto;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colStock;
    @FXML private TableColumn<Producto, String> colMinimo;
    @FXML private TableColumn<Producto, String> colUbicacion;
    @FXML private TableColumn<Producto, Void>   colAcciones;

    @FXML private Label lblPaginacion;

    private final ObservableList<Producto> productos = FXCollections.observableArrayList();
    private Usuario usuarioActual;

    // ==================== INICIALIZACIÓN ====================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblFecha.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
        lblHora.setText(LocalTime.now().format(
                DateTimeFormatter.ofPattern("hh:mm a")));

        // Configurar columnas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));

        // Columnas con estilo especial (stock y mínimo)
        colStock.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getStock() + "  " + cd.getValue().getEstadoStock()));
        colMinimo.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().getMinimo())));

        // Columna de acciones (botones)
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏ Editar");
            private final Button btnAjustar = new Button("⚙ Ajustar");
            private final HBox box = new HBox(6, btnEditar, btnAjustar);

            {
                btnEditar.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-cursor: hand; -fx-font-size: 11px;");
                btnAjustar.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-cursor: hand; -fx-font-size: 11px;");
                box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                btnEditar.setOnAction(e -> {
                    Producto p = getTableView().getItems().get(getIndex());
                    handleEditarProducto(p);
                });
                btnAjustar.setOnAction(e -> {
                    Producto p = getTableView().getItems().get(getIndex());
                    handleAjustarStock(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Datos demo
        cargarProductosDemo();
        tblInventario.setItems(productos);

        // KPIs demo
        lblProdTotales.setText("1,248");
        lblProdBajo.setText("27");
        lblValorInv.setText("$ 582,430.75");
        lblMovDia.setText("18");

        // Pie de tabla
        lblPaginacion.setText("Mostrando 1 a " + productos.size() + " de 1,248 productos");

        // Buscador en vivo
        txtBuscar.textProperty().addListener((obs, old, val) -> filtrar(val));
    }

    // ==================== INYECCIÓN DEL USUARIO ====================
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (usuario == null) return;

        if (lblUsuario != null)    lblUsuario.setText(usuario.getNombreCompleto());
        if (lblRolUsuario != null && usuario.getRol() != null)
            lblRolUsuario.setText(usuario.getRol().name());
    }

    public Usuario getUsuarioActual() { return usuarioActual; }

    // ==================== FILTRO ====================
    private void filtrar(String texto) {
        if (texto == null || texto.isBlank()) {
            tblInventario.setItems(productos);
            return;
        }
        String t = texto.toLowerCase();
        ObservableList<Producto> filtrados = productos.filtered(p ->
                p.getCodigo().toLowerCase().contains(t) ||
                p.getProducto().toLowerCase().contains(t) ||
                p.getCategoria().toLowerCase().contains(t));
        tblInventario.setItems(filtrados);
    }

    // ==================== DATOS DEMO ====================
    private void cargarProductosDemo() {
        productos.addAll(
                new Producto("PRD-00125", "🔨 Martillo profesional 20 oz",
                        "Acero forjado, mango de fibra de vidrio",
                        "Herramientas", 56, 10, "A01-E02-P03", "OK"),
                new Producto("PRD-00248", "🔧 Taladro inalámbrico 20V",
                        "Batería de litio, incluye 2 baterías",
                        "Herramientas", 8, 12, "A02-E01-P04", "Bajo"),
                new Producto("PRD-00333", "🎨 Pintura látex blanca 1 gal",
                        "Interior/exterior, alta cobertura",
                        "Pinturas", 32, 15, "B01-E03-P02", "OK"),
                new Producto("PRD-00401", "🔩 Tornillo cabeza plana 1/4\" x 1\"",
                        "Caja de 100 unidades",
                        "Ferretería", 3, 20, "B02-E02-P05", "Crítico"),
                new Producto("PRD-00517", "📏 Cinta métrica 5m",
                        "Carcasa de goma anti-deslizante",
                        "Medición", 15, 8, "A03-E01-P01", "OK"),
                new Producto("PRD-00622", "🥽 Lentes de seguridad",
                        "Antirreflejo, protección UV",
                        "Seguridad", 5, 10, "C01-E02-P03", "Bajo")
        );
    }

    // ==================== ACCIONES ====================
    @FXML private void handleNuevoProducto()    { mostrarInfo("Nuevo producto", "Formulario para crear un nuevo producto."); }
    @FXML private void handleEntradaMercancia() { mostrarInfo("Entrada de mercancía", "Registrar entrada de productos al inventario."); }
    @FXML private void handleSalida()           { mostrarInfo("Salida", "Registrar salida de productos del inventario."); }
    @FXML private void handleAjuste()           { mostrarInfo("Ajuste de inventario", "Ajustar cantidades manualmente."); }
    @FXML private void handleImprimir()         { mostrarInfo("Imprimir listado", "Se imprimirá el listado de productos."); }
    @FXML private void handleLimpiarFiltros()   { txtBuscar.clear(); tblInventario.setItems(productos); }

    @FXML private void handlePaginaAnterior()   { /* TODO */ }
    @FXML private void handleIrPagina2()        { /* TODO */ }
    @FXML private void handleIrPagina3()        { /* TODO */ }
    @FXML private void handleIrUltimaPagina()   { /* TODO */ }
    @FXML private void handlePaginaSiguiente()  { /* TODO */ }

    private void handleEditarProducto(Producto p) {
        mostrarInfo("Editar producto", "Editando: " + p.getProducto());
    }

    private void handleAjustarStock(Producto p) {
        mostrarInfo("Ajustar stock", "Ajustar stock de: " + p.getProducto());
    }

    private void mostrarInfo(String titulo, String mensaje) {
        try (java.net.Socket socket = new java.net.Socket("127.0.0.1", Integer.parseInt(System.getProperty("tcp.port", "9100")));
             java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
             java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))) {
            
            // Enviar petición JSON TCP genérica
            out.println("{\"modulo\":\"ALMACEN\", \"accion\":\"" + titulo + "\", \"detalle\":\"" + mensaje + "\"}");
            
            String respuestaServidor = in.readLine();
            
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Servidor TCP Respondió: \n" + respuestaServidor, ButtonType.OK);
            a.setTitle(titulo + " - RED TCP");
            a.setHeaderText("Operación de Almacén Sincronizada");
            a.showAndWait();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Fallo al conectar con el servidor TCP: " + e.getMessage(), ButtonType.OK);
            a.showAndWait();
        }
    }

    // ==================== DTO ====================
    public static class Producto {
        private final String codigo;
        private final String producto;
        private final String descripcion;
        private final String categoria;
        private final int stock;
        private final int minimo;
        private final String ubicacion;
        private final String estado;

        public Producto(String codigo, String producto, String descripcion,
                        String categoria, int stock, int minimo,
                        String ubicacion, String estado) {
            this.codigo = codigo;
            this.producto = producto;
            this.descripcion = descripcion;
            this.categoria = categoria;
            this.stock = stock;
            this.minimo = minimo;
            this.ubicacion = ubicacion;
            this.estado = estado;
        }

        public String getCodigo()      { return codigo; }
        public String getProducto()    { return producto; }
        public String getDescripcion() { return descripcion; }
        public String getCategoria()   { return categoria; }
        public int    getStock()       { return stock; }
        public int    getMinimo()      { return minimo; }
        public String getUbicacion()   { return ubicacion; }
        public String getEstado()      { return estado; }

        public String getEstadoStock() {
            switch (estado) {
                case "OK":      return "● OK";
                case "Bajo":    return "⚠ Bajo";
                case "Crítico": return "▲ Crítico";
                default:        return estado;
            }
        }
    }
}