package magno.com.ve.facturacion.test;

import magno.com.ve.facturacion.domain.facturacion.DatosFactura;
import magno.com.ve.facturacion.domain.facturacion.MetodoFacturacion;
import magno.com.ve.facturacion.domain.facturacion.ResultadoFactura;
import magno.com.ve.facturacion.integration.facturacion.MetodoFacturacionPDF;
import magno.com.ve.facturacion.integration.facturacion.MetodoFacturacionSimulado;

import java.time.LocalDateTime;

public class TestPDF {
    public static void main(String[] args) throws Exception {
        DatosFactura datos = new DatosFactura();
        datos.setEmisorNombre("INVERSIONES COLD 2024, C.A.");
        datos.setEmisorRif("J-505366220");
        datos.setEmisorDireccion("CC. AA. Libertador, Nivel PB, Local 33, Urb. La Florida");
        datos.setEmisorCiudad("Caracas");
        datos.setEmisorEstado("Distrito Capital");

        datos.setClienteRif("V-99999999");
        datos.setClienteRazonSocial("SERGIO MIRANDA");
        datos.setClienteDireccion("Caracas, Venezuela");

        datos.setNumeroFactura("00001794");
        datos.setNumeroControl("00-00001794");
        datos.setFechaEmision(LocalDateTime.now());
        datos.setCajeroNombre("SONIA GALINDO");
        datos.setCajaNumero("013");

        datos.setBaseImponible(100.00);
        datos.setBaseExento(20.00);
        datos.setIva(16.00);
        datos.setIgtf(4.08);
        datos.setTotal(140.08);

        datos.setFormaPago("DV");
        datos.setMontoPagado(140.08);

        datos.addItem(new DatosFactura.ItemFactura(
            "PROD01", "30 HUEVOS XL", 2, 50.00, "G", 100.00));
        datos.addItem(new DatosFactura.ItemFactura(
            "PROD02", "Hamaca 3", 1, 20.00, "E", 20.00));

        System.out.println("═══════════════════════════════════════");
        System.out.println(" MÉTODO SIMULADO");
        System.out.println("═══════════════════════════════════════");
        MetodoFacturacion sim = new MetodoFacturacionSimulado();
        ResultadoFactura r1 = sim.emitir(datos);
        System.out.println(r1);

        System.out.println("");
        System.out.println("═══════════════════════════════════════");
        System.out.println(" MÉTODO PDF");
        System.out.println("═══════════════════════════════════════");
        MetodoFacturacion pdf = new MetodoFacturacionPDF();
        ResultadoFactura r2 = pdf.emitir(datos);
        System.out.println(r2);

        System.out.println("");
        System.out.println("✅ Test completado");
    }
}
