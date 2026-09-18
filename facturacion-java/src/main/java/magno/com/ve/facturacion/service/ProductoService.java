package magno.com.ve.facturacion.service;

import magno.com.ve.facturacion.domain.model.Producto;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.util.Validaciones;

public class ProductoService {

    public void validar(Producto producto) throws ValidacionException {
        StringBuilder errores = new StringBuilder();

        // ===== Código =====
        if (!Validaciones.noEstaVacio(producto.getCodigo())) {
            errores.append("• El código es obligatorio.\n");
        } else if (!Validaciones.esCodigoProductoValido(producto.getCodigo())) {
            errores.append("• El código debe tener exactamente 12 dígitos numéricos.\n");
        }

        // ===== Código de fabricación =====
        if (!Validaciones.noEstaVacio(producto.getCodigoFabricacion())) {
            errores.append("• El código de fabricación es obligatorio.\n");
        } else if (!Validaciones.esCodigoFabricacionValido(producto.getCodigoFabricacion())) {
            errores.append("• El código de fabricación debe tener 4-20 caracteres alfanuméricos.\n");
        }

        // ===== Nombre =====
        if (!Validaciones.noEstaVacio(producto.getNombre())) {
            errores.append("• El nombre del producto es obligatorio.\n");
        } else if (!Validaciones.tieneLongitudMinima(producto.getNombre(), 2)) {
            errores.append("• El nombre debe tener al menos 2 caracteres.\n");
        } else if (!Validaciones.tieneLongitudMaxima(producto.getNombre(), 100)) {
            errores.append("• El nombre no debe exceder 100 caracteres.\n");
        }

        // ===== Compañía de fabricación =====
        if (!Validaciones.noEstaVacio(producto.getCompaniaFabricacion())) {
            errores.append("• El nombre de la compañía de fabricación es obligatorio.\n");
        } else if (!Validaciones.esCompaniaValida(producto.getCompaniaFabricacion())) {
            errores.append("• El nombre de la compañía contiene caracteres inválidos.\n");
        }

        // ===== País de origen =====
        if (!Validaciones.noEstaVacio(producto.getPaisOrigen())) {
            errores.append("• El país de origen es obligatorio.\n");
        } else if (!Validaciones.esPaisValido(producto.getPaisOrigen())) {
            errores.append("• País de origen no reconocido. Seleccione uno de la lista.\n");
        }

        // ===== Precio =====
        if (!Validaciones.esPrecioValido(producto.getPrecio())) {
            errores.append("• El precio debe ser mayor a 0.\n");
        }

        // ===== Cantidad =====
        if (!Validaciones.esCantidadValida(producto.getCantidad())) {
            errores.append("• La cantidad debe ser mayor a 0.\n");
        }

        // ===== Medidas (opcionales) =====
        if (producto.getPeso() != null && !Validaciones.esNumeroNoNegativo(producto.getPeso())) {
            errores.append("• El peso no puede ser negativo.\n");
        }
        if (producto.getAltura() != null && !Validaciones.esNumeroNoNegativo(producto.getAltura())) {
            errores.append("• La altura no puede ser negativa.\n");
        }
        if (producto.getAnchura() != null && !Validaciones.esNumeroNoNegativo(producto.getAnchura())) {
            errores.append("• La anchura no puede ser negativa.\n");
        }
        if (producto.getGrosor() != null && !Validaciones.esNumeroNoNegativo(producto.getGrosor())) {
            errores.append("• El grosor no puede ser negativo.\n");
        }

        if (errores.length() > 0) {
            throw new ValidacionException("Errores de validación del producto:\n\n" + errores.toString());
        }
    }
}