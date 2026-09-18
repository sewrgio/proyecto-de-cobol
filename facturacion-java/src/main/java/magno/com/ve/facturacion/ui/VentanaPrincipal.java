package magno.com.ve.facturacion.ui;

import magno.com.ve.facturacion.ui.panel.PanelCliente;
import magno.com.ve.facturacion.ui.panel.PanelFacturacion;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("Sistema de Facturación - Java + COBOL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar
        }

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Clientes", new PanelCliente());
        pestanas.addTab("Facturación", new PanelFacturacion());

        add(pestanas);
    }
}