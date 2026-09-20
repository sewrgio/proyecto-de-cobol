package magno.com.ve.facturacion.integration.cobol;

/**
 * Excepción lanzada cuando el Core COBOL devuelve un error de negocio.
 */
public class CobolException extends Exception {

    private final int codigoError;

    public CobolException(String mensaje, int codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public int getCodigoError() {
        return codigoError;
    }
}