package magno.com.ve.facturacion.ui.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import magno.com.ve.facturacion.domain.enums.TipoPago;
import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Pago;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.service.FacturacionService;
import magno.com.ve.facturacion.service.TasaCambioService;
import magno.com.ve.facturacion.util.RelojTiempoReal;     

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PanelFacturacionController implements Initializable {

    // Header
    @FXML private Label lblCajero;
    @FXML private Label lblHora;
    @FXML private Label lblFecha;
    @FXML private Label lblTasa;

    // Carrito
    @FXML private ListView<ItemCarrito> lstCarrito;
    @FXML private Label lblItemsCount;

    // Totales
    @FXML private Label lblTotalGrande;
    @FXML private Label lblTotalBsGrande;
    @FXML private Label lblSubtotal;
    @FXML private Label lblSubtotalBs;
    @FXML private Label lblMontoIva;
    @FXML private Label lblMontoIvaBs;
    @FXML private Label lblDescuento;
    @FXML private Label lblDescuentoBs;
    @FXML private Label lblTotalUsd;
    @FXML private Label lblTotalBs;

    private final ObservableList<ItemCarrito> items = FXCollections.observableArrayList();
    private static final double IVA = 0.16;

    private final TasaCambioService tasaService = new TasaCambioService();
    private double tasaActual = 0.0;
    private RelojTiempoReal reloj;

    // ✅ Ruta al CSS (usada por todos los diálogos)
    private static final String CSS_PATH =
            "/magno/com/ve/facturacion/css/magno.css";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Reloj en tiempo real
        reloj = new RelojTiempoReal(lblHora, lblFecha);

        // 2. Lista del carrito
        lstCarrito.setItems(items);
        items.addListener((javafx.collections.ListChangeListener<ItemCarrito>) c ->
                lblItemsCount.setText("(" + items.size() + ")"));
        lstCarrito.setCellFactory(lv -> new CeldaItemCarrito());

        // 3. Tasa de cambio (async)
        cargarTasaCambio();
        Timeline refresco = new Timeline(new KeyFrame(Duration.minutes(30),
                e -> cargarTasaCambio()));
        refresco.setCycleCount(Animation.INDEFINITE);
        refresco.play();

        recalcularTotales();
    }

    // ================== UTILIDADES ==================

    /**
     * ✅ Aplica estilo CLARO a cualquier diálogo (Alert o Dialog),
     *    forzando el fondo blanco con estilos inline (mayor prioridad
     *    que AtlantaFX), y le asigna owner + modalidad.
     */
    private void estilizarDialogo(Dialog<?> dialog) {
        if (dialog == null) return;

        DialogPane pane = dialog.getDialogPane();

        // 1) Aplicar CSS externo
        try {
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl != null) {
                pane.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("No se pudo aplicar CSS al diálogo: " + e.getMessage());
        }

        // 2) ✅ Forzar fondo BLANCO inline (AtlantaFX gana la cascada CSS,
        //    por eso el Alert sigue gris. El inline SIEMPRE gana).
        pane.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-border-color: #e6e8eb;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;"
        );

        // 3) ✅ Forzar header claro (donde va "Cerrar Turno" / "Advertencia")
        Node header = pane.lookup(".header-panel");
        if (header instanceof Region region) {
            region.setStyle(
                    "-fx-background-color: #f8fafc;" +
                    "-fx-border-color: transparent transparent #e6e8eb transparent;" +
                    "-fx-border-width: 0 0 1 0;"
            );
        }

        // 4) ✅ Forzar botones del diálogo (verde para "Aceptar", blanco para "Cancelar")
        for (ButtonType bt : pane.getButtonTypes()) {
            Node btnNode = pane.lookupButton(bt);
            if (btnNode instanceof Button b) {
                boolean esDefault = (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE
                        || bt.getButtonData() == ButtonBar.ButtonData.YES);

                if (esDefault) {
                    b.setStyle(
                            "-fx-background-color: #16a34a;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-radius: 8;" +
                            "-fx-padding: 8 20 8 20;" +
                            "-fx-cursor: hand;"
                    );
                } else {
                    b.setStyle(
                            "-fx-background-color: #ffffff;" +
                            "-fx-text-fill: #4b5563;" +
                            "-fx-border-color: #e6e8eb;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 8 20 8 20;" +
                            "-fx-cursor: hand;"
                    );
                }
            }
        }

        // 5) Owner + modalidad
        if (lblCajero != null && lblCajero.getScene() != null) {
            Stage owner = (Stage) lblCajero.getScene().getWindow();
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
    }

    /**
     * ✅ Recupera el foco del Stage principal después de cerrar
     *    un diálogo. Esto evita que GTK redibuje la ventana
     *    con un layout "deforme".
     */
    private void recuperarFoco() {
        Platform.runLater(() -> {
            if (lblCajero == null || lblCajero.getScene() == null) return;
            Stage owner = (Stage) lblCajero.getScene().getWindow();
            if (owner != null) {
                owner.requestFocus();
                Parent root = owner.getScene().getRoot();
                if (root != null) root.requestLayout();
            }
        });
    }

    // ================== TASA DE CAMBIO ==================

    private void cargarTasaCambio() {
        if (lblTasa != null) lblTasa.setText("Tasa: consultando...");

        Task<Double> task = new Task<>() {
            @Override protected Double call() throws Exception {
                return tasaService.obtenerTasaOficial();
            }
        };
        task.setOnSucceeded(e -> {
            double t = task.getValue();
            if (t > 0) {
                tasaActual = t;
                Platform.runLater(() -> {
                    if (lblTasa != null) {
                        lblTasa.setText(String.format("Tasa BCV: Bs. %.2f", tasaActual));
                    }
                    recalcularTotales();
                });
            } else {
                cargarTasaParalelo();
            }
        });
        task.setOnFailed(e -> cargarTasaParalelo());
        new Thread(task, "tasa-bcv").start();
    }

    private void cargarTasaParalelo() {
        Task<Double> task = new Task<>() {
            @Override protected Double call() throws Exception {
                return tasaService.obtenerTasaParalelo();
            }
        };
        task.setOnSucceeded(e -> {
            tasaActual = task.getValue();
            Platform.runLater(() -> {
                if (tasaActual > 0) {
                    if (lblTasa != null) {
                        lblTasa.setText(String.format("Tasa paralelo: Bs. %.2f", tasaActual));
                    }
                    recalcularTotales();
                } else {
                    if (lblTasa != null) lblTasa.setText("⚠️ Tasa no disponible");
                }
            });
        });
        task.setOnFailed(e -> Platform.runLater(() -> {
            if (lblTasa != null) lblTasa.setText("⚠️ Tasa no disponible");
        }));
        new Thread(task, "tasa-paralelo").start();
    }

    // ================== ACCIONES ==================

    @FXML
    private void handleCerrarSesion() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar Turno");
        confirm.setHeaderText("¿Cerrar el turno actual?");
        confirm.setContentText("Se generará el reporte de cierre y se reiniciará la caja.");

        estilizarDialogo(confirm);

        Optional<ButtonType> r = confirm.showAndWait();

        recuperarFoco();

        if (r.isPresent() && r.get() == ButtonType.OK) {
            cerrarTurno();
        }
    }

    private void cerrarTurno() {
        try {
            items.clear();
            if (reloj != null) reloj.detener();

            magno.com.ve.facturacion.App.cargarLogin();
            magno.com.ve.facturacion.App.getStagePrincipal().setTitle(
                    magno.com.ve.facturacion.config.AppConfig.NOMBRE_SISTEMA + " | Iniciar Sesión");

        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Error al cerrar turno: " + e.getMessage());
            estilizarDialogo(err);
            err.showAndWait();
            recuperarFoco();
        }
    }

    @FXML private void handleAgregarProducto() { /* TODO */ }

    @FXML private void handleCobroEfectivo()   { abrirDialogoPago(TipoPago.EFECTIVO); }
    @FXML private void handleCobroDebito()     { abrirDialogoPago(TipoPago.TARJETA_DEBITO); }
    @FXML private void handleCobroQR()         { abrirDialogoPago(TipoPago.QR); }
    @FXML private void handleVerMasMetodos()   { abrirDialogoPago(null); }

    @FXML private void handleNuevaVenta()      { items.clear(); recalcularTotales(); }
    @FXML private void handleCancelarVenta()   { items.clear(); recalcularTotales(); }
    @FXML private void handleAbrirCaja()       { /* TODO */ }
    @FXML private void handleImprimirTicket()  { /* TODO */ }
    @FXML private void handleMasOpciones()     { /* TODO */ }

    @FXML
    private void handleCobrarFacturar() {
        abrirDialogoPago(null);
    }

    // ================== DIÁLOGO DE PAGO ==================

    private void abrirDialogoPago(TipoPago tipoInicial) {
        if (items.isEmpty()) {
            Alert warn = new Alert(Alert.AlertType.WARNING,
                    "No hay productos en el carrito.");
            estilizarDialogo(warn);
            warn.showAndWait();
            recuperarFoco();
            return;
        }
        if (tasaActual <= 0) {
            Alert warn = new Alert(Alert.AlertType.WARNING,
                    "La tasa de cambio aún no está disponible. Espere unos segundos.");
            estilizarDialogo(warn);
            warn.showAndWait();
            recuperarFoco();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/magno/com/ve/facturacion/ui/view/metodo-pago-dialog.fxml"));
            Parent root = loader.load();

            MetodoPagoDialogController ctrl = loader.getController();
            double totalUsd = calcularTotalUsd();
            double totalBs = totalUsd * tasaActual;
            ctrl.inicializar(tipoInicial, totalUsd, totalBs, tasaActual);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(lblCajero.getScene().getWindow());
            dialog.setTitle("Registrar Pago");
            dialog.setResizable(false);

            Scene dialogScene = new Scene(root);
            try {
                URL cssUrl = getClass().getResource(CSS_PATH);
                if (cssUrl != null) {
                    dialogScene.getStylesheets().add(cssUrl.toExternalForm());
                }
            } catch (Exception e) {
                System.err.println("No se pudo aplicar CSS al diálogo de pago: " + e.getMessage());
            }
            dialog.setScene(dialogScene);

            dialog.showAndWait();

            recuperarFoco();

            if (ctrl.isConfirmado()) {
                procesarPago(ctrl.getResultado());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Error al abrir diálogo: " + e.getMessage());
            estilizarDialogo(err);
            err.showAndWait();
            recuperarFoco();
        }
    }

    private void procesarPago(Pago pago) {
        try {
            Cliente cliente = pago.getCliente() != null
                    ? pago.getCliente() : new Cliente();
            if (cliente.getCedula() == null || cliente.getCedula().isBlank()) {
                cliente.setCedula("V-00000000");
                cliente.setNombres("Cliente Mostrador");
            }

            List<Producto> productos = new ArrayList<>();
            for (ItemCarrito it : items) {
                Producto p = new Producto();
                p.setNombreProducto(it.getNombre());
                p.setPrecio(it.getPrecio());
                p.setCantidad((int) it.getCantidad());
                productos.add(p);
            }

            FacturacionService servicio = new FacturacionService();
            String recibo = servicio.procesarPedido(cliente, productos);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Factura Procesada");
            alert.setHeaderText("Pago con " + pago.getTipo().getTitulo());
            alert.setContentText(String.format(
                    "Total USD: $ %.2f%n" +
                    "Total Bs.:  Bs. %.2f%n" +
                    "Tasa aplicada: %.2f%n" +
                    "Forma de pago: %s%n%n" +
                    "%s",
                    pago.getMontoUsd(), pago.getMontoBs(),
                    pago.getTasaAplicada(),
                    pago.getFormaPagoFactura(),
                    recibo));
            estilizarDialogo(alert);
            alert.showAndWait();
            recuperarFoco();

            handleNuevaVenta();
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Error al procesar pago: " + e.getMessage());
            estilizarDialogo(err);
            err.showAndWait();
            recuperarFoco();
        }
    }

    // ================== CÁLCULOS ==================

    private double calcularTotalUsd() {
        double subtotal  = items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
        double descuento = subtotal * 0.01;
        double baseIva   = subtotal - descuento;
        return baseIva + baseIva * IVA;
    }

    private void recalcularTotales() {
        double subtotal  = items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
        double descuento = subtotal * 0.01;
        double baseIva   = subtotal - descuento;
        double iva       = baseIva * IVA;
        double total     = baseIva + iva;

        if (lblSubtotal    != null) lblSubtotal.setText(monedaUsd(subtotal));
        if (lblMontoIva    != null) lblMontoIva.setText(monedaUsd(iva));
        if (lblDescuento   != null) lblDescuento.setText("-" + monedaUsd(descuento));
        if (lblTotalGrande != null) lblTotalGrande.setText(monedaUsd(total));
        if (lblTotalUsd    != null) lblTotalUsd.setText(monedaUsd(total));

        if (tasaActual > 0) {
            if (lblSubtotalBs    != null) lblSubtotalBs.setText(monedaBs(subtotal * tasaActual));
            if (lblMontoIvaBs    != null) lblMontoIvaBs.setText(monedaBs(iva * tasaActual));
            if (lblDescuentoBs   != null) lblDescuentoBs.setText("-" + monedaBs(descuento * tasaActual));
            if (lblTotalBsGrande != null) lblTotalBsGrande.setText(monedaBs(total * tasaActual));
            if (lblTotalBs       != null) lblTotalBs.setText(monedaBs(total * tasaActual));
        }
    }

    private static String monedaUsd(double v) { return String.format("$ %,.2f", v); }
    private static String monedaBs(double v)  { return String.format("Bs. %,.2f", v); }

    // ================== DTO ==================

    public static class ItemCarrito {
        private final String nombre;
        private final String detalle;
        private final int cantidad;
        private final double precio;

        public ItemCarrito(String nombre, String detalle, int cantidad, double precio) {
            this.nombre = nombre; this.detalle = detalle;
            this.cantidad = cantidad; this.precio = precio;
        }
        public String getNombre()   { return nombre; }
        public String getDetalle()  { return detalle; }
        public int    getCantidad() { return cantidad; }
        public double getPrecio()   { return precio; }
        public double getSubtotal() { return cantidad * precio; }
    }

    private static class CeldaItemCarrito extends ListCell<ItemCarrito> {
        @Override protected void updateItem(ItemCarrito it, boolean empty) {
            super.updateItem(it, empty);
            if (empty || it == null) { setText(null); setGraphic(null); return; }
            setText(String.format("%s  ×%d   %s%n%s",
                    it.getNombre(), it.getCantidad(),
                    monedaUsd(it.getSubtotal()), it.getDetalle()));
        }
    }
}