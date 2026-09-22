package magno.com.ve.facturacion.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import magno.com.ve.facturacion.domain.facturacion.DatosFactura;
import magno.com.ve.facturacion.domain.facturacion.MetodoFacturacion;
import magno.com.ve.facturacion.domain.facturacion.ResultadoFactura;
import magno.com.ve.facturacion.domain.model.ProductoInventario;
import magno.com.ve.facturacion.integration.cobol.CobolConnectorFile;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;
import magno.com.ve.facturacion.integration.cobol.dto.ItemDTO;
import magno.com.ve.facturacion.integration.facturacion.MetodoFacturacionPDF;
import magno.com.ve.facturacion.service.InventarioService;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PanelFacturacionController {

    // ═══════════════════════════════════════════════════════════
    //  BÚSQUEDA
    // ═══════════════════════════════════════════════════════════
    @FXML private TextField txtBuscarProducto;
    @FXML private ListView<ProductoInventario> listResultados;

    // ═══════════════════════════════════════════════════════════
    //  MÉTODOS DE PAGO
    // ═══════════════════════════════════════════════════════════
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private TextField txtMontoPago;
    @FXML private TableView<PagoRealizado> tblPagos;
    @FXML private TableColumn<PagoRealizado, String> colPagoMetodo;
    @FXML private TableColumn<PagoRealizado, Double> colPagoMonto;
    @FXML private TableColumn<PagoRealizado, String> colPagoRef;

    // ═══════════════════════════════════════════════════════════
    //  CARRITO
    // ═══════════════════════════════════════════════════════════
    @FXML private TableView<ItemCarrito> tblCarrito;
    @FXML private TableColumn<ItemCarrito, Integer> colCant;
    @FXML private TableColumn<ItemCarrito, String> colCodigo;
    @FXML private TableColumn<ItemCarrito, String> colNombre;
    @FXML private TableColumn<ItemCarrito, Double> colPrecio;
    @FXML private TableColumn<ItemCarrito, String> colAlicuota;
    @FXML private TableColumn<ItemCarrito, Double> colSubtotal;
    @FXML private Label lblItemsCarrito;

    // ═══════════════════════════════════════════════════════════
    //  TOTALES
    // ═══════════════════════════════════════════════════════════
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblBaseImponible;
    @FXML private Label lblBaseExento;
    @FXML private Label lblIVA;
    @FXML private Label lblIGTF;
    @FXML private Label lblTotal;
    @FXML private Label lblTotalPagado;
    @FXML private Label lblCambio;

    // ═══════════════════════════════════════════════════════════
    //  INFO DEL CAJERO Y CLIENTE
    // ═══════════════════════════════════════════════════════════
    @FXML private Label lblCajero;
    @FXML private Label lblTurno;
    @FXML private Label lblFechaHora;
    @FXML private Label lblEstadoConexion;
    @FXML private Label lblClienteRif;
    @FXML private Label lblClienteNombre;

    // ═══════════════════════════════════════════════════════════
    //  BARRA DE ESTADO
    // ═══════════════════════════════════════════════════════════
    @FXML private Label lblMensajeEstado;
    @FXML private Label lblContadorItems;
    @FXML private Label lblContadorPagos;

    // ═══════════════════════════════════════════════════════════
    //  ESTADO
    // ═══════════════════════════════════════════════════════════
    private final InventarioService inventarioService = new InventarioService();
    private final ObservableList<ItemCarrito> carrito = FXCollections.observableArrayList();
    private final ObservableList<PagoRealizado> pagos = FXCollections.observableArrayList();
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "VE"));

    private String clienteRif = "V-99999999";
    private String clienteNombre = "CLIENTE CONTADO";
    private String cajeroNombre = "SONIA GALINDO";
    private String cajeroCodigo = "013";
    private String cajaNumero = "013";

    // ═══════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ═══════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        try {
            inventarioService.cargar();
        } catch (Exception e) {
            System.err.println("⚠ No se pudo cargar el inventario: " + e.getMessage());
        }

        // Columnas del carrito
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colAlicuota.setCellValueFactory(new PropertyValueFactory<>("alicuota"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tblCarrito.setItems(carrito);

        // Columnas de pagos
        colPagoMetodo.setCellValueFactory(new PropertyValueFactory<>("metodo"));
        colPagoMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colPagoRef.setCellValueFactory(new PropertyValueFactory<>("referencia"));
        tblPagos.setItems(pagos);

        // CellFactory de resultados
        listResultados.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProductoInventario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s - %s",
                        item.getCodigo(), item.getNombre(),
                        formatoMoneda.format(item.getPrecio())));
                }
            }
        });

        listResultados.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProductoInventario seleccionado = listResultados.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    agregarAlCarrito(seleccionado);
                }
            }
        });

        txtBuscarProducto.setOnAction(e -> handleBuscarProducto());

        // ComboBox de métodos de pago
        cmbMetodoPago.setItems(FXCollections.observableArrayList(
            "💵 Efectivo",
            "💳 Tarjeta Débito",
            "💳 Tarjeta Crédito",
            "🏦 Transferencia",
            "📱 Pago Móvil",
            "📝 Crédito (CxC)"
        ));
        cmbMetodoPago.getSelectionModel().selectFirst();

        configurarAtajos();
        recalcularTotales();
        inicializarInfoCaja();
    }

    private void inicializarInfoCaja() {
        if (lblCajero != null) lblCajero.setText("👤 Cajero: " + cajeroNombre);
        if (lblTurno != null) lblTurno.setText("Turno: " + cajaNumero);
        if (lblFechaHora != null) {
            String fecha = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            lblFechaHora.setText("📅 " + fecha);
        }
        if (lblEstadoConexion != null) {
            boolean disponible = new CobolConnectorFile().estaDisponible();
            if (disponible) {
                lblEstadoConexion.setText("🟢 COBOL Online");
                lblEstadoConexion.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
            } else {
                lblEstadoConexion.setText("🔴 COBOL Offline");
                lblEstadoConexion.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            }
        }
        mostrarClienteEnPantalla();
        log("🟢 Listo para facturar");
    }

    private void mostrarClienteEnPantalla() {
        if (lblClienteRif != null) lblClienteRif.setText(clienteRif);
        if (lblClienteNombre != null) lblClienteNombre.setText(clienteNombre);
    }

    /**
     * Muestra un mensaje en la barra de estado.
     */
    private void log(String mensaje) {
        if (lblMensajeEstado != null) {
            lblMensajeEstado.setText(mensaje);
        }
        System.out.println("[POS] " + mensaje);
    }

    // ═══════════════════════════════════════════════════════════
    //  ATAJOS
    // ═══════════════════════════════════════════════════════════
    private void configurarAtajos() {
        tblCarrito.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    switch (event.getCode()) {
                        case F12: handleFacturar(); event.consume(); break;
                        case F4: handleCancelar(); event.consume(); break;
                        case F7: handleDevolucion(); event.consume(); break;
                        case F9: handleArqueo(); event.consume(); break;
                        case F8: handleCambiarCliente(); event.consume(); break;
                        case ESCAPE:
                            txtBuscarProducto.clear();
                            listResultados.getItems().clear();
                            txtBuscarProducto.requestFocus();
                            event.consume();
                            break;
                        default: break;
                    }
                });
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    //  BÚSQUEDA
    // ═══════════════════════════════════════════════════════════
    @FXML
    public void handleBuscarProducto() {
        String texto = txtBuscarProducto.getText().trim();
        if (texto.isEmpty()) {
            listResultados.getItems().clear();
            return;
        }

        List<ProductoInventario> resultados = inventarioService.buscar(texto);
        listResultados.setItems(FXCollections.observableArrayList(resultados));

        if (resultados.size() == 1) {
            agregarAlCarrito(resultados.get(0));
            listResultados.getItems().clear();
            txtBuscarProducto.clear();
        } else if (resultados.isEmpty()) {
            log("⚠ Sin resultados para: " + texto);
        }
    }

    @FXML
    public void handleAgregarProducto() {
        ProductoInventario seleccionado = listResultados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAviso("Seleccione un producto de la lista.");
            return;
        }
        agregarAlCarrito(seleccionado);
    }

    private void agregarAlCarrito(ProductoInventario producto) {
        for (ItemCarrito item : carrito) {
            if (item.getCodigo().equals(producto.getCodigo())) {
                item.setCantidad(item.getCantidad() + 1);
                tblCarrito.refresh();
                recalcularTotales();
                log("➕ " + producto.getNombre() + " x" + item.getCantidad());
                return;
            }
        }

        ItemCarrito nuevo = new ItemCarrito(
            producto.getCodigo(),
            producto.getNombre(),
            producto.getPrecio(),
            producto.getAlicuota(),
            1
        );
        carrito.add(nuevo);
        recalcularTotales();
        log("➕ " + producto.getNombre());

        listResultados.getItems().clear();
        txtBuscarProducto.clear();
        txtBuscarProducto.requestFocus();
    }

    // ═══════════════════════════════════════════════════════════
    //  CARRITO
    // ═══════════════════════════════════════════════════════════
    @FXML
    public void handleEditarCantidad() {
        ItemCarrito seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAviso("Seleccione un item del carrito.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(seleccionado.getCantidad()));
        dialog.setTitle("Editar cantidad");
        dialog.setHeaderText("Producto: " + seleccionado.getNombre());
        dialog.setContentText("Nueva cantidad:");

        dialog.showAndWait().ifPresent(valor -> {
            try {
                int nuevaCant = Integer.parseInt(valor.trim());
                if (nuevaCant > 0) {
                    seleccionado.setCantidad(nuevaCant);
                    tblCarrito.refresh();
                    recalcularTotales();
                }
            } catch (NumberFormatException e) {
                mostrarAviso("Cantidad inválida.");
            }
        });
    }

    @FXML
    public void handleQuitarItem() {
        ItemCarrito seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAviso("Seleccione un item del carrito.");
            return;
        }
        carrito.remove(seleccionado);
        recalcularTotales();
        log("🗑️ Quitado: " + seleccionado.getNombre());
    }

    @FXML
    public void handleVaciarCarrito() {
        if (carrito.isEmpty()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Vaciar carrito");
        confirm.setHeaderText("¿Está seguro que desea vaciar el carrito?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                carrito.clear();
                recalcularTotales();
                log("🧹 Carrito vaciado");
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    //  PAGOS
    // ═══════════════════════════════════════════════════════════
    @FXML
    public void handleAgregarPago() {
        String metodo = cmbMetodoPago.getSelectionModel().getSelectedItem();
        if (metodo == null) {
            mostrarAviso("Seleccione un método de pago.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(txtMontoPago.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            mostrarAviso("Monto inválido.");
            return;
        }

        if (monto <= 0) {
            mostrarAviso("El monto debe ser mayor a 0.");
            return;
        }

        pagos.add(new PagoRealizado(metodo, monto, ""));
        txtMontoPago.setText("0.00");
        recalcularTotalPagado();
        log("💳 Pago: " + metodo + " " + formatoMoneda.format(monto));
    }

    @FXML
    public void handleQuitarPago() {
        PagoRealizado seleccionado = tblPagos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAviso("Seleccione un pago de la lista.");
            return;
        }
        pagos.remove(seleccionado);
        recalcularTotalPagado();
        log("🗑️ Pago quitado");
    }

    // ═══════════════════════════════════════════════════════════
    //  CÁLCULOS
    // ═══════════════════════════════════════════════════════════
    private void recalcularTotales() {
        double subtotal = 0, baseImponible = 0, baseExento = 0;

        for (ItemCarrito item : carrito) {
            double linea = item.getSubtotal();
            subtotal += linea;
            if ("G".equals(item.getAlicuota())) {
                baseImponible += linea;
            } else {
                baseExento += linea;
            }
        }

        double iva = baseImponible * 0.16;
        double igtf = 0;
        double total = subtotal + iva + igtf;

        lblSubtotal.setText(formatoMoneda.format(subtotal));
        lblBaseImponible.setText(formatoMoneda.format(baseImponible));
        lblBaseExento.setText(formatoMoneda.format(baseExento));
        lblIVA.setText(formatoMoneda.format(iva));
        lblIGTF.setText(formatoMoneda.format(igtf));
        lblTotal.setText(formatoMoneda.format(total));
        lblItemsCarrito.setText(carrito.size() + " items");
        if (lblContadorItems != null) lblContadorItems.setText(carrito.size() + " items");
        if (lblContadorPagos != null) lblContadorPagos.setText(pagos.size() + " pagos");

        recalcularTotalPagado();
    }

    private void recalcularTotalPagado() {
        double pagado = pagos.stream().mapToDouble(PagoRealizado::getMonto).sum();
        lblTotalPagado.setText(formatoMoneda.format(pagado));

        double total = getTotalFactura();
        double cambio = pagado - total;
        lblCambio.setText(formatoMoneda.format(Math.max(0, cambio)));

        if (lblContadorPagos != null) lblContadorPagos.setText(pagos.size() + " pagos");
    }

    private double getTotalFactura() {
        double subtotal = 0, baseImponible = 0;
        for (ItemCarrito item : carrito) {
            subtotal += item.getSubtotal();
            if ("G".equals(item.getAlicuota())) {
                baseImponible += item.getSubtotal();
            }
        }
        return subtotal + (baseImponible * 0.16);
    }

    // ═══════════════════════════════════════════════════════════
    //  FACTURAR
    // ═══════════════════════════════════════════════════════════
    @FXML
    public void handleFacturar() {
        if (carrito.isEmpty()) {
            mostrarAviso("El carrito está vacío.");
            return;
        }

        double total = getTotalFactura();
        double pagado = pagos.stream().mapToDouble(PagoRealizado::getMonto).sum();

        if (pagado < total) {
            mostrarAviso("El monto pagado (" + formatoMoneda.format(pagado)
                + ") es menor al total (" + formatoMoneda.format(total) + ")");
            return;
        }

        try {
            log("⏳ Procesando facturación...");

            FacturaRequestDTO request = construirRequest();
            CobolConnectorFile cobol = new CobolConnectorFile();
            FacturaResponseDTO response = cobol.procesarFactura(request);

            if (!response.esExitosa()) {
                log("❌ Error del COBOL: " + response.getMensaje());
                mostrarAviso("Error del COBOL: " + response.getMensaje());
                return;
            }

            log("✅ Factura generada: " + response.getNumeroFactura());

            DatosFactura datosPdf = construirDatosPdf(response);
            MetodoFacturacion pdf = new MetodoFacturacionPDF();
            ResultadoFactura resultadoPdf = pdf.emitir(datosPdf);

            log("✅ PDF: " + resultadoPdf.getArchivoGenerado().getFileName());

            mostrarAviso("Factura " + response.getNumeroFactura() + " emitida correctamente.\n\n"
                + "Nro Control: " + response.getNumeroControl() + "\n"
                + "TOTAL: " + formatoMoneda.format(response.getTotal()) + "\n\n"
                + "PDF: " + resultadoPdf.getArchivoGenerado());

            limpiar();

        } catch (Exception e) {
            log("❌ Error: " + e.getMessage());
            e.printStackTrace();
            mostrarAviso("Error: " + e.getMessage());
        }
    }

    private FacturaRequestDTO construirRequest() {
        FacturaRequestDTO request = new FacturaRequestDTO();

        request.setNombreSucursal("INVERSIONES COLD 2024, C.A.");
        request.setRifSucursal("J-505366220");
        request.setDireccionSucursal("CC. AA. Libertador, Nivel PB, Local 33");
        request.setCiudadSucursal("Caracas");
        request.setEstadoSucursal("Distrito Capital");
        request.setZonaPostal("1053");

        request.setCajeroCodigo(cajeroCodigo);
        request.setCajeroNombre(cajeroNombre);
        request.setCajaNumero(cajaNumero);

        request.setRifCliente(clienteRif);
        request.setRazonSocial(clienteNombre);

        request.setFormaPago("CO");
        request.setFechaEmision(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        request.setHoraEmision(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
        request.setMontoPagado(pagos.stream().mapToDouble(PagoRealizado::getMonto).sum());

        for (ItemCarrito item : carrito) {
            request.addItem(new ItemDTO(
                item.getCodigo(),
                item.getNombre(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getAlicuota()
            ));
        }

        return request;
    }

    private DatosFactura construirDatosPdf(FacturaResponseDTO response) {
        DatosFactura datos = new DatosFactura();

        datos.setEmisorNombre("INVERSIONES COLD 2024, C.A.");
        datos.setEmisorRif("J-505366220");
        datos.setEmisorDireccion("CC. AA. Libertador, Nivel PB, Local 33, Urb. La Florida");
        datos.setEmisorCiudad("Caracas");
        datos.setEmisorEstado("Distrito Capital");

        datos.setClienteRif(clienteRif);
        datos.setClienteRazonSocial(clienteNombre);

        datos.setNumeroFactura(String.valueOf(response.getNumeroFactura()));
        datos.setNumeroControl(response.getNumeroControl());
        datos.setFechaEmision(LocalDateTime.now());
        datos.setCajeroNombre(cajeroNombre);
        datos.setCajaNumero(cajaNumero);

        datos.setBaseImponible(response.getBaseImponible());
        datos.setBaseExento(response.getBaseExento());
        datos.setIva(response.getIva());
        datos.setIgtf(response.getIgtf());
        datos.setTotal(response.getTotal());

        datos.setFormaPago("CO");
        datos.setMontoPagado(pagos.stream().mapToDouble(PagoRealizado::getMonto).sum());

        for (ItemCarrito item : carrito) {
            datos.addItem(new DatosFactura.ItemFactura(
                item.getCodigo(),
                item.getNombre(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getAlicuota(),
                item.getSubtotal()
            ));
        }

        return datos;
    }

    // ═══════════════════════════════════════════════════════════
    //  ACCIONES
    // ═══════════════════════════════════════════════════════════
    @FXML
    public void handleCancelar() {
        if (!carrito.isEmpty() || !pagos.isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cancelar venta");
            confirm.setHeaderText("¿Cancelar la venta actual?");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) limpiar();
            });
        } else {
            limpiar();
        }
    }

    @FXML
    public void handleCambiarCliente() {
        TextInputDialog dialog = new TextInputDialog(clienteRif);
        dialog.setTitle("Cambiar Cliente");
        dialog.setHeaderText("Ingrese el RIF/Cédula del cliente");
        dialog.setContentText("RIF/Cédula:");

        dialog.showAndWait().ifPresent(rif -> {
            String nuevoRif = rif.trim().toUpperCase();
            if (nuevoRif.isEmpty()) return;

            this.clienteRif = nuevoRif;
            this.clienteNombre = "CLIENTE " + nuevoRif;

            mostrarClienteEnPantalla();
            log("👤 Cliente: " + nuevoRif);
        });
    }

    @FXML public void handleDevolucion() {
        mostrarAviso("Módulo de devoluciones por implementar.");
    }

    @FXML public void handleArqueo() {
        mostrarAviso("Módulo de arqueo por implementar.");
    }

    private void limpiar() {
        carrito.clear();
        pagos.clear();
        txtMontoPago.setText("0.00");
        cmbMetodoPago.getSelectionModel().selectFirst();
        recalcularTotales();
        log("🟢 Listo para facturar");
        txtBuscarProducto.requestFocus();
    }

    private void mostrarAviso(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ═══════════════════════════════════════════════════════════
    //  CLASES INTERNAS
    // ═══════════════════════════════════════════════════════════
    public static class ItemCarrito {
        private String codigo;
        private String nombre;
        private double precioUnitario;
        private String alicuota;
        private int cantidad;

        public ItemCarrito(String codigo, String nombre, double precioUnitario,
                           String alicuota, int cantidad) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.precioUnitario = precioUnitario;
            this.alicuota = alicuota;
            this.cantidad = cantidad;
        }

        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public double getPrecioUnitario() { return precioUnitario; }
        public String getAlicuota() { return alicuota; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getSubtotal() { return precioUnitario * cantidad; }
    }

    public static class PagoRealizado {
        private String metodo;
        private double monto;
        private String referencia;

        public PagoRealizado(String metodo, double monto, String referencia) {
            this.metodo = metodo;
            this.monto = monto;
            this.referencia = referencia;
        }

        public String getMetodo() { return metodo; }
        public void setMetodo(String metodo) { this.metodo = metodo; }
        public double getMonto() { return monto; }
        public void setMonto(double monto) { this.monto = monto; }
        public String getReferencia() { return referencia; }
        public void setReferencia(String referencia) { this.referencia = referencia; }
    }
}