package magno.com.ve.facturacion.service;

import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.integration.cobol.CobolConnector;
import magno.com.ve.facturacion.integration.cobol.CobolConnectorMock;
import magno.com.ve.facturacion.integration.cobol.CobolException;
import magno.com.ve.facturacion.integration.cobol.dto.ClienteDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;
import magno.com.ve.facturacion.integration.cobol.dto.ItemDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FacturacionService {

    private final CobolConnector cobol;

    public FacturacionService() {
        // En desarrollo usamos el Mock. En producción: new CobolConnectorSocket("192.168.1.100", 5000)
        this.cobol = new CobolConnectorMock();
    }

    public FacturacionService(CobolConnector cobol) {
        this.cobol = cobol;
    }

    public String procesarPedido(Cliente cliente, List<Producto> items) {
        StringBuilder log = new StringBuilder();
        log.append("--- Iniciando Facturación ---\n");

        try {
            FacturaRequestDTO request = construirRequest(cliente, items, "CO");
            log.append("[CONNECTOR] Enviando solicitud al Core COBOL...\n");

            FacturaResponseDTO response = cobol.procesarFactura(request);

            log.append("\n=========================================\n");
            log.append("           FACTURA GENERADA              \n");
            log.append("=========================================\n");
            log.append("Cliente:        ").append(cliente.getNombreCompleto()).append("\n");
            log.append("Cédula:         ").append(cliente.getCedula()).append("\n");
            log.append("Nro Control:    ").append(response.getNumeroControl()).append("\n");
            log.append("Nro Factura:    ").append(response.getNumeroFactura()).append("\n");
            log.append("-----------------------------------------\n");
            for (Producto p : items) {
                log.append(String.format("%-20s x%d  $%.2f\n",
                    p.getNombre(), p.getCantidad(), p.getSubtotal()));
            }
            log.append("-----------------------------------------\n");
            log.append(String.format("Base Imponible: $%.2f\n", response.getBaseImponible()));
            log.append(String.format("Base Exento:    $%.2f\n", response.getBaseExento()));
            log.append(String.format("IVA 16%%:        $%.2f\n", response.getIva()));
            log.append(String.format("IGTF 3%%:        $%.2f\n", response.getIgtf()));
            log.append(String.format("TOTAL:          $%.2f\n", response.getTotal()));
            log.append("=========================================\n");

        } catch (CobolException ex) {
            log.append("ERROR del Core COBOL [").append(ex.getCodigoError()).append("]: ")
               .append(ex.getMessage()).append("\n");
        } catch (IOException ex) {
            log.append("ERROR de comunicación: ").append(ex.getMessage()).append("\n");
        }

        return log.toString();
    }

    private FacturaRequestDTO construirRequest(Cliente cliente, List<Producto> items, String formaPago) {
        FacturaRequestDTO request = new FacturaRequestDTO();

        ClienteDTO clienteDTO = new ClienteDTO(
            cliente.getCedula(),
            cliente.getCedula(),
            cliente.getNombreCompleto()
        );
        request.setCliente(clienteDTO);
        request.setFormaPago(formaPago);
        request.setFechaEmision(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        for (Producto p : items) {
            ItemDTO item = new ItemDTO(
                p.getCodigo(),
                p.getCantidad(),
                p.getPrecio(),
                "G"
            );
            request.addItem(item);
        }

        return request;
    }
}