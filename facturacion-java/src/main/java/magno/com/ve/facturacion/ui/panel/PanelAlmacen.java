package magno.com.ve.facturacion.ui.panel;

import magno.com.ve.facturacion.domain.enums.UnidadMedida;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.ProductoService;
import magno.com.ve.facturacion.util.Validaciones;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PanelAlmacen extends JPanel {

    private JTextField txtCodigo;
    private JTextField txtCodigoFabricacion;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCompania;
    private JComboBox<String> cmbPais;
    private JTextField txtPeso;
    private JComboBox<UnidadMedida> cmbUnidadPeso;
    private JTextField txtAltura;
    private JTextField txtAnchura;
    private JTextField txtGrosor;
    private JComboBox<UnidadMedida> cmbUnidadDimension;
    private JTextField txtPrecio;
    private JTextField txtStockInicial;

    private JTextArea areaLog;

    private ProductoService productoService;

    public PanelAlmacen() {
        productoService = new ProductoService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(crearSeccionIdentificacion());
        form.add(Box.createVerticalStrut(10));
        form.add(crearSeccionFabricacion());
        form.add(Box.createVerticalStrut(10));
        form.add(crearSeccionMedidas());
        form.add(Box.createVerticalStrut(10));
        form.add(crearSeccionPrecio());
        form.add(Box.createVerticalStrut(10));
        form.add(crearSeccionLog());

        add(new JScrollPane(form), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearSeccionIdentificacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Identificación del Producto",
            TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Nombre: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtNombre = new JTextField(40);
        panel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtDescripcion = new JTextField(40);
        panel.add(txtDescripcion, gbc);

        return panel;
    }

    private JPanel crearSeccionFabricacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Fabricación",
            TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Compañía fabricación: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtCompania = new JTextField(25);
        txtCompania.setToolTipText("Ej: Samsung Electronics Co.");
        panel.add(txtCompania, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("País origen: *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cmbPais = new JComboBox<>(Validaciones.getPaisesValidos());
        cmbPais.setSelectedItem("Venezuela");
        panel.add(cmbPais, gbc);

        return panel;
    }

    private JPanel crearSeccionMedidas() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Medidas",
            TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
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

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
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

        return panel;
    }

    private JPanel crearSeccionPrecio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Precio y Stock",
            TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Precio ($): *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPrecio = new JTextField(10);
        panel.add(txtPrecio, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Stock inicial: *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtStockInicial = new JTextField(10);
        panel.add(txtStockInicial, gbc);

        return panel;
    }

    private JPanel crearSeccionLog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Log"));
        areaLog = new JTextArea(6, 60);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnGuardar = new JButton("💾 Registrar Producto");
        btnGuardar.addActionListener(e -> guardarProducto());

        JButton btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panel.add(btnGuardar);
        panel.add(btnLimpiar);
        return panel;
    }

    private void guardarProducto() {
        try {
            Producto producto = construirProductoDesdeFormulario();
            productoService.validar(producto);

            areaLog.append("[ALMACÉN] Producto registrado: " + producto.getCodigo()
                    + " - " + producto.getNombre() + "\n");
            areaLog.append("[ALMACÉN] Precio: $" + producto.getPrecio()
                    + " | Stock: " + producto.getCantidad() + "\n");

            JOptionPane.showMessageDialog(this,
                "✅ Producto registrado:\n\n" +
                "Código: " + producto.getCodigo() + "\n" +
                "Nombre: " + producto.getNombre() + "\n" +
                "Compañía: " + producto.getCompaniaFabricacion() + "\n" +
                "País: " + producto.getPaisOrigen() + "\n" +
                "Precio: $" + producto.getPrecio() + "\n" +
                "Stock: " + producto.getCantidad(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();

        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Errores de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Verifique que los campos numéricos sean válidos.",
                "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Producto construirProductoDesdeFormulario() {
        Producto p = new Producto();
        p.setCodigo(txtCodigo.getText().trim());
        p.setCodigoFabricacion(txtCodigoFabricacion.getText().trim().toUpperCase());
        p.setNombre(txtNombre.getText().trim());
        p.setDescripcion(txtDescripcion.getText().trim());
        p.setCompaniaFabricacion(txtCompania.getText().trim());
        p.setPaisOrigen((String) cmbPais.getSelectedItem());

        p.setPeso(parseDouble(txtPeso.getText()));
        p.setUnidadPeso((UnidadMedida) cmbUnidadPeso.getSelectedItem());

        p.setAltura(parseDouble(txtAltura.getText()));
        p.setAnchura(parseDouble(txtAnchura.getText()));
        p.setGrosor(parseDouble(txtGrosor.getText()));
        p.setUnidadDimension((UnidadMedida) cmbUnidadDimension.getSelectedItem());

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
        txtCodigo.setText("");
        txtCodigoFabricacion.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCompania.setText("");
        cmbPais.setSelectedItem("Venezuela");
        txtPeso.setText("");
        txtAltura.setText("");
        txtAnchura.setText("");
        txtGrosor.setText("");
        cmbUnidadPeso.setSelectedItem(UnidadMedida.KILOGRAMO);
        cmbUnidadDimension.setSelectedItem(UnidadMedida.CENTIMETRO);
        txtPrecio.setText("");
        txtStockInicial.setText("");
        txtCodigo.requestFocus();
    }
}