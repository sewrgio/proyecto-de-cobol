package magno.com.ve.facturacion.integration.cobol.dto;

import java.util.ArrayList;
import java.util.List;

public class FacturaRequestDTO {

    private String nombreSucursal;
    private String rifSucursal;
    private String direccionSucursal;
    private String ciudadSucursal;
    private String estadoSucursal;
    private String zonaPostal;

    private String cajeroCodigo;
    private String cajeroNombre;
    private String cajaNumero;

    private String rifCliente;
    private String razonSocial;

    private String formaPago;
    private String fechaEmision;
    private String horaEmision;
    private double montoPagado;

    private List<ItemDTO> items = new ArrayList<>();

    public FacturaRequestDTO() {}

    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }

    public String getRifSucursal() { return rifSucursal; }
    public void setRifSucursal(String rifSucursal) { this.rifSucursal = rifSucursal; }

    public String getDireccionSucursal() { return direccionSucursal; }
    public void setDireccionSucursal(String direccionSucursal) { this.direccionSucursal = direccionSucursal; }

    public String getCiudadSucursal() { return ciudadSucursal; }
    public void setCiudadSucursal(String ciudadSucursal) { this.ciudadSucursal = ciudadSucursal; }

    public String getEstadoSucursal() { return estadoSucursal; }
    public void setEstadoSucursal(String estadoSucursal) { this.estadoSucursal = estadoSucursal; }

    public String getZonaPostal() { return zonaPostal; }
    public void setZonaPostal(String zonaPostal) { this.zonaPostal = zonaPostal; }

    public String getCajeroCodigo() { return cajeroCodigo; }
    public void setCajeroCodigo(String cajeroCodigo) { this.cajeroCodigo = cajeroCodigo; }

    public String getCajeroNombre() { return cajeroNombre; }
    public void setCajeroNombre(String cajeroNombre) { this.cajeroNombre = cajeroNombre; }

    public String getCajaNumero() { return cajaNumero; }
    public void setCajaNumero(String cajaNumero) { this.cajaNumero = cajaNumero; }

    public String getRifCliente() { return rifCliente; }
    public void setRifCliente(String rifCliente) { this.rifCliente = rifCliente; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getHoraEmision() { return horaEmision; }
    public void setHoraEmision(String horaEmision) { this.horaEmision = horaEmision; }

    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }

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