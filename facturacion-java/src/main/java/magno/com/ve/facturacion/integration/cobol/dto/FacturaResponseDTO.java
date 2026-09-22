package magno.com.ve.facturacion.integration.cobol.dto;

public class FacturaResponseDTO {

    private String estado;
    private int codigoError;
    private String mensaje;
    private String numeroControl;
    private long numeroFactura;
    private double baseImponible;
    private double baseExento;
    private double iva;
    private double igtf;
    private double total;

    public FacturaResponseDTO() {}

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getCodigoError() { return codigoError; }
    public void setCodigoError(int codigoError) { this.codigoError = codigoError; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getNumeroControl() { return numeroControl; }
    public void setNumeroControl(String numeroControl) { this.numeroControl = numeroControl; }
    public long getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(long numeroFactura) { this.numeroFactura = numeroFactura; }
    public double getBaseImponible() { return baseImponible; }
    public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }
    public double getBaseExento() { return baseExento; }
    public void setBaseExento(double baseExento) { this.baseExento = baseExento; }
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    public double getIgtf() { return igtf; }
    public void setIgtf(double igtf) { this.igtf = igtf; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public boolean esExitosa() {
        return "OK".equalsIgnoreCase(estado) && codigoError == 0;
    }
}