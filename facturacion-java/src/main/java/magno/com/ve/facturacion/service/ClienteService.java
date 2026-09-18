package magno.com.ve.facturacion.service;

import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.exception.ValidacionException;
import magno.com.ve.facturacion.util.Validaciones;

public class ClienteService {

    public void validar(Cliente cliente) throws ValidacionException {
        StringBuilder errores = new StringBuilder();

        if (!Validaciones.noEstaVacio(cliente.getCedula())) {
            errores.append("• La cédula/RIF es obligatoria.\n");
        } else if (!Validaciones.esCedulaValida(cliente.getCedula())) {
            errores.append("• Cédula/RIF inválida. Use V, E, J, G o P.\n");
        }

        if (!Validaciones.noEstaVacio(cliente.getNombres())) {
            errores.append("• El nombre/razón social es obligatorio.\n");
        }

        boolean esPersonaNatural = cliente.getTipo() != null &&
            (cliente.getTipo().name().equals("VENEZOLANO") ||
             cliente.getTipo().name().equals("EXTRANJERO") ||
             cliente.getTipo().name().equals("PASAPORTE"));

        if (esPersonaNatural) {
            if (!Validaciones.noEstaVacio(cliente.getApellidos())) {
                errores.append("• El apellido es obligatorio.\n");
            }
        }

        if (cliente.getDireccion() == null ||
            !Validaciones.noEstaVacio(cliente.getDireccion().getDireccionCompleta())) {
            errores.append("• La dirección es obligatoria.\n");
        }

        if (cliente.getContacto() != null) {
            if (!Validaciones.esTelefonoValido(cliente.getContacto().getTelefonoPrincipal())) {
                errores.append("• Teléfono principal inválido.\n");
            }
            if (!Validaciones.esEmailValido(cliente.getContacto().getEmail())) {
                errores.append("• Email inválido.\n");
            }
        }

        if (errores.length() > 0) {
            throw new ValidacionException("Errores de validación:\n\n" + errores.toString());
        }
    }

    public void normalizar(Cliente cliente) {
        if (cliente.getCedula() != null) {
            cliente.setCedula(Validaciones.normalizarCedula(cliente.getCedula()));
        }
    }

    public void validarYNormalizar(Cliente cliente) throws ValidacionException {
        normalizar(cliente);
        validar(cliente);
    }
}
