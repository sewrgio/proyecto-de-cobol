package magno.com.ve.facturacion.domain.model;

/**
 * Producto del inventario leído desde inventario.dat (generado por COBOL).
 */
public class ProductoInventario {
    private String codigo;
    private String nombre;
    private double precio;
    private int stock;
    private String alicuota;   // "G" = Gravado, "E" = Exento
    private String categoria;

    public ProductoInventario() {}

    public ProductoInventario(String codigo, String nombre, double precio,
                              int stock, String alicuota, String categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.alicuota = alicuota;
        this.categoria = categoria;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getAlicuota() { return alicuota; }
    public void setAlicuota(String alicuota) { this.alicuota = alicuota; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return String.format("%s - %s - Bs %.2f", codigo, nombre, precio);
    }
}
