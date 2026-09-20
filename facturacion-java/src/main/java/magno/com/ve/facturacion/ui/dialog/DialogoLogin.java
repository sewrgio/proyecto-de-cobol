package magno.com.ve.facturacion.ui.dialog;

import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Diálogo modal de login para MAGNO.
 * Bloquea la aplicación hasta que el usuario se autentique o cancele.
 */
public class DialogoLogin extends JDialog {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblMensaje;
    private Usuario usuarioAutenticado;

    private final UsuarioService usuarioService;

    public DialogoLogin(Frame parent, UsuarioService usuarioService) {
        super(parent, AppConfig.NOMBRE_SISTEMA + " - Iniciar Sesión", true);
        this.usuarioService = usuarioService;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelar();
            }
        });

        setSize(440, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        add(crearContenido());

        getRootPane().registerKeyboardAction(
            e -> cancelar(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private JPanel crearContenido() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(20, 30, 20, 30));

        // ===== Encabezado con identidad MAGNO =====
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(AppConfig.NOMBRE_SISTEMA);
        titulo.setFont(new Font("Arial", Font.BOLD, 36));
        titulo.setForeground(new Color(0, 80, 160));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel(AppConfig.SLOGAN);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(100, 100, 100));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descripcion = new JLabel(AppConfig.DESCRIPCION_CORTA);
        descripcion.setFont(new Font("Arial", Font.ITALIC, 11));
        descripcion.setForeground(new Color(150, 150, 150));
        descripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel version = new JLabel("v" + AppConfig.VERSION);
        version.setFont(new Font("Arial", Font.ITALIC, 10));
        version.setForeground(Color.GRAY);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(3));
        encabezado.add(subtitulo);
        encabezado.add(Box.createVerticalStrut(2));
        encabezado.add(descripcion);
        encabezado.add(Box.createVerticalStrut(5));
        encabezado.add(version);

        // ===== Formulario =====
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formulario.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtUsuario = new JTextField(15);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        formulario.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formulario.add(txtPassword, gbc);

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

        panelPrincipal.add(encabezado, BorderLayout.NORTH);
        panelPrincipal.add(formulario, BorderLayout.CENTER);
        panelPrincipal.add(botones, BorderLayout.SOUTH);

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
            dispose();

        } catch (ValidacionException ex) {
            lblMensaje.setText("✗ " + ex.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private void cancelar() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea salir de " + AppConfig.NOMBRE_SISTEMA + "?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            usuarioAutenticado = null;
            dispose();
        }
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public static Usuario mostrar(Frame parent, UsuarioService usuarioService) {
        DialogoLogin dialog = new DialogoLogin(parent, usuarioService);
        dialog.setVisible(true);
        return dialog.getUsuarioAutenticado();
    }
}