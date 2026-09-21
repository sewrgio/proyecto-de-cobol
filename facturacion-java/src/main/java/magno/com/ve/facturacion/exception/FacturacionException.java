package magno.com.ve.facturacion.exception;

public class FacturacionException extends Exception {

    private final String codigo;

    public FacturacionException(String mensaje) {
        super(mensaje);
        this.codigo = "ERR";
    }

    public FacturacionException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public FacturacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = "ERR";
    }

    public String getCodigo() {
        return codigo;
    }
}