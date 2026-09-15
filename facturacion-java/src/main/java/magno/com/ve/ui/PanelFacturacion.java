package magno.com.ve.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import magno.com.ve.model.Producto;
import magno.com.ve.service.FacturacionService;

public class PanelFacturacion extends JPanel {

    private JTextField txtCliente;
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente y Producto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtCliente = new JTextField(30);
        panel.add(txtCliente, gbc);

        // ID Producto
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("ID Producto:"), gbc);
        gbc.gridx = 1;
        txtIdProducto = new JTextField(10);
        panel.add(txtIdProducto, gbc);

        // Nombre
        gbc.gridx = 2;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3;
        txtNombreProducto = new JTextField(15);
        panel.add(txtNombreProducto, gbc);

        // Precio
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Precio ($):"), gbc);
        gbc.gridx = 1;
        txtPrecio = new JTextField(10);
        panel.add(txtPrecio, gbc);

        // Cantidad
        gbc.gridx = 2;
        panel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 3;
        txtCantidad = new JTextField(5);
        panel.add(txtCantidad, gbc);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Tabla del carrito
        String[] columnas = {"ID", "Producto", "Precio", "Cantidad", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaCarrito);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Carrito de Compra"));
        panel.add(scrollTabla, BorderLayout.CENTER);

        // Área de log
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
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
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

            // Limpiar campos de producto
            txtIdProducto.setText("");
            txtNombreProducto.setText("");
            txtPrecio.setText("");
            txtCantidad.setText("");
            txtIdProducto.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y Cantidad deben ser numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void facturar() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cliente = txtCliente.getText().trim();
        if (cliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        areaLog.setText("");
        String resultado = servicio.procesarPedido(cliente, carrito);
        areaLog.setText(resultado);
    }

    private void limpiarTodo() {
        carrito.clear();
        modeloTabla.setRowCount(0);
        areaLog.setText("");
        txtCliente.setText("");
    }
}