package magno.com.ve.facturacion.domain.facturacion;

import java.nio.file.Path;

public class ResultadoFactura {

    private boolean exitosa;
    private String numeroFactura;
    private String numeroControl;
    private Path archivoGenerado;
    private String mensaje;
    private String codigoError;

    public ResultadoFactura() {}

    public static ResultadoFactura exito(String numeroFactura, String numeroControl, Path archivo) {
        ResultadoFactura r = new ResultadoFactura();
        r.exitosa = true;
        r.numeroFactura = numeroFactura;
        r.numeroControl = numeroControl;
        r.archivoGenerado = archivo;
        r.mensaje = "Factura emitida correctamente";
        return r;
    }

    public static ResultadoFactura error(String codigoError, String mensaje) {
        ResultadoFactura r = new ResultadoFactura();
        r.exitosa = false;
        r.codigoError = codigoError;
        r.mensaje = mensaje;
        return r;
    }

    public boolean isExitosa() { return exitosa; }
    public String getNumeroFactura() { return numeroFactura; }
    public String getNumeroControl() { return numeroControl; }
    public Path getArchivoGenerado() { return archivoGenerado; }
    public String getMensaje() { return mensaje; }
    public String getCodigoError() { return codigoError; }

    public void setExitosa(boolean exitosa) { this.exitosa = exitosa; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public void setNumeroControl(String numeroControl) { this.numeroControl = numeroControl; }
    public void setArchivoGenerado(Path archivoGenerado) { this.archivoGenerado = archivoGenerado; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setCodigoError(String codigoError) { this.codigoError = codigoError; }

    @Override
    public String toString() {
        if (exitosa) {
            return "✅ Factura " + numeroFactura + " (Control: " + numeroControl + ")\n"
                 + "   Archivo: " + archivoGenerado;
        } else {
            return "❌ Error [" + codigoError + "]: " + mensaje;
        }
    }
}
