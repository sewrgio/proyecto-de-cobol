package magno.com.ve.facturacion.api.tcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import magno.com.ve.facturacion.infrastructure.cobol.CobolProcessPool;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaRequestDTO;
import magno.com.ve.facturacion.integration.cobol.dto.FacturaResponseDTO;

public class FacturacionHandler extends ChannelInboundHandlerAdapter {
    private static final ObjectMapper mapper = new ObjectMapper();
    // Instancia estática compartida del pool de COBOL (tamaño de 8 procesos para alta concurrencia)
    private static final CobolProcessPool cobolPool = new CobolProcessPool(8);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String jsonPayload = (String) msg;
        System.out.println("\n[TCP SERVER] Recibida trama TCP: " + jsonPayload);
        
        try {
            if (jsonPayload.contains("\"rifCliente\"")) {
                // Es una factura
                FacturaRequestDTO request = mapper.readValue(jsonPayload, FacturaRequestDTO.class);
                FacturaResponseDTO response = cobolPool.procesarFactura(request);
                String jsonResponse = mapper.writeValueAsString(response);
                ctx.writeAndFlush(jsonResponse + "\n");
                System.out.println("[TCP SERVER] COBOL procesó Factura OK. Respuesta enviada al cliente.");
            } else {
                // Operaciones de Almacén, Admin, Login, etc.
                System.out.println("[TCP SERVER] Procesando comando de módulo: " + jsonPayload);
                ctx.writeAndFlush("{\"estado\":\"OK\", \"mensaje\":\"Procesado en TCP Server exitosamente\"}\n");
            }
        } catch (Exception e) {
            ctx.writeAndFlush("{\"estado\":\"ERROR\", \"mensaje\":\"" + e.getMessage() + "\"}\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
