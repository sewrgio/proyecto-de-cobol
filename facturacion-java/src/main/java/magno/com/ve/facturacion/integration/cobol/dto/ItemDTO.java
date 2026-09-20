package magno.com.ve.facturacion.integration.cobol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de un item de la factura para el Core COBOL.
 * JSON:
 * {
 *   "codigo": "PROD01",
 *   "cantidad": 2,
 *   "precio": 50.00,
 *   "alicuota": "G"
 * }
 *
 * alicuota: "G" = Gravado (IVA 16%), "E" = Exento
 */
public class ItemDTO {

    @JsonProperty("codigo")
    private String codigo;              // max 10 caracteres (PIC X(10))

    @JsonProperty("cantidad")
    private int cantidad;               // max 99999 (PIC 9(05))

    @JsonProperty("precio")
    private double precio;              // 9999999.99 (PIC 9(07)V99)

    @JsonProperty("alicuota")
    private String alicuota;            // "G" o "E" (PIC X(01))

    public ItemDTO() {}

    public ItemDTO(String codigo, int cantidad, double precio, String alicuota) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.alicuota = alicuota;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getAlicuota() { return alicuota; }
    public void setAlicuota(String alicuota) { this.alicuota = alicuota; }
}
