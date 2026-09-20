package magno.com.ve.facturacion.ui;

import magno.com.ve.facturacion.App;
import magno.com.ve.facturacion.config.AppConfig;
import magno.com.ve.facturacion.domain.enums.Rol;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.ui.panel.PanelAlmacen;
import magno.com.ve.facturacion.ui.panel.PanelCliente;
import magno.com.ve.facturacion.ui.panel.PanelFacturacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaPrincipal extends JFrame {

    private final Usuario usuarioActual;
    private JTabbedPane panelModulos;
    private JLabel lblEstado;

    public VentanaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle(AppConfig.NOMBRE_SISTEMA + " | " + AppConfig.SLOGAN);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1024, 700));
        getContentPane().setBackground(AppConfig.COLOR_FONDO_PRINCIPAL);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        setLayout(new BorderLayout());
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelModulos(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    // ============================================================
    //  BARRA SUPERIOR MODERNA
    // ============================================================
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(AppConfig.COLOR_PRIMARIO);
        barra.setBorder(new EmptyBorder(0, 24, 0, 24));
        barra.setPreferredSize(new Dimension(0, 64));

        // ===== Izquierda: Logo + Empresa =====
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);

        JLabel lblLogo = new JLabel("MAGNO");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(new EmptyBorder(18, 0, 18, 0));

        JLabel lblSeparador = new JLabel("  │  ");
        lblSeparador.setForeground(new Color(255, 255, 255, 80));
        lblSeparador.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JLabel lblEmpresa = new JLabel("Empresa Demo");
        lblEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblEmpresa.setForeground(AppConfig.COLOR_ACENTO);

        izquierda.add(lblLogo);
        izquierda.add(lblSeparador);
        izquierda.add(lblEmpresa);

        // ===== Derecha: Usuario + Rol + Cerrar sesión =====
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        derecha.setOpaque(false);

        JLabel lblUsuario = new JLabel(usuarioActual.getNombreCompleto());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUsuario.setForeground(Color.WHITE);

        JLabel lblRol = new JLabel(
            (usuarioActual.getRol() != null ? usuarioActual.getRol().getNombre() : "")
        );
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRol.setForeground(AppConfig.COLOR_ACENTO);
        lblRol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(6, 182, 212, 120), 1),
            new EmptyBorder(3, 10, 3, 10)
        ));

        JButton btnCerrarSesion = crearBotonCerrarSesion();

        derecha.add(lblUsuario);
        derecha.add(lblRol);
        derecha.add(Box.createHorizontalStrut(4));
        derecha.add(btnCerrarSesion);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    /**
     * Crea un botón de "Cerrar sesión" blanco con texto negro y efecto hover.
     */
    private JButton crearBotonCerrarSesion() {
        final Color FONDO_NORMAL  = Color.WHITE;
        final Color FONDO_HOVER   = new Color(241, 245, 249);   // #F1F5F9
        final Color FONDO_PRESSED = new Color(226, 232, 240);   // #E2E8F0
        final Color TEXTO         = new Color(15, 23, 42);      // #0F172A

        JButton btn = new JButton("Cerrar sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(TEXTO);
        btn.setBackground(FONDO_NORMAL);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(FONDO_HOVER);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(FONDO_NORMAL);
                btn.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(FONDO_PRESSED);
                btn.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(btn.contains(e.getPoint()) ? FONDO_HOVER : FONDO_NORMAL);
                btn.repaint();
            }
        });

        btn.addActionListener(e -> cerrarSesion());
        return btn;
    }

    // ============================================================
    //  PANEL DE MÓDULOS
    // ============================================================
    private JTabbedPane crearPanelModulos() {
        panelModulos = new JTabbedPane();
        panelModulos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelModulos.setBackground(AppConfig.COLOR_FONDO_PRINCIPAL);
        panelModulos.setBorder(new EmptyBorder(0, 0, 0, 0));

        UIManager.put("TabbedPane.selected", AppConfig.COLOR_FONDO_TARJETA);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabsOverlapBorder", true);

        Rol rol = usuarioActual.getRol();

        if (rol == Rol.ADMINISTRADOR) {
            panelModulos.addTab("  Almacén  ", new PanelAlmacen());
            panelModulos.addTab("  Facturación  ", new PanelFacturacion());
            panelModulos.addTab("  Clientes  ", new PanelCliente());
            panelModulos.addTab("  Reportes  ", crearPanelProximamente("Reportes"));
            panelModulos.addTab("  Configuración  ", crearPanelProximamente("Configuración"));
        }
        else if (rol == Rol.CAJERO) {
            panelModulos.addTab("  Facturación  ", new PanelFacturacion());
        }
        else if (rol == Rol.ALMACENISTA) {
            panelModulos.addTab("  Almacén  ", new PanelAlmacen());
        }
        else {
            panelModulos.addTab("  Sin acceso  ",
                crearPanelProximamente("Su usuario no tiene permisos asignados"));
        }

        panelModulos.addChangeListener(e -> {
            int idx = panelModulos.getSelectedIndex();
            if (idx >= 0) {
                actualizarEstado("Módulo: " + panelModulos.getTitleAt(idx).trim());
            }
        });

        return panelModulos;
    }

    private JPanel crearPanelProximamente(String tituloModulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppConfig.COLOR_FONDO_PRINCIPAL);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);

        JLabel lblIcono = new JLabel("🚧");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(tituloModulo);
        lblTitulo.setFont(AppConfig.FUENTE_TITULO);
        lblTitulo.setForeground(AppConfig.COLOR_PRIMARIO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMensaje = new JLabel("Este módulo estará disponible próximamente");
        lblMensaje.setFont(AppConfig.FUENTE_TEXTO);
        lblMensaje.setForeground(AppConfig.COLOR_TEXTO_SECUNDARIO);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(lblIcono);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(lblTitulo);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(lblMensaje);

        panel.add(contenido);
        return panel;
    }

    // ============================================================
    //  BARRA DE ESTADO
    // ============================================================
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(AppConfig.COLOR_FONDO_TARJETA);
        barra.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, AppConfig.COLOR_BORDE),
            new EmptyBorder(6, 20, 6, 20)
        ));

        lblEstado = new JLabel("Listo");
        lblEstado.setFont(AppConfig.FUENTE_PEQUENA);
        lblEstado.setForeground(AppConfig.COLOR_TEXTO_SECUNDARIO);

        JLabel lblVersion = new JLabel(
            AppConfig.NOMBRE_SISTEMA + " v" + AppConfig.VERSION
            + "  ·  © " + java.time.Year.now().getValue() + " " + AppConfig.NOMBRE_SISTEMA
        );
        lblVersion.setFont(AppConfig.FUENTE_PEQUENA);
        lblVersion.setForeground(AppConfig.COLOR_TEXTO_SECUNDARIO);

        barra.add(lblEstado, BorderLayout.WEST);
        barra.add(lblVersion, BorderLayout.EAST);

        return barra;
    }

    public void actualizarEstado(String mensaje) {
        if (lblEstado != null) {
            lblEstado.setText(mensaje);
        }
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar sesión?",
            "Cerrar sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            App.main(new String[0]);
        }
    }
}