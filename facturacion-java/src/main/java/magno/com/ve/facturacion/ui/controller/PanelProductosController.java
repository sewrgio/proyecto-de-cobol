package magno.com.ve.facturacion.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import magno.com.ve.facturacion.domain.model.ItemFactura;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.repository.ProductoRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PanelProductosController {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private Spinner<Integer> spinnerCantidad;

    private final ObservableList<Producto> resultados = FXCollections.observableArrayList();
    private final ObservableList<ItemFactura> items = FXCollections.observableArrayList();
    private final ProductoRepository repo = new RepoEnMemoria();

    @FXML
    public void initialize() {
        repo.guardar(new Producto("P001", "Café molido 500g",   5.50, 100));
        repo.guardar(new Producto("P002", "Azúcar 1kg",          2.30, 200));
        repo.guardar(new Producto("P003", "Leche en polvo 400g", 4.80,  50));
        repo.guardar(new Producto("P004", "Pan de molde",        1.20,  30));
        repo.guardar(new Producto("P005", "Arroz 1kg",           1.80, 500));
        repo.guardar(new Producto("P006", "Harina de maíz 1kg",  1.50,  80));

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        resultados.setAll(repo.listarTodos());
        tablaProductos.setItems(resultados);

        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, old, val) ->
                resultados.setAll(repo.buscarPorNombre(val)));
        }

        if (spinnerCantidad != null) {
            spinnerCantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));
        }
    }

    /** Expone los items agregados para que otro controlador los lea. */
    public List<ItemFactura> getItems() {
        return new ArrayList<>(items);
    }

    @FXML
    private void handleAgregar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto");
            return;
        }

        int cantidad = spinnerCantidad.getValue();
        if (cantidad <= 0) {
            mostrarAlerta("La cantidad debe ser mayor a 0");
            return;
        }
        if (seleccionado.getStock() < cantidad) {
            mostrarAlerta("Stock insuficiente. Disponible: " + seleccionado.getStock());
            return;
        }

        Optional<ItemFactura> existente = items.stream()
            .filter(i -> i.getProducto().getId().equals(seleccionado.getId()))
            .findFirst();

        if (existente.isPresent()) {
            ItemFactura item = existente.get();
            int nueva = item.getCantidad() + cantidad;
            if (seleccionado.getStock() < nueva) {
                mostrarAlerta("Stock insuficiente. Ya tienes " + item.getCantidad()
                    + " y el stock total es " + seleccionado.getStock());
                return;
            }
            item.sumarCantidad(cantidad);
        } else {
            items.add(new ItemFactura(seleccionado, cantidad));
        }

        cerrarVentana();
    }

    @FXML
    private void handleCancelar() {
        items.clear();
        cerrarVentana();
    }

    private void cerrarVentana() {
        if (txtBuscar != null && txtBuscar.getScene() != null) {
            ((javafx.stage.Stage) txtBuscar.getScene().getWindow()).close();
        }
    }

    private void mostrarAlerta(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private static class RepoEnMemoria implements ProductoRepository {
        private final Map<String, Producto> datos = new ConcurrentHashMap<>();

        @Override
        public Optional<Producto> buscarPorId(String id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public List<Producto> buscarPorNombre(String nombre) {
            if (nombre == null || nombre.isBlank()) return listarTodos();
            String q = nombre.toLowerCase();
            return datos.values().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(q)
                          || p.getId().toLowerCase().contains(q))
                .collect(Collectors.toList());
        }

        @Override
        public List<Producto> listarTodos() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public void guardar(Producto producto) {
            datos.put(producto.getId(), producto);
        }
    }
}