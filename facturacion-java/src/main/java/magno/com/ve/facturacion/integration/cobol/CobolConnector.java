package magno.com.ve.facturacion.integration.cobol;

import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;
import java.io.IOException;

public interface CobolConnector {

    FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException;

    boolean estaDisponible();
}
