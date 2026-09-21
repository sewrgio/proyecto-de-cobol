package magno.com.ve.facturacion.domain.facturacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatosFactura {

    private String emisorNombre;
    private String emisorRif;
    private String emisorDireccion;
    private String emisorCiudad;
    private String emisorEstado;
    private String emisorZonaPostal;
    private String emisorTelefono;

    private String clienteRif;
    private String clienteRazonSocial;
    private String clienteDireccion;

    private String numeroFactura;
    private String numeroControl;
    private LocalDateTime fechaEmision;
    private String cajeroNombre;
    private String cajeroCodigo;
    private String cajaNumero;

    private double baseImponible;
    private double baseExento;
    private double iva;
    private double igtf;
    private double descuento;
    private double total;

    private String formaPago;
    private double montoPagado;
    private double vuelto;

    private List<ItemFactura> items = new ArrayList<>();

    public DatosFactura() {}

    public static class ItemFactura {
        private String codigo;
        private String nombre;
        private int cantidad;
        private double precioUnitario;
        private String alicuota;
        private double totalLinea;

        public ItemFactura() {}

        public ItemFactura(String codigo, String nombre, int cantidad,
                           double precioUnitario, String alicuota, double totalLinea) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.alicuota = alicuota;
            this.totalLinea = totalLinea;
        }

        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
        public String getAlicuota() { return alicuota; }
        public void setAlicuota(String alicuota) { this.alicuota = alicuota; }
        public double getTotalLinea() { return totalLinea; }
        public void setTotalLinea(double totalLinea) { this.totalLinea = totalLinea; }
    }

    // Getters y Setters
    public String getEmisorNombre() { return emisorNombre; }
    public void setEmisorNombre(String emisorNombre) { this.emisorNombre = emisorNombre; }
    public String getEmisorRif() { return emisorRif; }
    public void setEmisorRif(String emisorRif) { this.emisorRif = emisorRif; }
    public String getEmisorDireccion() { return emisorDireccion; }
    public void setEmisorDireccion(String emisorDireccion) { this.emisorDireccion = emisorDireccion; }
    public String getEmisorCiudad() { return emisorCiudad; }
    public void setEmisorCiudad(String emisorCiudad) { this.emisorCiudad = emisorCiudad; }
    public String getEmisorEstado() { return emisorEstado; }
    public void setEmisorEstado(String emisorEstado) { this.emisorEstado = emisorEstado; }
    public String getEmisorZonaPostal() { return emisorZonaPostal; }
    public void setEmisorZonaPostal(String emisorZonaPostal) { this.emisorZonaPostal = emisorZonaPostal; }
    public String getEmisorTelefono() { return emisorTelefono; }
    public void setEmisorTelefono(String emisorTelefono) { this.emisorTelefono = emisorTelefono; }
    public String getClienteRif() { return clienteRif; }
    public void setClienteRif(String clienteRif) { this.clienteRif = clienteRif; }
    public String getClienteRazonSocial() { return clienteRazonSocial; }
    public void setClienteRazonSocial(String clienteRazonSocial) { this.clienteRazonSocial = clienteRazonSocial; }
    public String getClienteDireccion() { return clienteDireccion; }
    public void setClienteDireccion(String clienteDireccion) { this.clienteDireccion = clienteDireccion; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getNumeroControl() { return numeroControl; }
    public void setNumeroControl(String numeroControl) { this.numeroControl = numeroControl; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getCajeroNombre() { return cajeroNombre; }
    public void setCajeroNombre(String cajeroNombre) { this.cajeroNombre = cajeroNombre; }
    public String getCajeroCodigo() { return cajeroCodigo; }
    public void setCajeroCodigo(String cajeroCodigo) { this.cajeroCodigo = cajeroCodigo; }
    public String getCajaNumero() { return cajaNumero; }
    public void setCajaNumero(String cajaNumero) { this.cajaNumero = cajaNumero; }
    public double getBaseImponible() { return baseImponible; }
    public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }
    public double getBaseExento() { return baseExento; }
    public void setBaseExento(double baseExento) { this.baseExento = baseExento; }
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    public double getIgtf() { return igtf; }
    public void setIgtf(double igtf) { this.igtf = igtf; }
    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }
    public double getVuelto() { return vuelto; }
    public void setVuelto(double vuelto) { this.vuelto = vuelto; }
    public List<ItemFactura> getItems() { return items; }
    public void setItems(List<ItemFactura> items) { this.items = items; }
    public void addItem(ItemFactura item) { this.items.add(item); }
}
