package magno.com.ve.facturacion.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import magno.com.ve.facturacion.domain.enums.ClasificacionProducto;
import magno.com.ve.facturacion.domain.enums.UnidadMedida;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.ProductoService;
import magno.com.ve.facturacion.util.Validaciones;

public class PanelAlmacenController {

    // ══════════════════════════════════════════════
    // IDENTIFICACIÓN DEL PRODUCTO
    // ══════════════════════════════════════════════
    @FXML private TextField txtNumeroBarra;
    @FXML private TextField txtNombreProducto;
    @FXML private ComboBox<ClasificacionProducto> cmbTipoProducto;
    @FXML private TextField txtMarcaProducto;
    @FXML private ComboBox<String> cmbUnidadMedida;   // bulto, docena, unidad, etc.
    @FXML private TextField txtCantidad;
    @FXML private TextField txtDescripcion;

    // ══════════════════════════════════════════════
    // FÁBRICA
    // ══════════════════════════════════════════════
    @FXML private TextField txtCompania;
    @FXML private ComboBox<String> cmbPais;
    @FXML private TextField txtRif;

    // ══════════════════════════════════════════════
    // MEDIDAS
    // ══════════════════════════════════════════════
    @FXML private ComboBox<UnidadMedida> cmbTipoMedida;      // kg, g, L, mL, etc.
    @FXML private TextField txtCantidadMedida;               // ej: 1
    @FXML private TextField txtContenido;                    // ej: 1000
    @FXML private ComboBox<UnidadMedida> cmbTipoDimension;   // cm, mm, m, etc.
    @FXML private TextField txtCantidadDimension;            // ej: 20

    // ══════════════════════════════════════════════
    // PRECIO
    // ══════════════════════════════════════════════
    @FXML private TextField txtPrecioFactura;

    private final ProductoService productoService = new ProductoService();

    // =====================================================
    // INICIALIZACIÓN
    // =====================================================
    @FXML
    public void initialize() {
        // Tipo de producto
        cmbTipoProducto.setItems(FXCollections.observableArrayList(ClasificacionProducto.values()));
        cmbTipoProducto.getSelectionModel().selectFirst();

        // Unidad de medida del empaque
        cmbUnidadMedida.setItems(FXCollections.observableArrayList(
            "Unidad", "Par", "Docena", "Media docena", "Veintena",
            "Bulto", "Caja", "Paquete", "Saco", "Paleta"
        ));
        cmbUnidadMedida.getSelectionModel().selectFirst();

        // Países
        cmbPais.setItems(FXCollections.observableArrayList(Validaciones.getPaisesValidos()));
        cmbPais.getSelectionModel().select("Venezuela");

        // Tipo de medida (magnitud física: peso/volumen)
        cmbTipoMedida.setItems(FXCollections.observableArrayList(UnidadMedida.values()));
        cmbTipoMedida.getSelectionModel().select(UnidadMedida.KILOGRAMO);

        // Tipo de dimensión (longitud)
        cmbTipoDimension.setItems(FXCollections.observableArrayList(UnidadMedida.values()));
        cmbTipoDimension.getSelectionModel().select(UnidadMedida.CENTIMETRO);
    }

    // =====================================================
    // ACCIONES
    // =====================================================
    @FXML
    public void handleGuardar() {
        try {
            Producto producto = construirProducto();
            productoService.validar(producto);

            // TODO: guardar en repository
            mostrarExito(producto);
            limpiarFormulario();

        } catch (ValidacionException ex) {
            mostrarError("Errores de Validación", ex.getMessage());
        } catch (NumberFormatException ex) {
            mostrarError("Error de Formato",
                "Verifique que los campos numéricos sean válidos.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleLimpiar() {
        limpiarFormulario();
    }

    // =====================================================
    // MAPEO UI → MODELO
    // =====================================================
    private Producto construirProducto() throws NumberFormatException {
        Producto p = new Producto();

        // ── Identificación
        p.setCodigoBarra(txtNumeroBarra.getText().trim());
        p.setNombreProducto(txtNombreProducto.getText().trim());
        p.setTipoProducto(cmbTipoProducto.getValue());
        p.setMarcaProducto(txtMarcaProducto.getText().trim());
        p.setUnidadMedida(cmbUnidadMedida.getValue());
        p.setCantidad(parseIntObligatorio(txtCantidad.getText()));
        p.setDescripcion(txtDescripcion.getText().trim());

        // ── Fábrica
        p.setCompaniaFabricacion(txtCompania.getText().trim());
        p.setPaisOrigen(cmbPais.getValue());
        p.setIdentificador(txtRif.getText().trim().toUpperCase());

        // ── Medidas
        p.setUnidadPeso(cmbTipoMedida.getValue());
        p.setCantidadMedida(parseDouble(txtCantidadMedida.getText()));
        p.setContenido(parseDouble(txtContenido.getText()));
        p.setUnidadDimension(cmbTipoDimension.getValue());
        p.setCantidadDimension(parseDouble(txtCantidadDimension.getText()));

        // ── Precio
        p.setPrecio(parseDoubleObligatorio(txtPrecioFactura.getText()));

        return p;
    }

    // =====================================================
    // CONVERSORES
    // =====================================================
    private Double parseDouble(String texto) {
        if (texto == null || texto.trim().isEmpty()) return null;
        return Double.parseDouble(texto.trim().replace(",", "."));
    }

    private double parseDoubleObligatorio(String texto) {
        if (texto == null || texto.trim().isEmpty()) return 0;
        return Double.parseDouble(texto.trim().replace(",", "."));
    }

    private int parseIntObligatorio(String texto) {
        if (texto == null || texto.trim().isEmpty()) return 0;
        return Integer.parseInt(texto.trim());
    }

    // =====================================================
    // RESET
    // =====================================================
    private void limpiarFormulario() {
        // Identificación
        txtNumeroBarra.clear();
        txtNombreProducto.clear();
        cmbTipoProducto.getSelectionModel().selectFirst();
        txtMarcaProducto.clear();
        cmbUnidadMedida.getSelectionModel().selectFirst();
        txtCantidad.clear();
        txtDescripcion.clear();

        // Fábrica
        txtCompania.clear();
        cmbPais.getSelectionModel().select("Venezuela");
        txtRif.clear();

        // Medidas
        cmbTipoMedida.getSelectionModel().select(UnidadMedida.KILOGRAMO);
        txtCantidadMedida.clear();
        txtContenido.clear();
        cmbTipoDimension.getSelectionModel().select(UnidadMedida.CENTIMETRO);
        txtCantidadDimension.clear();

        // Precio
        txtPrecioFactura.clear();

        txtNumeroBarra.requestFocus();
    }

    // =====================================================
    // DIÁLOGOS
    // =====================================================
    private void mostrarExito(Producto producto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Producto Registrado");
        alert.setHeaderText("✅ " + producto.getNombreProducto());
        alert.setContentText(
            "── Identificación ──\n" +
            "Nº de barra: " + producto.getCodigoBarra() + "\n" +
            "Tipo: " + producto.getTipoProducto() + "\n" +
            "Marca: " + producto.getMarcaProducto() + "\n" +
            "Unidad: " + producto.getUnidadMedida() + " × " + producto.getCantidad() + "\n\n" +
            "── Fábrica ──\n" +
            "Compañía: " + producto.getCompaniaFabricacion() + "\n" +
            "País: " + producto.getPaisOrigen() + "\n" +
            "RIF: " + producto.getIdentificador() + "\n\n" +
            "── Medidas ──\n" +
            "Medida: " + producto.getCantidadMedida() + " " + producto.getUnidadPeso() + "\n" +
            "Contenido: " + producto.getContenido() + "\n" +
            "Dimensión: " + producto.getCantidadDimension() + " " + producto.getUnidadDimension() + "\n\n" +
            "── Precio ──\n" +
            "Precio factura: $" + producto.getPrecio()
        );
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}