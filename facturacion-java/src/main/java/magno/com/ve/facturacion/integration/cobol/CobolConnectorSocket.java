package magno.com.ve.facturacion.integration.cobol;

import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;
import java.io.IOException;

public class CobolConnectorSocket implements CobolConnector {

    private final String host;
    private final int puerto;

    public CobolConnectorSocket(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    @Override
    public FacturaResponseDTO procesarFactura(FacturaRequestDTO request)
            throws IOException, CobolException {
        throw new UnsupportedOperationException("Conector socket no implementado aún");
    }

    @Override
    public boolean estaDisponible() {
        return false;
    }
}
