package magno.com.ve.ui;

import javax.swing.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("Sistema de Facturación - Java + COBOL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null); // Centrar en pantalla

        // Aplicar estilo moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar, usar estilo por defecto
        }

        add(new PanelFacturacion());
    }
}