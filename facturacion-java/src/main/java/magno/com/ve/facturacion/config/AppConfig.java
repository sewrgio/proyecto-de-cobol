package magno.com.ve.facturacion.config;

import java.awt.Color;
import java.awt.Font;

/**
 * Configuración global de MAGNO + Sistema de diseño (Design System).
 */
public class AppConfig {

    // ===== Identidad del sistema =====
    public static final String NOMBRE_SISTEMA = "MAGNO";
    public static final String SLOGAN = "Suite de Gestión Empresarial";
    public static final String DESCRIPCION_CORTA = "Todos los procesos de tu empresa, en un solo lugar.";
    public static final String VERSION = "1.0.0";

    // ===== COLORES (Design System) =====
    public static final Color COLOR_PRIMARIO          = new Color(30, 58, 138);    // #1E3A8A
    public static final Color COLOR_PRIMARIO_HOVER    = new Color(30, 64, 175);    // #1E40AF
    public static final Color COLOR_ACENTO            = new Color(6, 182, 212);    // #06B6D4
    public static final Color COLOR_FONDO_PRINCIPAL   = new Color(248, 250, 252);  // #F8FAFC
    public static final Color COLOR_FONDO_TARJETA     = new Color(255, 255, 255);  // #FFFFFF
    public static final Color COLOR_BORDE             = new Color(226, 232, 240);  // #E2E8F0
    public static final Color COLOR_TEXTO_PRINCIPAL   = new Color(15, 23, 42);     // #0F172A
    public static final Color COLOR_TEXTO_SECUNDARIO  = new Color(100, 116, 139);  // #64748B
    public static final Color COLOR_EXITO             = new Color(16, 185, 129);   // #10B981
    public static final Color COLOR_ERROR             = new Color(239, 68, 68);    // #EF4444
    public static final Color COLOR_ADVERTENCIA       = new Color(245, 158, 11);   // #F59E0B

    // ===== FUENTES (Design System) =====
    public static final Font FUENTE_TITULO_GRANDE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FUENTE_TITULO        = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_SUBTITULO     = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_TEXTO         = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_PEQUENA       = new Font("Segoe UI", Font.PLAIN, 11);

    // ===== ESPACIADO =====
    public static final int ESPACIO_XS = 4;
    public static final int ESPACIO_S  = 8;
    public static final int ESPACIO_M  = 16;
    public static final int ESPACIO_L  = 24;
    public static final int ESPACIO_XL = 32;

    // ===== RADIOS DE BORDE =====
    public static final int RADIO_S = 4;
    public static final int RADIO_M = 8;
    public static final int RADIO_L = 12;

    private AppConfig() {
        throw new UnsupportedOperationException("Clase de utilidades, no se instancia.");
    }
}