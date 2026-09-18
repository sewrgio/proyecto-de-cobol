package magno.com.ve.facturacion.integration.cobol;

import magno.com.ve.facturacion.domain.model.Producto;
import java.util.List;

public class CobolSimulator {

    public boolean verificarStock(String idProducto, int cantidad) {
        if (idProducto.equalsIgnoreCase("P999")) {
            return false;
        }
        return true;
    }

    public double[] calcularFactura(List<Producto> items) {
        double subtotal = items.stream().mapToDouble(Producto::getSubtotal).sum();
        double iva = subtotal * 0.16;
        double total = subtotal + iva;
        return new double[]{subtotal, iva, total};
    }

    public String generarNumeroFactura() {
        return "FAC-" + System.currentTimeMillis();
    }
}
