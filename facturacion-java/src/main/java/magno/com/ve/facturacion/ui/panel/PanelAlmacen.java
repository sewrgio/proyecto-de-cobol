package magno.com.ve.facturacion.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.enums.UnidadMedida;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.ProductoService;
import magno.com.ve.facturacion.util.Validaciones;

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
        setLayout(new BorderLayout());
        setBackground(AppConfig.COLOR_FONDO_PRINCIPAL);

        add(crearEncabezado(), BorderLayout.NORTH);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(AppConfig.COLOR_FONDO_PRINCIPAL);
        formulario.setBorder(new EmptyBorder(20, 30, 20, 30));

        formulario.add(crearTarjeta("Identificación del Producto", crearSeccionIdentificacion()));
        formulario.add(Box.createVerticalStrut(AppConfig.ESPACIO_M));
        formulario.add(crearTarjeta("Fabricación", crearSeccionFabricacion()));
        formulario.add(Box.createVerticalStrut(AppConfig.ESPACIO_M));
        formulario.add(crearTarjeta("Medidas", crearSeccionMedidas()));
        formulario.add(Box.createVerticalStrut(AppConfig.ESPACIO_M));
        formulario.add(crearTarjeta("Precio y Stock", crearSeccionPrecio()));

        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppConfig.COLOR_FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, AppConfig.COLOR_BORDE),
            new EmptyBorder(20, 30, 20, 30)
        ));

        JLabel titulo = new JLabel("Registrar Nuevo Producto");
        titulo.setFont(AppConfig.FUENTE_TITULO);
        titulo.setForeground(AppConfig.COLOR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Complete los datos del producto para agregarlo al almacén");
        subtitulo.setFont(AppConfig.FUENTE_PEQUENA);
        subtitulo.setForeground(AppConfig.COLOR_TEXTO_SECUNDARIO);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearTarjeta(String titulo, JPanel contenido) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(AppConfig.COLOR_FONDO_TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.COLOR_BORDE, 1),
            new EmptyBorder(16, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(AppConfig.FUENTE_SUBTITULO);
        lblTitulo.setForeground(AppConfig.COLOR_PRIMARIO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 12, 0));

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(contenido, BorderLayout.CENTER);
        return tarjeta;
    }

    private JPanel crearSeccionIdentificacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(crearLabelObligatorio("Código (12 dígitos)"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        txtCodigo = crearTextField();
        txtCodigo.setToolTipText("Ej: 123456789012");
        panel.add(txtCodigo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(crearLabelObligatorio("Código de fabricación"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        txtCodigoFabricacion = crearTextField();
        txtCodigoFabricacion.setToolTipText("Ej: FAB-2024-A001");
        panel.add(txtCodigoFabricacion, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.weightx = 1;
        panel.add(crearLabelObligatorio("Nombre del producto"), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        txtNombre = crearTextField();
        panel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panel.add(crearLabel("Descripción"), gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        txtDescripcion = crearTextField();
        panel.add(txtDescripcion, gbc);

        return panel;
    }

    private JPanel crearSeccionFabricacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(crearLabelObligatorio("Compañía de fabricación"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtCompania = crearTextField();
        panel.add(txtCompania, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(crearLabelObligatorio("País de origen"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbPais = new JComboBox<>(Validaciones.getPaisesValidos());
        cmbPais.setSelectedItem("Venezuela");
        cmbPais.setFont(AppConfig.FUENTE_TEXTO);
        cmbPais.setBackground(Color.WHITE);
        panel.add(cmbPais, gbc);

        return panel;
    }

    private JPanel crearSeccionMedidas() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(crearLabel("Peso"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtPeso = crearTextField();
        panel.add(txtPeso, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(crearLabel("Unidad"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        cmbUnidadPeso = new JComboBox<>(UnidadMedida.values());
        cmbUnidadPeso.setSelectedItem(UnidadMedida.KILOGRAMO);
        cmbUnidadPeso.setFont(AppConfig.FUENTE_TEXTO);
        panel.add(cmbUnidadPeso, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(crearLabel("Altura"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtAltura = crearTextField();
        panel.add(txtAltura, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(crearLabel("Anchura"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtAnchura = crearTextField();
        panel.add(txtAnchura, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(crearLabel("Grosor"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtGrosor = crearTextField();
        panel.add(txtGrosor, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(crearLabel("Unidad"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        cmbUnidadDimension = new JComboBox<>(UnidadMedida.values());
        cmbUnidadDimension.setSelectedItem(UnidadMedida.CENTIMETRO);
        cmbUnidadDimension.setFont(AppConfig.FUENTE_TEXTO);
        panel.add(cmbUnidadDimension, gbc);

        return panel;
    }

    private JPanel crearSeccionPrecio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(crearLabelObligatorio("Precio ($)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPrecio = crearTextField();
        panel.add(txtPrecio, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(crearLabelObligatorio("Stock inicial"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtStockInicial = crearTextField();
        panel.add(txtStockInicial, gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
        panel.setBackground(AppConfig.COLOR_FONDO_TARJETA);
        panel.setBorder(new MatteBorder(1, 0, 0, 0, AppConfig.COLOR_BORDE));

        JButton btnLimpiar = crearBotonSecundario("Cancelar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnGuardar = crearBotonPrimario("Registrar Producto");
        btnGuardar.addActionListener(e -> guardarProducto());

        panel.add(btnLimpiar);
        panel.add(btnGuardar);
        return panel;
    }

    // ===== COMPONENTES AUXILIARES =====

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(AppConfig.FUENTE_TEXTO);
        lbl.setForeground(AppConfig.COLOR_TEXTO_SECUNDARIO);
        return lbl;
    }

    private JLabel crearLabelObligatorio(String texto) {
        JLabel lbl = new JLabel(texto + " *");
        lbl.setFont(AppConfig.FUENTE_TEXTO);
        lbl.setForeground(AppConfig.COLOR_TEXTO_PRINCIPAL);
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField tf = new JTextField(20);
        tf.setFont(AppConfig.FUENTE_TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.COLOR_BORDE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(AppConfig.COLOR_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(AppConfig.FUENTE_TEXTO);
        btn.setBackground(Color.WHITE);
        btn.setForeground(AppConfig.COLOR_TEXTO_PRINCIPAL);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConfig.COLOR_BORDE, 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ===== LÓGICA =====

    private void guardarProducto() {
        try {
            Producto producto = construirProductoDesdeFormulario();
            productoService.validar(producto);

            JOptionPane.showMessageDialog(this,
                "Producto registrado correctamente:\n\n" +
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