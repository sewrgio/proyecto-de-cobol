package magno.com.ve.facturacion.exception;

public class ValidacionException extends Exception {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }

    public ValidacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
