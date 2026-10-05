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
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import magno.com.ve.facturacion.domain.enums.TipoPago;
import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.ItemFactura;
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

    @FXML private Label lblCajero;
    @FXML private Label lblHora;
    @FXML private Label lblFecha;
    @FXML private Label lblTasa;

    @FXML private ListView<ItemCarrito> lstCarrito;
    @FXML private Label lblItemsCount;

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

    @FXML private Button btnMasOpciones;

    private final ObservableList<ItemCarrito> items = FXCollections.observableArrayList();
    private static final double IVA = 0.16;

    private final TasaCambioService tasaService = new TasaCambioService();
    private double tasaActual = 0.0;
    private RelojTiempoReal reloj;

    private static final String CSS_PATH =
            "/magno/com/ve/facturacion/css/magno.css";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reloj = new RelojTiempoReal(lblHora, lblFecha);

        lstCarrito.setItems(items);
        items.addListener((javafx.collections.ListChangeListener<ItemCarrito>) c ->
                lblItemsCount.setText("(" + items.size() + ")"));
        lstCarrito.setCellFactory(lv -> new CeldaItemCarrito());

        cargarTasaCambio();
        Timeline refresco = new Timeline(new KeyFrame(Duration.minutes(30),
                e -> cargarTasaCambio()));
        refresco.setCycleCount(Animation.INDEFINITE);
        refresco.play();

        recalcularTotales();
    }

    // ================== UTILIDADES ==================

    private void estilizarDialogo(Dialog<?> dialog) {
        if (dialog == null) return;
        DialogPane pane = dialog.getDialogPane();

        try {
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl != null) pane.getStylesheets().add(cssUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar CSS al diálogo: " + e.getMessage());
        }

        pane.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-border-color: #e6e8eb;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;"
        );

        Node header = pane.lookup(".header-panel");
        if (header instanceof Region region) {
            region.setStyle(
                    "-fx-background-color: #f8fafc;" +
                    "-fx-border-color: transparent transparent #e6e8eb transparent;" +
                    "-fx-border-width: 0 0 1 0;"
            );
        }

        for (ButtonType bt : pane.getButtonTypes()) {
            Node btnNode = pane.lookupButton(bt);
            if (btnNode instanceof Button b) {
                boolean esDefault = (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE
                        || bt.getButtonData() == ButtonBar.ButtonData.YES);
                if (esDefault) {
                    b.setStyle("-fx-background-color: #16a34a;-fx-text-fill: white;-fx-font-weight: bold;-fx-background-radius: 8;-fx-border-radius: 8;-fx-padding: 8 20 8 20;-fx-cursor: hand;");
                } else {
                    b.setStyle("-fx-background-color: #ffffff;-fx-text-fill: #4b5563;-fx-border-color: #e6e8eb;-fx-border-radius: 8;-fx-background-radius: 8;-fx-padding: 8 20 8 20;-fx-cursor: hand;");
                }
            }
        }

        if (lblCajero != null && lblCajero.getScene() != null) {
            Stage owner = (Stage) lblCajero.getScene().getWindow();
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
    }

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
                    if (lblTasa != null) lblTasa.setText(String.format("Tasa BCV: Bs. %.2f", tasaActual));
                    recalcularTotales();
                });
            } else cargarTasaParalelo();
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
                    if (lblTasa != null) lblTasa.setText(String.format("Tasa paralelo: Bs. %.2f", tasaActual));
                    recalcularTotales();
                } else if (lblTasa != null) lblTasa.setText("⚠️ Tasa no disponible");
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
        if (r.isPresent() && r.get() == ButtonType.OK) cerrarTurno();
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

    // ============================================================
    // AGREGAR PRODUCTO — abre el diálogo de búsqueda y pasa items al carrito
    // ============================================================
    @FXML
    private void handleAgregarProducto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/magno/com/ve/facturacion/view/panel-productos.fxml"));
            Parent root = loader.load();

            PanelProductosController ctrl = loader.getController();

            Stage dialog = new Stage();
            dialog.setTitle("Productos");
            dialog.initModality(Modality.APPLICATION_MODAL);
            if (lblCajero != null && lblCajero.getScene() != null) {
                dialog.initOwner(lblCajero.getScene().getWindow());
            }

            Scene scene = new Scene(root, 800, 600);
            try {
                URL css = getClass().getResource(CSS_PATH);
                if (css != null) scene.getStylesheets().add(css.toExternalForm());
            } catch (Exception ignored) {}

            dialog.setScene(scene);
            dialog.setMinWidth(700);
            dialog.setMinHeight(500);
            dialog.showAndWait();

            // Pasar los items del diálogo al carrito principal
            for (ItemFactura itemDialog : ctrl.getItems()) {
                String nombre = itemDialog.getProducto().getNombre();
                String id     = itemDialog.getProducto().getId();
                double precio = itemDialog.getProducto().getPrecio();
                int cant      = itemDialog.getCantidad();

                Optional<ItemCarrito> existente = items.stream()
                    .filter(x -> x.getNombre().equals(nombre))
                    .findFirst();

                if (existente.isPresent()) {
                    ItemCarrito prev = existente.get();
                    items.remove(prev);
                    items.add(new ItemCarrito(
                        prev.getNombre(),
                        prev.getDetalle(),
                        prev.getCantidad() + cant,
                        prev.getPrecio()
                    ));
                } else {
                    items.add(new ItemCarrito(nombre, id, cant, precio));
                }
            }
            recalcularTotales();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleCobroEfectivo()   { abrirDialogoPago(TipoPago.EFECTIVO); }
    @FXML private void handleCobroDebito()     { abrirDialogoPago(TipoPago.TARJETA_DEBITO); }
    @FXML private void handleCobroQR()         { abrirDialogoPago(TipoPago.QR); }
    @FXML private void handleVerMasMetodos()   { abrirDialogoPago(null); }

    @FXML private void handleNuevaVenta()      { items.clear(); recalcularTotales(); }
    @FXML private void handleCancelarVenta()   { items.clear(); recalcularTotales(); }
    @FXML private void handleAbrirCaja()       { /* TODO */ }

    @FXML
    private void handleCobrarFacturar() {
        abrirDialogoPago(null);
    }

    // ================== NOTA DE CRÉDITO ==================

    @FXML
    private void handleNotaCredito() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Nota de Crédito");
        confirm.setHeaderText("🧾 Emitir Nota de Crédito");
        confirm.setContentText("Esta acción anulará la última factura emitida.\n\n¿Desea continuar?");
        estilizarDialogo(confirm);
        Optional<ButtonType> r = confirm.showAndWait();
        recuperarFoco();
        if (r.isPresent() && r.get() == ButtonType.OK) emitirNotaCredito();
    }

    private void emitirNotaCredito() {
        try {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Nota de Crédito Emitida");
            info.setHeaderText("✅ Nota de Crédito generada");
            info.setContentText(String.format(
                    "Fecha: %s%nHora: %s%nCajero: %s%n%nLa nota de crédito fue registrada correctamente.%nSe enviará al servidor COBOL en la próxima sincronización.",
                    lblFecha != null ? lblFecha.getText() : "—",
                    lblHora  != null ? lblHora.getText()  : "—",
                    lblCajero != null ? lblCajero.getText() : "—"));
            estilizarDialogo(info);
            info.showAndWait();
            recuperarFoco();
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR,
                    "Error al emitir Nota de Crédito: " + e.getMessage());
            estilizarDialogo(err);
            err.showAndWait();
            recuperarFoco();
        }
    }

    // ================== MENÚ "MÁS OPCIONES" ==================

    @FXML
    private void handleMasOpciones() {
        ContextMenu menu = new ContextMenu();
        menu.setStyle("-fx-background-color: #ffffff;-fx-border-color: #e6e8eb;-fx-border-width: 1;-fx-background-radius: 10;-fx-border-radius: 10;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4);-fx-padding: 6 0 6 0;");

        Label lblHeader = new Label("⚙️  HERRAMIENTAS Y AJUSTES");
        lblHeader.setStyle("-fx-font-size: 11px;-fx-font-weight: bold;-fx-text-fill: #8a94a6;-fx-padding: 8 16 8 16;");
        CustomMenuItem headerItem = new CustomMenuItem(lblHeader);
        headerItem.setHideOnClick(false);
        headerItem.setStyle("-fx-background-color: #f8fafc;");
        menu.getItems().add(headerItem);
        menu.getItems().add(new SeparatorMenuItem());

        menu.getItems().add(crearItemMenu("📊", "Reporte de ventas del día", "Ver todas las ventas", this::mostrarReporteVentas));
        menu.getItems().add(crearItemMenu("💰", "Arqueo de caja", "Contar efectivo actual", this::mostrarArqueoCaja));
        menu.getItems().add(crearItemMenu("📦", "Consultar inventario", "Buscar productos", this::mostrarInventario));
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(crearItemMenu("📄", "Reporte X (Corte parcial)", "Ver ventas sin cerrar turno", this::mostrarReporteX));
        menu.getItems().add(crearItemMenu("🔒", "Reporte Z (Cierre de turno)", "Cerrar caja y generar reporte", this::mostrarReporteZ));
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(crearItemMenu("🖨️", "Configurar impresora", "Puerto y modelo", this::mostrarConfigImpresora));
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(crearItemMenu("⚙️", "Configuración general", "Parámetros del sistema", this::mostrarConfigGeneral));
        menu.getItems().add(crearItemMenu("❓", "Ayuda / Acerca de", "MAGNO POS v1.0.0", this::mostrarAcercaDe));

        if (btnMasOpciones != null) menu.show(btnMasOpciones, Side.TOP, 0, -5);
    }

    private CustomMenuItem crearItemMenu(String icono, String titulo, String subtitulo, Runnable accion) {
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 18px;");
        lblIcono.setMinWidth(30);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a2b4c;");

        Label lblSub = new Label(subtitulo);
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #8a94a6;");

        VBox textos = new VBox(1, lblTitulo, lblSub);
        HBox contenido = new HBox(12, lblIcono, textos);
        contenido.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        contenido.setPrefWidth(280);
        contenido.setStyle("-fx-padding: 8 20 8 16; -fx-cursor: hand;");

        contenido.setOnMouseEntered(e -> contenido.setStyle("-fx-padding: 8 20 8 16; -fx-cursor: hand;-fx-background-color: #f5f7fa; -fx-background-radius: 6;"));
        contenido.setOnMouseExited(e -> contenido.setStyle("-fx-padding: 8 20 8 16; -fx-cursor: hand;"));

        CustomMenuItem item = new CustomMenuItem(contenido);
        item.setOnAction(e -> accion.run());
        return item;
    }

    // ================== ACCIONES DE MENÚ ==================

    private void mostrarReporteVentas() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reporte de Ventas");
        alert.setHeaderText("📊 Reporte del día");
        alert.setContentText("Fecha: " + lblFecha.getText() + "\nVentas totales: 0\nMonto USD: $ 0,00\nMonto Bs.: Bs. 0,00\n\n(Función en desarrollo)");
        estilizarDialogo(alert);
        alert.showAndWait();
        recuperarFoco();
    }

    private void mostrarArqueoCaja() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Arqueo de Caja");
        alert.setHeaderText("💰 Conteo de efectivo");
        alert.setContentText("Efectivo inicial: $ 0,00\nVentas en efectivo: $ 0,00\nEfectivo esperado: $ 0,00\n\n(Función en desarrollo)");
        estilizarDialogo(alert);
        alert.showAndWait();
        recuperarFoco();
    }

    private void mostrarInventario() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventario");
        alert.setHeaderText("📦 Consulta de inventario");
        alert.setContentText("Productos en stock: 0\nProductos agotados: 0\nProductos por agotarse: 0\n\n(Función en desarrollo)");
        estilizarDialogo(alert);
        alert.showAndWait();
        recuperarFoco();
    }

    private void mostrarConfigImpresora() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configurar Impresora");
        alert.setHeaderText("🖨️ Impresora de tickets");
        alert.setContentText("Puerto: /dev/usb/lp0\nModelo: Epson TM-T20\nEstado: No conectada\n\n(Función en desarrollo)");
        estilizarDialogo(alert);
        alert.showAndWait();
        recuperarFoco();
    }

    private void mostrarConfigGeneral() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuración General");
        alert.setHeaderText("⚙️ Parámetros del sistema");
        alert.setContentText("IVA: 16%\nMoneda base: USD\nTasa BCV: " + (tasaActual > 0 ? String.format("Bs. %.2f", tasaActual) : "no disponible") + "\nServidor TCP: puerto 9100\n\n(Función en desarrollo)");
        estilizarDialogo(alert);
        alert.showAndWait();
        recuperarFoco();
    }

    private void mostrarAcercaDe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("ℹ️ MAGNO - Punto de Venta");
        alert.setContentText("Versión: 1.0.0\nMódulo: Facturación\nCliente: " + (lblCajero != null ? lblCajero.getText() : "—") + "\nFecha: " + lblFecha.getText() + "\n\n© 2026 Magno C.A.\nTodos los derechos reservados.");
        estilizarDialogo(alert);
        alert.showAndWait();
        recuperarFoco();
    }

    private void mostrarReporteX() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reporte X");
        confirm.setHeaderText("📄 Reporte X — Corte parcial");
        confirm.setContentText("Este reporte NO cierra el turno.\nSolo muestra las ventas hasta el momento.\n\n¿Desea generarlo?");
        estilizarDialogo(confirm);
        Optional<ButtonType> r = confirm.showAndWait();
        recuperarFoco();
        if (r.isPresent() && r.get() == ButtonType.OK) emitirReporteX();
    }

    private void emitirReporteX() {
        try {
            double totalVentas = calcularTotalUsd();
            double totalBs     = totalVentas * tasaActual;
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Reporte X — Generado");
            info.setHeaderText("📄 Reporte X (Corte parcial)");
            info.setContentText(String.format(
                    "═══════════════════════════════════%n  REPORTE X — CORTE PARCIAL%n═══════════════════════════════════%nCajero: %s%nCaja: 001%nFecha: %s%nHora: %s%nTasa BCV: Bs. %.2f%n───────────────────────────────────%nVentas del turno:%n  Cantidad: 0%n  Total USD: $ 0,00%n  Total Bs.: Bs. 0,00%n───────────────────────────────────%nÚltima venta:%n  Total USD: $ %.2f%n  Total Bs.: Bs. %.2f%n───────────────────────────────────%n  ⚠️  El turno SIGUE ABIERTO%n═══════════════════════════════════%n",
                    lblCajero != null ? lblCajero.getText() : "—",
                    lblFecha  != null ? lblFecha.getText()  : "—",
                    lblHora   != null ? lblHora.getText()   : "—",
                    tasaActual, totalVentas, totalBs));
            estilizarDialogo(info);
            info.showAndWait();
            recuperarFoco();
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR, "Error al generar Reporte X: " + e.getMessage());
            estilizarDialogo(err); err.showAndWait(); recuperarFoco();
        }
    }

    private void mostrarReporteZ() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reporte Z");
        confirm.setHeaderText("🔒 Reporte Z — Cierre de turno");
        confirm.setContentText("⚠️  ADVERTENCIA: Este reporte CIERRA el turno.\n\n• Se contabilizarán todas las ventas del día\n• Se resetearán los contadores de la caja\n• El cajero deberá iniciar sesión de nuevo\n\n¿Está seguro de cerrar el turno?");
        estilizarDialogo(confirm);
        Optional<ButtonType> r = confirm.showAndWait();
        recuperarFoco();
        if (r.isPresent() && r.get() == ButtonType.OK) emitirReporteZ();
    }

    private void emitirReporteZ() {
        try {
            double totalVentas = calcularTotalUsd();
            double totalBs     = totalVentas * tasaActual;
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Reporte Z — Turno Cerrado");
            info.setHeaderText("🔒 Reporte Z (Cierre de turno)");
            info.setContentText(String.format(
                    "═══════════════════════════════════%n  REPORTE Z — CIERRE DE TURNO%n═══════════════════════════════════%nCajero: %s%nCaja: 001%nFecha: %s%nHora: %s%nTasa BCV: Bs. %.2f%n───────────────────────────────────%nTotales del turno:%n  Ventas realizadas: 0%n  Total USD: $ 0,00%n  Total Bs.: Bs. 0,00%n───────────────────────────────────%nÚltima venta registrada:%n  Total USD: $ %.2f%n  Total Bs.: Bs. %.2f%n───────────────────────────────────%n  ✅  TURNO CERRADO CORRECTAMENTE%n  El cajero será redirigido al login.%n═══════════════════════════════════%n",
                    lblCajero != null ? lblCajero.getText() : "—",
                    lblFecha  != null ? lblFecha.getText()  : "—",
                    lblHora   != null ? lblHora.getText()   : "—",
                    tasaActual, totalVentas, totalBs));
            estilizarDialogo(info);
            info.showAndWait();
            recuperarFoco();
            cerrarTurno();
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR, "Error al generar Reporte Z: " + e.getMessage());
            estilizarDialogo(err); err.showAndWait(); recuperarFoco();
        }
    }

    // ================== DIÁLOGO DE PAGO ==================

    private void abrirDialogoPago(TipoPago tipoInicial) {
        if (items.isEmpty()) {
            Alert warn = new Alert(Alert.AlertType.WARNING, "No hay productos en el carrito.");
            estilizarDialogo(warn); warn.showAndWait(); recuperarFoco(); return;
        }
        if (tasaActual <= 0) {
            Alert warn = new Alert(Alert.AlertType.WARNING, "La tasa de cambio aún no está disponible. Espere unos segundos.");
            estilizarDialogo(warn); warn.showAndWait(); recuperarFoco(); return;
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
                if (cssUrl != null) dialogScene.getStylesheets().add(cssUrl.toExternalForm());
            } catch (Exception e) {
                System.err.println("No se pudo aplicar CSS al diálogo de pago: " + e.getMessage());
            }
            dialog.setScene(dialogScene);
            dialog.showAndWait();
            recuperarFoco();

            if (ctrl.isConfirmado()) procesarPago(ctrl.getResultado());
        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR, "Error al abrir diálogo: " + e.getMessage());
            estilizarDialogo(err); err.showAndWait(); recuperarFoco();
        }
    }

    private void procesarPago(Pago pago) {
        try {
            Cliente cliente = pago.getCliente() != null ? pago.getCliente() : new Cliente();
            if (cliente.getCedula() == null || cliente.getCedula().isBlank()) {
                cliente.setCedula("V-00000000");
                cliente.setNombres("Cliente Mostrador");
            }

            List<Producto> productos = new ArrayList<>();
            for (ItemCarrito it : items) {
                Producto p = new Producto();
                p.setId(it.getNombre());
                p.setNombre(it.getNombre());
                p.setPrecio(it.getPrecio());
                p.setStock((int) it.getCantidad());
                productos.add(p);
            }

            FacturacionService servicio = new FacturacionService();
            String recibo = servicio.procesarPedido(cliente, productos);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Factura Procesada");
            alert.setHeaderText("Pago con " + pago.getTipo().getTitulo());
            alert.setContentText(String.format(
                    "Total USD: $ %.2f%nTotal Bs.:  Bs. %.2f%nTasa aplicada: %.2f%nForma de pago: %s%n%n%s",
                    pago.getMontoUsd(), pago.getMontoBs(), pago.getTasaAplicada(),
                    pago.getFormaPagoFactura(), recibo));
            estilizarDialogo(alert);
            alert.showAndWait();
            recuperarFoco();
            handleNuevaVenta();
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR, "Error al procesar pago: " + e.getMessage());
            estilizarDialogo(err); err.showAndWait(); recuperarFoco();
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