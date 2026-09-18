package magno.com.ve.facturacion.ui.panel;

import magno.com.ve.facturacion.domain.enums.UnidadMedida;
import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Contacto;
import magno.com.ve.facturacion.domain.model.Direccion;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.FacturacionService;
import magno.com.ve.facturacion.service.ProductoService;
import magno.com.ve.facturacion.util.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelFacturacion extends JPanel {

    // Datos del cliente
    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtDireccion;
    private JTextField txtTelefono;

    // Datos del producto
    private JTextField txtCodigo;
    private JTextField txtCodigoFabricacion;
    private JTextField txtNombreProducto;
    private JTextField txtDescripcion;
    private JTextField txtCompaniaFabricacion;   // NUEVO
    private JComboBox<String> cmbPaisOrigen;      // NUEVO
    private JTextField txtPeso;
    private JComboBox<UnidadMedida> cmbUnidadPeso;
    private JTextField txtAltura;
    private JTextField txtAnchura;
    private JTextField txtGrosor;
    private JComboBox<UnidadMedida> cmbUnidadDimension;
    private JTextField txtPrecio;
    private JTextField txtCantidad;

    // Tabla y log
    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JTextArea areaLog;

    private List<Producto> carrito;
    private FacturacionService servicio;
    private ProductoService productoService;

    public PanelFacturacion() {
        carrito = new ArrayList<>();
        servicio = new FacturacionService();
        productoService = new ProductoService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panelExterior = new JPanel(new BorderLayout(5, 5));
        panelExterior.setBorder(BorderFactory.createTitledBorder("Datos del Cliente y Producto"));
        panelExterior.add(crearPanelCliente(), BorderLayout.NORTH);
        panelExterior.add(crearPanelProducto(), BorderLayout.CENTER);
        return panelExterior;
    }

    private JPanel crearPanelCliente() {
        JPanel panelCliente = new JPanel(new GridBagLayout());
        panelCliente.setBorder(BorderFactory.createTitledBorder("Cliente (Rápido)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelCliente.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        txtCedula = new JTextField(15);
        panelCliente.add(txtCedula, gbc);

        gbc.gridx = 2;
        panelCliente.add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 3;
        txtNombres = new JTextField(15);
        panelCliente.add(txtNombres, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelCliente.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        txtApellidos = new JTextField(15);
        panelCliente.add(txtApellidos, gbc);

        gbc.gridx = 2;
        panelCliente.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 3;
        txtTelefono = new JTextField(15);
        panelCliente.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelCliente.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDireccion = new JTextField(40);
        panelCliente.add(txtDireccion, gbc);

        return panelCliente;
    }

    private JPanel crearPanelProducto() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Código y código de fabricación
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Código (12 dígitos): *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtCodigo = new JTextField(15);
        txtCodigo.setToolTipText("Ej: 123456789012");
        panel.add(txtCodigo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Cód. Fabricación: *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtCodigoFabricacion = new JTextField(15);
        txtCodigoFabricacion.setToolTipText("Ej: FAB-2024-A001");
        panel.add(txtCodigoFabricacion, gbc);

        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Nombre: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtNombreProducto = new JTextField(40);
        panel.add(txtNombreProducto, gbc);

        // Fila 2: Descripción
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtDescripcion = new JTextField(40);
        panel.add(txtDescripcion, gbc);

        // Fila 3: Compañía y país de origen (NUEVO)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Compañía fabricación: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtCompaniaFabricacion = new JTextField(20);
        txtCompaniaFabricacion.setToolTipText("Ej: Samsung Electronics Co.");
        panel.add(txtCompaniaFabricacion, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("País origen: *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cmbPaisOrigen = new JComboBox<>(Validaciones.getPaisesValidos());
        cmbPaisOrigen.setSelectedItem("Venezuela");
        panel.add(cmbPaisOrigen, gbc);

        // Fila 4: Peso + unidad
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Peso:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPeso = new JTextField(10);
        panel.add(txtPeso, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Unidad peso:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cmbUnidadPeso = new JComboBox<>(UnidadMedida.values());
        cmbUnidadPeso.setSelectedItem(UnidadMedida.KILOGRAMO);
        panel.add(cmbUnidadPeso, gbc);

        // Fila 5: Dimensiones
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Altura:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        txtAltura = new JTextField(8);
        panel.add(txtAltura, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Anchura:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        txtAnchura = new JTextField(8);
        panel.add(txtAnchura, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        panel.add(new JLabel("Grosor:"), gbc);
        gbc.gridx = 5; gbc.weightx = 0.5;
        txtGrosor = new JTextField(8);
        panel.add(txtGrosor, gbc);

        gbc.gridx = 6; gbc.weightx = 0;
        panel.add(new JLabel("Unidad:"), gbc);
        gbc.gridx = 7; gbc.weightx = 1;
        cmbUnidadDimension = new JComboBox<>(UnidadMedida.values());
        cmbUnidadDimension.setSelectedItem(UnidadMedida.CENTIMETRO);
        panel.add(cmbUnidadDimension, gbc);

        // Fila 6: Precio y cantidad
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("Precio ($): *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPrecio = new JTextField(10);
        panel.add(txtPrecio, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Cantidad: *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtCantidad = new JTextField(5);
        panel.add(txtCantidad, gbc);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        String[] columnas = {"Código", "Producto", "Fabricante", "Origen", "Peso", "Dimensiones", "Precio", "Cant.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaCarrito);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Carrito de Compra"));
        panel.add(scrollTabla, BorderLayout.CENTER);

        areaLog = new JTextArea(10, 50);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Log del Core COBOL"));
        panel.add(scrollLog, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAgregar = new JButton("➕ Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarProducto());

        JButton btnFacturar = new JButton("🧾 Facturar");
        btnFacturar.addActionListener(e -> facturar());

        JButton btnLimpiar = new JButton("🗑️ Limpiar Todo");
        btnLimpiar.addActionListener(e -> limpiarTodo());

        panel.add(btnAgregar);
        panel.add(btnFacturar);
        panel.add(btnLimpiar);

        return panel;
    }

    private void agregarProducto() {
        try {
            Producto producto = construirProductoDesdeFormulario();
            productoService.validar(producto);

            carrito.add(producto);
            modeloTabla.addRow(new Object[]{
                producto.getCodigo(),
                producto.getNombre(),
                producto.getCompaniaFabricacion(),
                producto.getPaisOrigen(),
                producto.getPesoFormateado(),
                producto.getDimensionesFormateadas(),
                String.format("%.2f", producto.getPrecio()),
                producto.getCantidad(),
                String.format("%.2f", producto.getSubtotal())
            });

            limpiarCamposProducto();

        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Errores de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Verifique que los campos numéricos sean válidos.",
                "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Producto construirProductoDesdeFormulario() throws NumberFormatException {
        Producto p = new Producto();
        p.setCodigo(txtCodigo.getText().trim());
        p.setCodigoFabricacion(txtCodigoFabricacion.getText().trim().toUpperCase());
        p.setNombre(txtNombreProducto.getText().trim());
        p.setDescripcion(txtDescripcion.getText().trim());
        p.setCompaniaFabricacion(txtCompaniaFabricacion.getText().trim());
        p.setPaisOrigen((String) cmbPaisOrigen.getSelectedItem());

        p.setPeso(parseDouble(txtPeso.getText()));
        p.setUnidadPeso((UnidadMedida) cmbUnidadPeso.getSelectedItem());

        p.setAltura(parseDouble(txtAltura.getText()));
        p.setAnchura(parseDouble(txtAnchura.getText()));
        p.setGrosor(parseDouble(txtGrosor.getText()));
        p.setUnidadDimension((UnidadMedida) cmbUnidadDimension.getSelectedItem());

        p.setPrecio(parseDoubleObligatorio(txtPrecio.getText()));
        p.setCantidad(parseIntObligatorio(txtCantidad.getText()));

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

    private void facturar() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cedula = txtCedula.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (cedula.isEmpty() || nombres.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese al menos cédula y nombres.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente(
            cedula, nombres, apellidos,
            new Direccion(direccion, "", "", "", "", "", "Venezuela"),
            new Contacto(telefono, "", "")
        );

        areaLog.setText("");
        String resultado = servicio.procesarPedido(cliente, carrito);
        areaLog.setText(resultado);
    }

    private void limpiarCamposProducto() {
        txtCodigo.setText("");
        txtCodigoFabricacion.setText("");
        txtNombreProducto.setText("");
        txtDescripcion.setText("");
        txtCompaniaFabricacion.setText("");
        cmbPaisOrigen.setSelectedItem("Venezuela");
        txtPeso.setText("");
        txtAltura.setText("");
        txtAnchura.setText("");
        txtGrosor.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
        cmbUnidadPeso.setSelectedItem(UnidadMedida.KILOGRAMO);
        cmbUnidadDimension.setSelectedItem(UnidadMedida.CENTIMETRO);
        txtCodigo.requestFocus();
    }

    private void limpiarTodo() {
        carrito.clear();
        modeloTabla.setRowCount(0);
        areaLog.setText("");
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        limpiarCamposProducto();
    }
}