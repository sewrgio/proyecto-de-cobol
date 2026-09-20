package magno.com.ve.facturacion.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.repository.RepositoryFactory;
import magno.com.ve.facturacion.service.FacturacionService;
import magno.com.ve.facturacion.util.Validaciones;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class PanelFacturacionController {

    @FXML private TextField txtCedula;
    @FXML private Label lblNombreCliente;
    @FXML private Label lblTelefonoCliente;
    @FXML private Label lblDireccionCliente;
    @FXML private ComboBox<String> cmbFormaPago;

    @FXML private TextField txtCodigoProducto;
    @FXML private TextField txtCantidad;

    @FXML private TableView<Producto> tblCarrito;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colCantidad;
    @FXML private TableColumn<Producto, Double> colSubtotal;

    @FXML private TextArea txtLog;

    private final ObservableList<Producto> carrito = FXCollections.observableArrayList();
    private final FacturacionService facturacionService = new FacturacionService();
    private Cliente clienteActual;

    private boolean buscandoCliente = false;

    private static final String FXML_DIALOGO_CLIENTE =
        "/magno/com/ve/facturacion/view/DialogoCliente.fxml";
    private static final String CSS_MAGNO =
        "/magno/com/ve/facturacion/css/magno.css";

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tblCarrito.setItems(carrito);

        cmbFormaPago.setItems(FXCollections.observableArrayList(
            "CO - Contado", "DV - Divisa", "TR - Transferencia", "CR - Crédito"));
        cmbFormaPago.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleBuscarCliente() {
        if (buscandoCliente) return;

        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) return;

        if (!Validaciones.esCedulaValida(cedula)) {
            mostrarAviso("La cédula '" + cedula + "' no tiene un formato válido.\n\n" +
                         "Formatos aceptados:\n" +
                         "• V-12345678 (Venezolano)\n" +
                         "• E-12345678 (Extranjero)\n" +
                         "• J-123456789 (Jurídico / RIF)\n" +
                         "• G-123456789 (Gubernamental)\n" +
                         "• P-12345678 (Pasaporte)");
            return;
        }

        buscandoCliente = true;
        try {
            String cedulaNorm = Validaciones.normalizarCedula(cedula);
            txtCedula.setText(cedulaNorm);

            Optional<Cliente> opt = RepositoryFactory.getClienteRepository().buscarPorCedula(cedulaNorm);

            if (opt.isPresent()) {
                clienteActual = opt.get();
                mostrarClienteEnPantalla(clienteActual);
            } else {
                abrirModalNuevoCliente(cedulaNorm);
            }
        } finally {
            buscandoCliente = false;
        }
    }

    private void abrirModalNuevoCliente(String cedula) {
        try {
            URL recurso = getClass().getResource(FXML_DIALOGO_CLIENTE);
            if (recurso == null) {
                mostrarError("Recurso no encontrado",
                    "El archivo FXML no se encontró:\n" + FXML_DIALOGO_CLIENTE);
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();

            DialogoClienteController controller = loader.getController();
            controller.precargarCedula(cedula);

            Scene scene = new Scene(root, 750, 620);
            URL css = getClass().getResource(CSS_MAGNO);
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            Stage modal = new Stage();
            modal.setTitle("Registrar Nuevo Cliente");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(txtCedula.getScene().getWindow());
            modal.setScene(scene);
            modal.setWidth(750);
            modal.setHeight(620);
            modal.setMinWidth(700);
            modal.setMinHeight(600);
            modal.centerOnScreen();

            modal.showAndWait();

            if (controller.getClienteGuardado() != null) {
                clienteActual = controller.getClienteGuardado();
                mostrarClienteEnPantalla(clienteActual);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Throwable causa = e;
            while (causa.getCause() != null) causa = causa.getCause();
            mostrarError("Error al abrir modal",
                "Tipo: " + causa.getClass().getSimpleName() + "\n" +
                "Mensaje: " + causa.getMessage());
        }
    }

    private void mostrarClienteEnPantalla(Cliente cliente) {
        lblNombreCliente.setText(cliente.getNombreCompleto());
        lblTelefonoCliente.setText(cliente.getContacto() != null
            && cliente.getContacto().getTelefonoPrincipal() != null
            && !cliente.getContacto().getTelefonoPrincipal().isEmpty()
            ? cliente.getContacto().getTelefonoPrincipal() : "—");
        lblDireccionCliente.setText(cliente.getDireccion() != null
            ? cliente.getDireccion().getDireccionCompleta() : "—");
    }

    @FXML
    public void handleAgregarProducto() {
        try {
            String codigo = txtCodigoProducto.getText().trim();
            String cantStr = txtCantidad.getText().trim();
            if (codigo.isEmpty() || cantStr.isEmpty()) {
                mostrarAviso("Complete código y cantidad.");
                return;
            }
            int cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) {
                mostrarAviso("La cantidad debe ser mayor a 0.");
                return;
            }
            Producto p = new Producto();
            p.setCodigo(codigo);
            p.setNombre("Producto " + codigo);
            p.setPrecio(100.00);
            p.setCantidad(cantidad);
            carrito.add(p);
            txtCodigoProducto.clear();
            txtCantidad.clear();
            txtCodigoProducto.requestFocus();
        } catch (NumberFormatException ex) {
            mostrarAviso("La cantidad debe ser un número entero.");
        }
    }

    @FXML public void handleQuitarProducto() {
        Producto sel = tblCarrito.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAviso("Seleccione un producto."); return; }
        carrito.remove(sel);
    }

    @FXML public void handleLimpiarCarrito() { carrito.clear(); }

    @FXML
    public void handleFacturar() {
        if (carrito.isEmpty()) { mostrarAviso("El carrito está vacío."); return; }
        if (clienteActual == null) { mostrarAviso("Busque o registre un cliente antes de facturar."); return; }
        List<Producto> items = carrito.stream().toList();
        String resultado = facturacionService.procesarPedido(clienteActual, items);
        txtLog.setText(resultado);
    }

    @FXML
    public void handleLimpiarTodo() {
        carrito.clear();
        txtLog.clear();
        txtCedula.clear();
        lblNombreCliente.setText("— Busque un cliente —");
        lblTelefonoCliente.setText("—");
        lblDireccionCliente.setText("—");
        cmbFormaPago.getSelectionModel().selectFirst();
        txtCodigoProducto.clear();
        txtCantidad.clear();
        clienteActual = null;
    }

    private void mostrarAviso(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
