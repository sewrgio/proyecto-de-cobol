package magno.com.ve.facturacion.service;

import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.integration.cobol.CobolSimulator;
import java.util.List;

public class FacturacionService {

    private final CobolSimulator cobol = new CobolSimulator();

    public String procesarPedido(Cliente cliente, List<Producto> items) {
        StringBuilder log = new StringBuilder();
        log.append("--- Iniciando Facturación ---\n");

        for (Producto p : items) {
            log.append("[SIMULADOR COBOL] Verificando stock: ").append(p.getId()).append("\n");
            if (!cobol.verificarStock(p.getId(), p.getCantidad())) {
                log.append("❌ ERROR: Sin stock para ").append(p.getNombre()).append("\n");
                return log.toString();
            }
        }
        log.append("✅ Stock verificado correctamente.\n");

        log.append("[SIMULADOR COBOL] Calculando subtotal, IVA y total...\n");
        double[] totales = cobol.calcularFactura(items);

        String numeroFactura = cobol.generarNumeroFactura();
        log.append("[SIMULADOR COBOL] Factura generada: ").append(numeroFactura).append("\n");

        log.append("\n=========================================\n");
        log.append("           FACTURA GENERADA              \n");
        log.append("=========================================\n");
        log.append("Cliente:  ").append(cliente.getNombreCompleto()).append("\n");
        log.append("Cédula:   ").append(cliente.getCedula()).append("\n");
        log.append("Dirección:").append(cliente.getDireccion().getDireccionCompleta()).append("\n");
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
