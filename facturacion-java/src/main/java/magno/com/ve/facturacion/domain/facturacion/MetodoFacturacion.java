package magno.com.ve.facturacion.domain.facturacion;

import magno.com.ve.facturacion.exception.FacturacionException;

public interface MetodoFacturacion {

    ResultadoFactura emitir(DatosFactura datosFactura) throws FacturacionException;

    String getNombre();

    boolean estaDisponible();
}
