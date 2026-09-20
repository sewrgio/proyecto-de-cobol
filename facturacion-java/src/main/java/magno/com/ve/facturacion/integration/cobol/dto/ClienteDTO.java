package magno.com.ve.facturacion.integration.cobol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO del cliente para la comunicación con el Core COBOL.
 * El JSON resultante tiene este formato:
 * {
 *   "codigo": "CLI001",
 *   "rif": "J-123456789",
 *   "razonSocial": "EMPRESA DE PRUEBA C.A."
 * }
 */
public class ClienteDTO {

    @JsonProperty("codigo")
    private String codigo;              // max 10 caracteres (PIC X(10))

    @JsonProperty("rif")
    private String rif;                 // max 12 caracteres (PIC X(12))

    @JsonProperty("razonSocial")
    private String razonSocial;         // max 40 caracteres (PIC X(40))

    public ClienteDTO() {}

    public ClienteDTO(String codigo, String rif, String razonSocial) {
        this.codigo = codigo;
        this.rif = rif;
        this.razonSocial = razonSocial;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getRif() { return rif; }
    public void setRif(String rif) { this.rif = rif; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
}