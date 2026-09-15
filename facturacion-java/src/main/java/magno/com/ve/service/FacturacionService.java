package magno.com.ve.service;

import magno.com.ve.corecobol.CobolSimulator;
import magno.com.ve.model.Producto;
import java.util.List;

public class FacturacionService {
    
    private final CobolSimulator cobol = new CobolSimulator();

    public void procesarPedido(String cliente, List<Producto> items) {
        System.out.println("--- Iniciando Facturación ---");
        
        for (Producto p : items) {
            if (!cobol.verificarStock(p.getId(), 1)) {
                System.out.println("❌ Sin stock: " + p.getNombre());
                return;
            }
        }

        String factura = cobol.generarFactura(cliente, items);
        double total = items.stream().mapToDouble(Producto::getPrecio).sum();
        
        System.out.println("✅ Factura: " + factura);
        System.out.println("💰 Total: $" + total);
        System.out.println("--- Proceso Finalizado ---");
    }
}