package magno.com.ve.facturacion.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PanelFacturacionController implements Initializable {

    // Header
    @FXML private Label lblCajero;
    @FXML private Label lblHora;
    @FXML private Label lblFecha;

    // Carrito
    @FXML private ListView<ItemCarrito> lstCarrito;
    @FXML private Label lblItemsCount;

    // Totales
    @FXML private Label lblTotalGrande;
    @FXML private Label lblSubtotal;
    @FXML private Label lblMontoIva;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotalUsd;

    private final ObservableList<ItemCarrito> items = FXCollections.observableArrayList();
    private static final double IVA = 0.16;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Fecha/hora
        lblFecha.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")));
        lblHora.setText(LocalTime.now().format(
                DateTimeFormatter.ofPattern("hh:mm a")));

        // Lista del carrito
        lstCarrito.setItems(items);

        items.addListener((javafx.collections.ListChangeListener<ItemCarrito>) c ->
                lblItemsCount.setText("(" + items.size() + ")"));

        // Datos de ejemplo (para ver el diseño como en la imagen)
        cargarDatosDemo();

        recalcularTotales();
    }

    private void cargarDatosDemo() {
        items.addAll(
                new ItemCarrito("Camiseta Básica",  "Talla: M · Color: Azul",   1, 19.90),
                new ItemCarrito("Tenis Urban",      "Talla: 39 · Color: Beige", 1, 59.90),
                new ItemCarrito("Mochila Essentials","Color: Negro",            1, 34.90),
                new ItemCarrito("Gorra Classic",    "Color: Beige",             2, 14.90)
        );
    }

    // ================== ACCIONES ==================

    @FXML private void handleCerrarSesion()     { /* TODO */ }
    @FXML private void handleAgregarProducto()  { /* TODO */ }
    @FXML private void handleCobroEfectivo()    { /* TODO */ }
    @FXML private void handleCobroDebito()      { /* TODO */ }
    @FXML private void handleCobroQR()          { /* TODO */ }
    @FXML private void handleVerMasMetodos()    { /* TODO */ }
    @FXML private void handleNuevaVenta()       { items.clear(); recalcularTotales(); }
    @FXML private void handleCancelarVenta()    { items.clear(); recalcularTotales(); }
    @FXML private void handleAbrirCaja()        { /* TODO */ }
    @FXML private void handleImprimirTicket()   { /* TODO */ }
    @FXML private void handleMasOpciones()      { /* TODO */ }

    @FXML
    private void handleCobrarFacturar() {
        if (items.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "No hay productos en el carrito.",
                    ButtonType.OK).showAndWait();
            return;
        }
        // TODO: lógica real de facturación
    }

    // ================== CÁLCULOS ==================

    private void recalcularTotales() {
        double subtotal   = items.stream().mapToDouble(ItemCarrito::getSubtotal).sum();
        double descuento  = subtotal * 0.01;                // 1% demo
        double baseIva    = subtotal - descuento;
        double iva        = baseIva * IVA;
        double total      = baseIva + iva;

        lblSubtotal.setText(moneda(subtotal));
        lblMontoIva.setText(moneda(iva));
        lblDescuento.setText("-" + moneda(descuento));
        lblTotalGrande.setText(moneda(total));
        lblTotalUsd.setText(moneda(total));
    }

    private static String moneda(double v) {
        return String.format("$ %,.2f", v);
    }

    // ================== DTO ==================

    public static class ItemCarrito {
        private final String nombre;
        private final String detalle;
        private final int cantidad;
        private final double precio;

        public ItemCarrito(String nombre, String detalle, int cantidad, double precio) {
            this.nombre   = nombre;
            this.detalle  = detalle;
            this.cantidad = cantidad;
            this.precio   = precio;
        }

        public String getNombre()   { return nombre; }
        public String getDetalle()  { return detalle; }
        public int    getCantidad() { return cantidad; }
        public double getPrecio()   { return precio; }
        public double getSubtotal() { return cantidad * precio; }

        @Override public String toString() {
            return String.format("%s  ×%d  ·  %s%n%s",
                    nombre, cantidad, moneda(precio * cantidad), detalle);
        }
    }
}