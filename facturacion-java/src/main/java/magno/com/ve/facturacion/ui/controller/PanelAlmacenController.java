package magno.com.ve.facturacion.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import magno.com.ve.facturacion.domain.enums.UnidadMedida;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.ProductoService;
import magno.com.ve.facturacion.util.Validaciones;

public class PanelAlmacenController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtCodigoFabricacion;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtCompania;
    @FXML private ComboBox<String> cmbPais;
    @FXML private TextField txtPeso;
    @FXML private ComboBox<UnidadMedida> cmbUnidadPeso;
    @FXML private TextField txtAltura;
    @FXML private TextField txtAnchura;
    @FXML private TextField txtGrosor;
    @FXML private ComboBox<UnidadMedida> cmbUnidadDimension;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStockInicial;

    private final ProductoService productoService = new ProductoService();

    @FXML
    public void initialize() {
        // Poblar combo de países
        cmbPais.setItems(FXCollections.observableArrayList(Validaciones.getPaisesValidos()));
        cmbPais.getSelectionModel().select("Venezuela");

        // Poblar combo de unidades
        cmbUnidadPeso.setItems(FXCollections.observableArrayList(UnidadMedida.values()));
        cmbUnidadPeso.getSelectionModel().select(UnidadMedida.KILOGRAMO);

        cmbUnidadDimension.setItems(FXCollections.observableArrayList(UnidadMedida.values()));
        cmbUnidadDimension.getSelectionModel().select(UnidadMedida.CENTIMETRO);
    }

    @FXML
    public void handleGuardar() {
        try {
            Producto producto = construirProducto();
            productoService.validar(producto);

            // TODO: Guardar en ProductoRepository cuando esté conectado

            mostrarExito(producto);
            limpiarFormulario();

        } catch (ValidacionException ex) {
            mostrarError("Errores de Validación", ex.getMessage());
        } catch (NumberFormatException ex) {
            mostrarError("Error de Formato",
                "Verifique que los campos numéricos (peso, medidas, precio, stock) sean válidos.");
        } catch (Exception ex) {
            mostrarError("Error Inesperado", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleLimpiar() {
        limpiarFormulario();
    }

    private Producto construirProducto() throws NumberFormatException {
        Producto p = new Producto();
        p.setCodigo(txtCodigo.getText().trim());
        p.setCodigoFabricacion(txtCodigoFabricacion.getText().trim().toUpperCase());
        p.setNombre(txtNombre.getText().trim());
        p.setDescripcion(txtDescripcion.getText().trim());
        p.setCompaniaFabricacion(txtCompania.getText().trim());
        p.setPaisOrigen(cmbPais.getValue());

        p.setPeso(parseDouble(txtPeso.getText()));
        p.setUnidadPeso(cmbUnidadPeso.getValue());

        p.setAltura(parseDouble(txtAltura.getText()));
        p.setAnchura(parseDouble(txtAnchura.getText()));
        p.setGrosor(parseDouble(txtGrosor.getText()));
        p.setUnidadDimension(cmbUnidadDimension.getValue());

        p.setPrecio(parseDoubleObligatorio(txtPrecio.getText()));
        p.setCantidad(parseIntObligatorio(txtStockInicial.getText()));

        return p;
    }

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

    private void limpiarFormulario() {
        txtCodigo.clear();
        txtCodigoFabricacion.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtCompania.clear();
        cmbPais.getSelectionModel().select("Venezuela");
        txtPeso.clear();
        cmbUnidadPeso.getSelectionModel().select(UnidadMedida.KILOGRAMO);
        txtAltura.clear();
        txtAnchura.clear();
        txtGrosor.clear();
        cmbUnidadDimension.getSelectionModel().select(UnidadMedida.CENTIMETRO);
        txtPrecio.clear();
        txtStockInicial.clear();
        txtCodigo.requestFocus();
    }

    private void mostrarExito(Producto producto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Producto Registrado");
        alert.setHeaderText("✅ " + producto.getNombre());
        alert.setContentText(
            "Código: " + producto.getCodigo() + "\n" +
            "Fabricación: " + producto.getCompaniaFabricacion() + "\n" +
            "País: " + producto.getPaisOrigen() + "\n" +
            "Precio: $" + producto.getPrecio() + "\n" +
            "Stock: " + producto.getCantidad()
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