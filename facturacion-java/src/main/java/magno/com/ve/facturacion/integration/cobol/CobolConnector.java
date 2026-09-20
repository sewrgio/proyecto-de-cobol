package magno.com.ve.facturacion.integration.cobol;

import java.io.IOException;

import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;

/**
 * Contrato de comunicación con el Core COBOL.
 *
 * Existen dos implementaciones:
 *   1. CobolConnectorMock   → Para desarrollo sin COBOL (devuelve respuestas simuladas)
 *   2. CobolConnectorSocket → Para producción (envía JSON por TCP al servidor COBOL)
 *
 * El contrato es simple:
 *   - Java envía un FacturaRequestDTO serializado en JSON.
 *   - El servidor COBOL responde con un FacturaResponseDTO serializado en JSON.
 */
public interface CobolConnector {

    /**
     * Procesa una factura en el Core COBOL.
     *
     * @param request Datos de la factura a procesar
     * @return Respuesta del Core COBOL
     * @throws IOException Si hay un problema de red o comunicación
     * @throws CobolException Si el COBOL devuelve un error de negocio
     */
    FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException;

    /**
     * Verifica si el Core COBOL está disponible.
     */
    boolean estaDisponible();
}