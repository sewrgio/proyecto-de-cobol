package magno.com.ve.facturacion.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidacionesTest {

    @Test
    public void testCedulaVenezolanaValida() {
        assertTrue(Validaciones.esCedulaValida("V-12345678"));
        assertTrue(Validaciones.esCedulaValida("v12345678"));
        assertTrue(Validaciones.esCedulaValida("J-123456789"));
        assertFalse(Validaciones.esCedulaValida("X-12345678"));
        assertFalse(Validaciones.esCedulaValida("12345678"));
    }

    @Test
    public void testCodigoProducto12Digitos() {
        assertTrue(Validaciones.esCodigoProductoValido("123456789012"));
        assertFalse(Validaciones.esCodigoProductoValido("12345678901"));
        assertFalse(Validaciones.esCodigoProductoValido("ABC123456789"));
    }

    @Test
    public void testCodigoFabricacion() {
        assertTrue(Validaciones.esCodigoFabricacionValido("FAB-2024-A001"));
        assertTrue(Validaciones.esCodigoFabricacionValido("ABC123"));
        assertFalse(Validaciones.esCodigoFabricacionValido("AB"));
    }

    @Test
    public void testNormalizarCedula() {
        assertEquals("V-12345678", Validaciones.normalizarCedula("v12345678"));
        assertEquals("V-12345678", Validaciones.normalizarCedula("V-12345678"));
    }
}