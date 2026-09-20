package magno.com.ve.facturacion.ui;

import magno.com.ve.facturacion.App;
import magno.com.ve.facturacion.domain.enums.Rol;
import magno.com.ve.facturacion.domain.model.Usuario;
import magno.com.ve.facturacion.ui.panel.PanelAlmacen;
import magno.com.ve.facturacion.ui.panel.PanelCliente;
import magno.com.ve.facturacion.ui.panel.PanelFacturacion;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final Usuario usuarioActual;

    public VentanaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle("Sistema de Facturación - Java + COBOL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar
        }

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPestanas(), BorderLayout.CENTER);
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        barra.setBackground(new Color(240, 240, 240));

        JLabel lblUsuario = new JLabel(
            "👤 " + usuarioActual.getNombreCompleto()
            + "  |  Rol: " + (usuarioActual.getRol() != null
                ? usuarioActual.getRol().getNombre() : "—")
        );
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        barra.add(lblUsuario, BorderLayout.WEST);

        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        barra.add(btnCerrarSesion, BorderLayout.EAST);

        return barra;
    }

    private JTabbedPane crearPestanas() {
        JTabbedPane pestanas = new JTabbedPane();
        Rol rol = usuarioActual.getRol();

        if (rol == Rol.ADMINISTRADOR) {
            pestanas.addTab("📦 Almacén", new PanelAlmacen());
            pestanas.addTab("🧾 Facturación", new PanelFacturacion());
            pestanas.addTab("👤 Clientes", new PanelCliente());
        }
        else if (rol == Rol.CAJERO) {
            pestanas.addTab("🧾 Facturación", new PanelFacturacion());
        }
        else if (rol == Rol.ALMACENISTA) {
            pestanas.addTab("📦 Almacén", new PanelAlmacen());
        }
        else {
            JPanel sinAcceso = new JPanel(new GridBagLayout());
            sinAcceso.add(new JLabel("⚠ No tiene permisos para acceder al sistema"));
            pestanas.addTab("Sin acceso", sinAcceso);
        }

        return pestanas;
    }

    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar sesión?",
            "Cerrar sesión",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            App.main(new String[0]);
        }
    }
}