package magno.com.ve.facturacion.service;

import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.util.Validaciones;

public class ProductoService {

    public void validar(Producto producto) throws ValidacionException {
        StringBuilder errores = new StringBuilder();

        // ══════════════════════════════════════════════
        // IDENTIFICACIÓN
        // ══════════════════════════════════════════════

        // Código de barra
        if (!Validaciones.noEstaVacio(producto.getCodigoBarra())) {
            errores.append("• El código de barra es obligatorio.\n");
        }

        // Nombre del producto
        if (!Validaciones.noEstaVacio(producto.getNombreProducto())) {
            errores.append("• El nombre del producto es obligatorio.\n");
        } else if (!Validaciones.tieneLongitudMinima(producto.getNombreProducto(), 2)) {
            errores.append("• El nombre debe tener al menos 2 caracteres.\n");
        } else if (!Validaciones.tieneLongitudMaxima(producto.getNombreProducto(), 100)) {
            errores.append("• El nombre no debe exceder 100 caracteres.\n");
        }

        // Tipo de producto
        if (producto.getTipoProducto() == null) {
            errores.append("• Debe seleccionar el tipo de producto.\n");
        }

        // Unidad de medida
        if (!Validaciones.noEstaVacio(producto.getUnidadMedida())) {
            errores.append("• Debe indicar la unidad de medida (Unidad, Docena, Bulto...).\n");
        }

        // Cantidad
        if (!Validaciones.esCantidadValida(producto.getCantidad())) {
            errores.append("• La cantidad debe ser mayor a 0.\n");
        }

        // ══════════════════════════════════════════════
        // FÁBRICA
        // ══════════════════════════════════════════════

        if (!Validaciones.noEstaVacio(producto.getCompaniaFabricacion())) {
            errores.append("• El nombre de la compañía de fabricación es obligatorio.\n");
        }

        if (!Validaciones.noEstaVacio(producto.getIdentificador())) {
            errores.append("• El RIF / identificación del fabricante es obligatorio.\n");
        }

        if (!Validaciones.noEstaVacio(producto.getPaisOrigen())) {
            errores.append("• El país de origen es obligatorio.\n");
        } else if (!Validaciones.esPaisValido(producto.getPaisOrigen())) {
            errores.append("• País de origen no reconocido. Seleccione uno de la lista.\n");
        }

        // ══════════════════════════════════════════════
        // MEDIDAS (opcionales, pero si están, deben ser válidas)
        // ══════════════════════════════════════════════

        if (producto.getCantidadMedida() != null
                && !Validaciones.esNumeroNoNegativo(producto.getCantidadMedida())) {
            errores.append("• La cantidad de medida no puede ser negativa.\n");
        }

        if (producto.getContenido() != null
                && !Validaciones.esNumeroNoNegativo(producto.getContenido())) {
            errores.append("• El contenido no puede ser negativo.\n");
        }

        if (producto.getCantidadDimension() != null
                && !Validaciones.esNumeroNoNegativo(producto.getCantidadDimension())) {
            errores.append("• La cantidad de dimensión no puede ser negativa.\n");
        }

        // ══════════════════════════════════════════════
        // PRECIO
        // ══════════════════════════════════════════════

        if (!Validaciones.esPrecioValido(producto.getPrecio())) {
            errores.append("• El precio debe ser mayor a 0.\n");
        }

        // ══════════════════════════════════════════════
        // LANZAR SI HAY ERRORES
        // ══════════════════════════════════════════════

        if (errores.length() > 0) {
            throw new ValidacionException(
                "Errores de validación del producto:\n\n" + errores.toString());
        }
    }
}