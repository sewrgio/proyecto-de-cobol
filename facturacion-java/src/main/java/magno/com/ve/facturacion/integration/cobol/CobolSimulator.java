package magno.com.ve.corecobol;

import magno.com.ve.model.Producto;
import java.util.List;

public class CobolSimulator {
    
    public boolean verificarStock(String idProducto, int cantidad) {
        // Simulamos que P999 no tiene stock
        if (idProducto.equalsIgnoreCase("P999")) {
            return false;
        }
        return true;
    }

    public double[] calcularFactura(List<Producto> items) {
        // Devuelve [subtotal, iva, total]
        double subtotal = items.stream().mapToDouble(Producto::getSubtotal).sum();
        double iva = subtotal * 0.16; // IVA 16% (Venezuela)
        double total = subtotal + iva;
        return new double[]{subtotal, iva, total};
    }

    public String generarNumeroFactura() {
        return "FAC-" + System.currentTimeMillis();
    }
}