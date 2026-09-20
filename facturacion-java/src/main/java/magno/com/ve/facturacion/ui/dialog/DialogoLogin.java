package magno.com.ve.facturacion.ui.dialog;

import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Diálogo modal de login.
 * Bloquea la aplicación hasta que el usuario se autentique o cancele.
 */
public class DialogoLogin extends JDialog {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblMensaje;
    private Usuario usuarioAutenticado;

    private final UsuarioService usuarioService;

    public DialogoLogin(Frame parent, UsuarioService usuarioService) {
        super(parent, "Iniciar Sesión", true);
        this.usuarioService = usuarioService;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        add(crearContenido());

        // Al presionar ESC, se cierra como cancelado
        getRootPane().registerKeyboardAction(
            e -> cancelar(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JPanel crearContenido() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(20, 30, 20, 30));

        // ===== Encabezado =====
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sistema de Facturación");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Java + COBOL");
        subtitulo.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(subtitulo);

        // ===== Formulario =====
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtUsuario = new JTextField(15);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        formulario.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formulario.add(txtPassword, gbc);

        // Mensaje de error
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        formulario.add(lblMensaje, gbc);

        // ===== Botones =====
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setPreferredSize(new Dimension(120, 32));
        btnIngresar.addActionListener(e -> intentarLogin());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 32));
        btnCancelar.addActionListener(e -> cancelar());

        botones.add(btnIngresar);
        botones.add(btnCancelar);

        // ===== Ensamblar =====
        panelPrincipal.add(encabezado, BorderLayout.NORTH);
        panelPrincipal.add(formulario, BorderLayout.CENTER);
        panelPrincipal.add(botones, BorderLayout.SOUTH);

        // Enter en cualquier campo = intentar login
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        };
        txtUsuario.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);

        return panelPrincipal;
    }

    private void intentarLogin() {
        lblMensaje.setText(" ");
        try {
            String username = txtUsuario.getText().trim();
            String password = new String(txtPassword.getPassword());

            Usuario usuario = usuarioService.autenticar(username, password);
            this.usuarioAutenticado = usuario;

            dispose(); // Cerrar el diálogo

        } catch (ValidacionException ex) {
            lblMensaje.setText("✗ " + ex.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void cancelar() {
        usuarioAutenticado = null;
        dispose();
    }

    /**
     * Devuelve el usuario autenticado, o null si se canceló.
     */
    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    /**
     * Método estático de conveniencia para mostrar el login.
     */
    public static Usuario mostrar(Frame parent, UsuarioService usuarioService) {
        DialogoLogin dialog = new DialogoLogin(parent, usuarioService);
        dialog.setVisible(true);
        return dialog.getUsuarioAutenticado();
    }
}