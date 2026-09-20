package magno.com.ve.facturacion.integration.cobol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de la respuesta del Core COBOL.
 * Este es el JSON que Java recibe del Core.
 *
 * Respuesta exitosa:
 * {
 *   "estado": "OK",
 *   "codigoError": 0,
 *   "numeroControl": "00-00000101",
 *   "numeroFactura": 101,
 *   "baseImponible": 100.00,
 *   "baseExento": 20.00,
 *   "iva": 16.00,
 *   "igtf": 4.08,
 *   "total": 140.08
 * }
 *
 * Respuesta con error:
 * {
 *   "estado": "ERROR",
 *   "codigoError": 1,
 *   "mensaje": "Falta el código del cliente"
 * }
 */
public class FacturaResponseDTO {

    @JsonProperty("estado")
    private String estado;              // "OK" o "ERROR"

    @JsonProperty("codigoError")
    private int codigoError;            // 0 = OK, otros = error

    @JsonProperty("mensaje")
    private String mensaje;             // Solo si hay error

    @JsonProperty("numeroControl")
    private String numeroControl;       // "00-00000101"

    @JsonProperty("numeroFactura")
    private long numeroFactura;         // 101

    @JsonProperty("baseImponible")
    private double baseImponible;

    @JsonProperty("baseExento")
    private double baseExento;

    @JsonProperty("iva")
    private double iva;

    @JsonProperty("igtf")
    private double igtf;

    @JsonProperty("total")
    private double total;

    public FacturaResponseDTO() {}

    // ===== Getters y Setters =====
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

    /**
     * Devuelve true si la factura se procesó correctamente.
     */
    public boolean esExitosa() {
        return "OK".equalsIgnoreCase(estado) && codigoError == 0;
    }
}