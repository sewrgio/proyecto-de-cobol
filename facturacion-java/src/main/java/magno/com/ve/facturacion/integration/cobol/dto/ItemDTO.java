package magno.com.ve.facturacion.integration.cobol.dto;

public class ItemDTO {

    private String codigo;
    private String nombre;
    private int cantidad;
    private double precio;
    private String alicuota;

    public ItemDTO() {}

    public ItemDTO(String codigo, String nombre, int cantidad, double precio, String alicuota) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.alicuota = alicuota;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public String getAlicuota() { return alicuota; }
    public void setAlicuota(String alicuota) { this.alicuota = alicuota; }
}