package magno.com.ve.facturacion.integration.cobol;

import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;
import magno.com.ve.facturacion.integration.cobol.dto.ItemDTO;
import java.io.IOException;

public class CobolConnectorMock implements CobolConnector {

    private static final double TASA_IVA = 0.16;
    private static final double TASA_IGTF = 0.03;
    private static long siguienteCorrelativo = 101L;

    @Override
    public FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException {

        FacturaResponseDTO response = new FacturaResponseDTO();

        if (request.getRifCliente() == null || request.getRifCliente().trim().isEmpty()) {
            response.setEstado("ERROR");
            response.setCodigoError(1);
            response.setMensaje("Falta el RIF del cliente");
            return response;
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            response.setEstado("ERROR");
            response.setCodigoError(3);
            response.setMensaje("La factura no tiene items");
            return response;
        }

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
        if ("DV".equalsIgnoreCase(request.getFormaPago())) {
            igtf = (baseImponible + baseExento + iva) * TASA_IGTF;
        }
        double total = baseImponible + baseExento + iva + igtf;

        long numFactura = siguienteCorrelativo++;
        String numControl = String.format("00-%08d", numFactura);

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
        return true;
    }

    private double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
