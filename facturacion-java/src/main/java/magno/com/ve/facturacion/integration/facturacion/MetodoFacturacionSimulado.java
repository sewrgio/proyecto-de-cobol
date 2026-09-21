package magno.com.ve.facturacion.integration.facturacion;

import magno.com.ve.facturacion.domain.facturacion.DatosFactura;
import magno.com.ve.facturacion.domain.facturacion.MetodoFacturacion;
import magno.com.ve.facturacion.domain.facturacion.ResultadoFactura;
import magno.com.ve.facturacion.exception.FacturacionException;

public class MetodoFacturacionSimulado implements MetodoFacturacion {

    private static long contador = 1000L;

    @Override
    public ResultadoFactura emitir(DatosFactura datos) throws FacturacionException {
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        long num = ++contador;
        String numeroFactura = String.format("%08d", num);
        String numeroControl = String.format("00-%08d", num);

        System.out.println("[SIMULADO] Factura emitida: " + numeroFactura
                + " | Control: " + numeroControl);

        return ResultadoFactura.exito(numeroFactura, numeroControl, null);
    }

    @Override
    public String getNombre() { return "Simulado (desarrollo)"; }

    @Override
    public boolean estaDisponible() { return true; }
}
