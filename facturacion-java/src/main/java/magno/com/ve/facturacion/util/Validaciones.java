package magno.com.ve.facturacion.util;

import java.util.regex.Pattern;

import magno.com.ve.facturacion.domain.enums.TipoContribuyente;

public class Validaciones {

    private static final Pattern PATRON_TELEFONO = Pattern.compile(
        "^(\\+58|0)?4\\d{2}[-. ]?\\d{3}[-. ]?\\d{2}[-. ]?\\d{2}$"
    );

    private static final Pattern PATRON_EMAIL = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final String[] PAISES_VALIDOS = {
        "Venezuela", "Colombia", "Ecuador", "Perú", "Brasil", "Argentina",
        "Chile", "México", "Estados Unidos", "China", "Japón", "Alemania",
        "España", "Italia", "Francia", "Reino Unido", "Canadá", "Corea del Sur",
        "India", "Vietnam", "Tailandia", "Taiwán"
    };

    // ===== CÉDULA / RIF =====
    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return false;
        String limpia = cedula.trim().toUpperCase().replace("-", "").replace(" ", "");
        if (limpia.length() < 8) return false;
        String prefijo = limpia.substring(0, 1);
        TipoContribuyente tipo = TipoContribuyente.fromPrefijo(prefijo);
        if (tipo == null) return false;
        String numero = limpia.substring(1);
        return numero.matches("\\d+") &&
               numero.length() >= tipo.getLongitudMinima() &&
               numero.length() <= tipo.getLongitudMaxima();
    }

    public static TipoContribuyente getTipoContribuyente(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return null;
        return TipoContribuyente.fromPrefijo(cedula.trim().substring(0, 1));
    }

    public static String normalizarCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return "";
        String limpia = cedula.trim().toUpperCase().replace("-", "").replace(" ", "");
        if (limpia.length() < 2) return limpia;
        return limpia.substring(0, 1) + "-" + limpia.substring(1);
    }

    // ===== TELÉFONO / EMAIL =====
    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) return true;
        return PATRON_TELEFONO.matcher(telefono.trim()).matches();
    }

    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return PATRON_EMAIL.matcher(email.trim()).matches();
    }

    // ===== UTILIDADES BÁSICAS =====
    public static boolean noEstaVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static boolean tieneLongitudMinima(String texto, int minimo) {
        return noEstaVacio(texto) && texto.trim().length() >= minimo;
    }

    public static boolean tieneLongitudMaxima(String texto, int maximo) {
        return texto == null || texto.trim().length() <= maximo;
    }

    public static boolean esSoloLetras(String texto) {
        if (texto == null || texto.trim().isEmpty()) return false;
        return texto.trim().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$");
    }

    // ===== PRODUCTO: CÓDIGOS =====
    public static boolean esCodigoProductoValido(String codigo) {
        if (codigo == null) return false;
        return codigo.trim().matches("^\\d{12}$");
    }

    public static boolean esCodigoFabricacionValido(String codigo) {
        if (codigo == null) return false;
        return codigo.trim().toUpperCase().matches("^[A-Z0-9-]{4,20}$");
    }

    // ===== NÚMEROS =====
    public static boolean esNumeroPositivo(Double valor) {
        return valor != null && valor > 0;
    }

    public static boolean esNumeroNoNegativo(Double valor) {
        return valor != null && valor >= 0;
    }

    public static boolean esPrecioValido(double precio) {
        return precio > 0;
    }

    public static boolean esCantidadValida(int cantidad) {
        return cantidad > 0;
    }

    // ===== FABRICACIÓN =====
    public static boolean esCompaniaValida(String compania) {
        if (compania == null || compania.trim().isEmpty()) return false;
        return compania.trim().matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ.,&\\-\\s]{2,100}$");
    }

    public static boolean esPaisValido(String pais) {
        if (pais == null || pais.trim().isEmpty()) return false;
        for (String p : PAISES_VALIDOS) {
            if (p.equalsIgnoreCase(pais.trim())) return true;
        }
        return false;
    }

    public static String[] getPaisesValidos() {
        return PAISES_VALIDOS.clone();
    }
}