package magno.com.ve.facturacion.domain.model;

public class ItemFactura {
    private final Producto producto;
    private int cantidad;

    public ItemFactura(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }

    public void sumarCantidad(int extra) {
        setCantidad(this.cantidad + extra);
    }

    @Override
    public String toString() {
        return cantidad + " x " + producto.getNombre() + " = " + getSubtotal();
    }
}