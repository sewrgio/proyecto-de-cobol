package magno.com.ve;

import magno.com.ve.model.Producto;
import magno.com.ve.service.FacturacionService;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        FacturacionService servicio = new FacturacionService();
        
        List<Producto> carrito = Arrays.asList(
            new Producto("P001", "Laptop HP", 1200.00, 5),
            new Producto("P002", "Mouse Inalámbrico", 25.50, 10)
        );

        servicio.procesarPedido("Cliente Corporativo S.A.", carrito);
    }
}