package magno.com.ve.corecobol;

import magno.com.ve.model.Producto;
import java.util.List;

public class CobolSimulator {
    
    public boolean verificarStock(String idProducto, int cantidad) {
        System.out.println("[SIMULADOR COBOL] Verificando stock para: " + idProducto);
        return true;
    }

    public String generarFactura(String cliente, List<Producto> items) {
        System.out.println("[SIMULADOR COBOL] Calculando totales para: " + cliente);
        return "FAC-" + System.currentTimeMillis();
    }
}