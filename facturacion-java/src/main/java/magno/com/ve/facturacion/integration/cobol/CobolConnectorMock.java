package magno.com.ve.facturacion.integration.cobol;

import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;
import magno.com.ve.facturacion.integration.cobol.dto.ItemDTO;
import java.io.IOException;

/**
 * Implementación simulada del CobolConnector.
 * Usada durante el desarrollo mientras el servidor COBOL real no está listo.
 * Aplica las mismas reglas de negocio (IVA 16%, IGTF 3% si forma de pago es "DV").
 */
public class CobolConnectorMock implements CobolConnector {

    private static final double TASA_IVA = 0.16;
    private static final double TASA_IGTF = 0.03;
    private static long siguienteCorrelativo = 101L;

    @Override
    public FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException {

        FacturaResponseDTO response = new FacturaResponseDTO();

        // ===== Validaciones (mismo código de error que COBOL) =====
        if (request.getCliente() == null
            || request.getCliente().getCodigo() == null
            || request.getCliente().getCodigo().trim().isEmpty()) {
            response.setEstado("ERROR");
            response.setCodigoError(1);
            response.setMensaje("Falta el código del cliente");
            return response;
        }

        if (request.getCliente().getRif() == null
            || request.getCliente().getRif().trim().isEmpty()) {
            response.setEstado("ERROR");
            response.setCodigoError(2);
            response.setMensaje("Falta el RIF del cliente");
            return response;
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            response.setEstado("ERROR");
            response.setCodigoError(3);
            response.setMensaje("La factura no tiene items");
            return response;
        }

        // ===== Cálculos (réplica de CALC-FACTURA + CALC-SENIAT) =====
        double baseImponible = 0.0;
        double baseExento = 0.0;

        for (ItemDTO item : request.getItems()) {
            double totalLinea = item.getCantidad() * item.getPrecio();
            if ("G".equalsIgnoreCase(item.getAlicuota())) {
                baseImponible += totalLinea;
            } else {
                baseExento += totalLinea;
            }
        }

        double iva = baseImponible * TASA_IVA;
        double igtf = 0.0;

        // IGTF solo aplica si la forma de pago es "DV" (Divisas)
        if ("DV".equalsIgnoreCase(request.getFormaPago())) {
            igtf = (baseImponible + baseExento + iva) * TASA_IGTF;
        }

        double total = baseImponible + baseExento + iva + igtf;

        // ===== Simular correlativo =====
        long numFactura = siguienteCorrelativo++;
        String numControl = String.format("00-%08d", numFactura);

        // ===== Llenar respuesta =====
        response.setEstado("OK");
        response.setCodigoError(0);
        response.setNumeroFactura(numFactura);
        response.setNumeroControl(numControl);
        response.setBaseImponible(redondear(baseImponible));
        response.setBaseExento(redondear(baseExento));
        response.setIva(redondear(iva));
        response.setIgtf(redondear(igtf));
        response.setTotal(redondear(total));

        return response;
    }

    @Override
    public boolean estaDisponible() {
        return true; // El mock siempre está disponible
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}