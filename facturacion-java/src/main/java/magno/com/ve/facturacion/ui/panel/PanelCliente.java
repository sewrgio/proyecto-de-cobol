package magno.com.ve.facturacion.ui.panel;

import magno.com.ve.facturacion.domain.enums.TipoContribuyente;
import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Contacto;
import magno.com.ve.facturacion.domain.model.Direccion;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.ClienteService;
import magno.com.ve.facturacion.util.Validaciones;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PanelCliente extends JPanel {

    private JTextField txtCedula;
    private JLabel lblTipoContribuyente;
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtCalle;
    private JTextField txtNumero;
    private JTextField txtUrbanizacion;
    private JTextField txtCiudad;
    private JTextField txtEstado;
    private JTextField txtCodigoPostal;
    private JTextField txtTelefonoPrincipal;
    private JTextField txtTelefonoSecundario;
    private JTextField txtEmail;

    private ClienteService clienteService;

    public PanelCliente() {
        clienteService = new ClienteService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(crearSeccionCedula());
        form.add(Box.createVerticalStrut(10));
        form.add(crearSeccionNombre());
        form.add(Box.createVerticalStrut(10));
        form.add(crearSeccionDireccion());
        form.add(Box.createVerticalStrut(10));
        form.add(crearSeccionContacto());

        add(new JScrollPane(form), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearSeccionCedula() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Identificación",
            TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Cédula/RIF: *"), gbc);

        gbc.gridx = 1;
        txtCedula = new JTextField(20);
        txtCedula.setToolTipText("Ejemplos: V-12345678, J-123456789, E-12345678");
        txtCedula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                actualizarTipoContribuyente();
            }
        });
        panel.add(txtCedula, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("  Tipo:"), gbc);

        gbc.gridx = 3;
        lblTipoContribuyente = new JLabel("—");
        lblTipoContribuyente.setFont(lblTipoContribuyente.getFont().deriveFont(Font.BOLD));
        lblTipoContribuyente.setForeground(Color.GRAY);
        panel.add(lblTipoContribuyente, gbc);

        gbc.gridx = 4; gbc.weightx = 1;
        panel.add(Box.createHorizontalGlue(), gbc);

        return panel;
    }

    private JPanel crearSeccionNombre() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Nombre / Razón Social",
            TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Nombres: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtNombres = new JTextField(40);
        panel.add(txtNombres, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtApellidos = new JTextField(40);
        panel.add(txtApellidos, gbc);

        JLabel ayuda = new JLabel("(Apellidos solo para personas naturales)");
        ayuda.setFont(ayuda.getFont().deriveFont(Font.ITALIC, 10f));
        ayuda.setForeground(Color.GRAY);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(ayuda, gbc);

        return panel;
    }

    private JPanel crearSeccionDireccion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Dirección",
            TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Calle: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtCalle = new JTextField(25);
        panel.add(txtCalle, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Nº:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.3;
        txtNumero = new JTextField(8);
        panel.add(txtNumero, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Urbanización:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtUrbanizacion = new JTextField(25);
        panel.add(txtUrbanizacion, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Ciudad: *"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        txtCiudad = new JTextField(15);
        panel.add(txtCiudad, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Estado: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtEstado = new JTextField(25);
        panel.add(txtEstado, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Cód. Postal:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.3;
        txtCodigoPostal = new JTextField(8);
        panel.add(txtCodigoPostal, gbc);

        return panel;
    }

    private JPanel crearSeccionContacto() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Contacto",
            TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Teléfono principal: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtTelefonoPrincipal = new JTextField(20);
        txtTelefonoPrincipal.setToolTipText("Ej: 0412-1234567");
        panel.add(txtTelefonoPrincipal, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Teléfono secundario:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtTelefonoSecundario = new JTextField(20);
        panel.add(txtTelefonoSecundario, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtEmail = new JTextField(40);
        txtEmail.setToolTipText("Ej: cliente@empresa.com");
        panel.add(txtEmail, gbc);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnGuardar = new JButton("💾 Guardar Cliente");
        btnGuardar.addActionListener(e -> guardarCliente());

        JButton btnLimpiar = new JButton("🗑️ Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panel.add(btnGuardar);
        panel.add(btnLimpiar);
        return panel;
    }

    private void actualizarTipoContribuyente() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            lblTipoContribuyente.setText("—");
            lblTipoContribuyente.setForeground(Color.GRAY);
            return;
        }
        TipoContribuyente tipo = Validaciones.getTipoContribuyente(cedula);
        if (tipo != null) {
            lblTipoContribuyente.setText(tipo.getDescripcion());
            lblTipoContribuyente.setForeground(new Color(0, 100, 0));
        } else {
            lblTipoContribuyente.setText("Prefijo inválido");
            lblTipoContribuyente.setForeground(Color.RED);
        }
    }

    private void guardarCliente() {
        try {
            Cliente cliente = construirClienteDesdeFormulario();
            clienteService.validarYNormalizar(cliente);
            JOptionPane.showMessageDialog(this,
                "✅ Cliente registrado correctamente:\n\n" +
                "Cédula: " + cliente.getCedula() + "\n" +
                "Nombre: " + cliente.getNombreCompleto() + "\n" +
                "Tipo: " + (cliente.getTipo() != null ? cliente.getTipo().getDescripcion() : "—"),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Errores de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Cliente construirClienteDesdeFormulario() {
        Direccion direccion = new Direccion(
            txtCalle.getText().trim(),
            txtNumero.getText().trim(),
            txtUrbanizacion.getText().trim(),
            txtCiudad.getText().trim(),
            txtEstado.getText().trim(),
            txtCodigoPostal.getText().trim(),
            "Venezuela"
        );
        Contacto contacto = new Contacto(
            txtTelefonoPrincipal.getText().trim(),
            txtTelefonoSecundario.getText().trim(),
            txtEmail.getText().trim()
        );
        return new Cliente(
            txtCedula.getText().trim(),
            txtNombres.getText().trim(),
            txtApellidos.getText().trim(),
            direccion,
            contacto
        );
    }

    private void limpiarFormulario() {
        txtCedula.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCalle.setText("");
        txtNumero.setText("");
        txtUrbanizacion.setText("");
        txtCiudad.setText("");
        txtEstado.setText("");
        txtCodigoPostal.setText("");
        txtTelefonoPrincipal.setText("");
        txtTelefonoSecundario.setText("");
        txtEmail.setText("");
        lblTipoContribuyente.setText("—");
        lblTipoContribuyente.setForeground(Color.GRAY);
        txtCedula.requestFocus();
    }
}