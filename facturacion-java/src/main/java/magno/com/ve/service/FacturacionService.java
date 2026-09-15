package magno.com.ve.service;

import magno.com.ve.corecobol.CobolSimulator;
import magno.com.ve.model.Producto;
import java.util.List;

public class FacturacionService {
    
    private final CobolSimulator cobol = new CobolSimulator();

    public String procesarPedido(String cliente, List<Producto> items) {
        StringBuilder log = new StringBuilder();
        log.append("--- Iniciando Facturación ---\n");
        
        // Paso 1: Verificar stock (llamada a COBOL)
        for (Producto p : items) {
            log.append("[SIMULADOR COBOL] Verificando stock: ").append(p.getId()).append("\n");
            if (!cobol.verificarStock(p.getId(), p.getCantidad())) {
                log.append("❌ ERROR: Sin stock para ").append(p.getNombre()).append("\n");
                return log.toString();
            }
        }
        log.append("✅ Stock verificado correctamente.\n");

        // Paso 2: Calcular factura (llamada a COBOL)
        log.append("[SIMULADOR COBOL] Calculando subtotal, IVA y total...\n");
        double[] totales = cobol.calcularFactura(items);
        
        // Paso 3: Generar número de factura (llamada a COBOL)
        String numeroFactura = cobol.generarNumeroFactura();
        log.append("[SIMULADOR COBOL] Factura generada: ").append(numeroFactura).append("\n");
        
        // Mostrar resultado
        log.append("\n=========================================\n");
        log.append("           FACTURA GENERADA              \n");
        log.append("=========================================\n");
        log.append("Cliente:  ").append(cliente).append("\n");
        log.append("Factura:  ").append(numeroFactura).append("\n");
        log.append("-----------------------------------------\n");
        for (Producto p : items) {
            log.append(String.format("%-20s x%d  $%.2f\n", 
                p.getNombre(), p.getCantidad(), p.getSubtotal()));
        }
        log.append("-----------------------------------------\n");
        log.append(String.format("Subtotal: $%.2f\n", totales[0]));
        log.append(String.format("IVA 16%%: $%.2f\n", totales[1]));
        log.append(String.format("TOTAL:    $%.2f\n", totales[2]));
        log.append("=========================================\n");
        
        return log.toString();
    }
}