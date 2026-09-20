package magno.com.ve.facturacion.integration.cobol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO completo de la solicitud de facturación.
 * Este es el JSON que Java envía al Core COBOL.
 *
 * JSON:
 * {
 *   "cliente": { ... },
 *   "formaPago": "DV",
 *   "fechaEmision": "20260919",
 *   "items": [ ... ]
 * }
 *
 * formaPago: "CO" = Contado, "DV" = Divisa, "TR" = Transferencia, "CR" = Crédito
 * fechaEmision: formato AAAAMMDD (PIC 9(08))
 * items: máximo 10 items (OCCURS 10 TIMES en COBOL)
 */
public class FacturaRequestDTO {

    @JsonProperty("cliente")
    private ClienteDTO cliente;

    @JsonProperty("formaPago")
    private String formaPago;           // "CO", "DV", "TR", "CR"

    @JsonProperty("fechaEmision")
    private String fechaEmision;        // "AAAAMMDD" (ej: "20260919")

    @JsonProperty("items")
    private List<ItemDTO> items = new ArrayList<>();

    public FacturaRequestDTO() {}

    public ClienteDTO getCliente() { return cliente; }
    public void setCliente(ClienteDTO cliente) { this.cliente = cliente; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }

    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }

    public void addItem(ItemDTO item) {
        if (this.items.size() < 10) {
            this.items.add(item);
        } else {
            throw new IllegalStateException("Máximo 10 items por factura");
        }
    }
}