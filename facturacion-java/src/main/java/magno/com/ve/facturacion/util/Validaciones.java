package magno.com.ve.facturacion.util;

import magno.com.ve.facturacion.domain.enums.TipoContribuyente;
import java.util.regex.Pattern;

public class Validaciones {

    private static final Pattern PATRON_TELEFONO = Pattern.compile(
        "^(\\+58|0)?4\\d{2}[-. ]?\\d{3}[-. ]?\\d{2}[-. ]?\\d{2}$"
    );

    private static final Pattern PATRON_EMAIL = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

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

    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) return true;
        return PATRON_TELEFONO.matcher(telefono.trim()).matches();
    }

    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return PATRON_EMAIL.matcher(email.trim()).matches();
    }

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
}
