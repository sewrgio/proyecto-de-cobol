package magno.com.ve.facturacion.ui.panel;

import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Contacto;
import magno.com.ve.facturacion.domain.model.Direccion;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.service.FacturacionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelFacturacion extends JPanel {

    private JTextField txtCedula;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtDireccion;
    private JTextField txtTelefono;

    private JTextField txtIdProducto;
    private JTextField txtNombreProducto;
    private JTextField txtPrecio;
    private JTextField txtCantidad;

    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JTextArea areaLog;

    private List<Producto> carrito;
    private FacturacionService servicio;

    public PanelFacturacion() {
        carrito = new ArrayList<>();
        servicio = new FacturacionService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panelExterior = new JPanel(new BorderLayout(5, 5));
        panelExterior.setBorder(BorderFactory.createTitledBorder("Datos del Cliente y Producto"));

        JPanel panelCliente = new JPanel(new GridBagLayout());
        panelCliente.setBorder(BorderFactory.createTitledBorder("Cliente"));
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

        JPanel panelProducto = new JPanel(new GridBagLayout());
        panelProducto.setBorder(BorderFactory.createTitledBorder("Producto"));
        GridBagConstraints gbcP = new GridBagConstraints();
        gbcP.insets = new Insets(3, 5, 3, 5);
        gbcP.fill = GridBagConstraints.HORIZONTAL;

        gbcP.gridx = 0; gbcP.gridy = 0;
        panelProducto.add(new JLabel("ID:"), gbcP);
        gbcP.gridx = 1;
        txtIdProducto = new JTextField(10);
        panelProducto.add(txtIdProducto, gbcP);

        gbcP.gridx = 2;
        panelProducto.add(new JLabel("Nombre:"), gbcP);
        gbcP.gridx = 3;
        txtNombreProducto = new JTextField(15);
        panelProducto.add(txtNombreProducto, gbcP);

        gbcP.gridx = 0; gbcP.gridy = 1;
        panelProducto.add(new JLabel("Precio ($):"), gbcP);
        gbcP.gridx = 1;
        txtPrecio = new JTextField(10);
        panelProducto.add(txtPrecio, gbcP);

        gbcP.gridx = 2;
        panelProducto.add(new JLabel("Cantidad:"), gbcP);
        gbcP.gridx = 3;
        txtCantidad = new JTextField(5);
        panelProducto.add(txtCantidad, gbcP);

        panelExterior.add(panelCliente, BorderLayout.NORTH);
        panelExterior.add(panelProducto, BorderLayout.CENTER);

        return panelExterior;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        String[] columnas = {"ID", "Producto", "Precio", "Cantidad", "Subtotal"};
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
            String id = txtIdProducto.getText().trim();
            String nombre = txtNombreProducto.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete los campos del producto.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Producto p = new Producto(id, nombre, precio, cantidad);
            carrito.add(p);
            modeloTabla.addRow(new Object[]{
                p.getId(), p.getNombre(),
                String.format("%.2f", p.getPrecio()),
                p.getCantidad(),
                String.format("%.2f", p.getSubtotal())
            });

            txtIdProducto.setText("");
            txtNombreProducto.setText("");
            txtPrecio.setText("");
            txtCantidad.setText("");
            txtIdProducto.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y Cantidad deben ser numéricos.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private void limpiarTodo() {
        carrito.clear();
        modeloTabla.setRowCount(0);
        areaLog.setText("");
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtIdProducto.setText("");
        txtNombreProducto.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
    }
}