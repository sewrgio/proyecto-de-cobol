package magno.com.ve.facturacion.domain.model;

import magno.com.ve.facturacion.domain.enums.TipoPago;

public class Pago {

    private TipoPago tipo;
    private Cliente cliente;
    private String referencia;
    private String banco;
    private String redTarjeta;
    private double montoUsd;
    private double montoBs;
    private double tasaAplicada;
    private double montoRecibidoBs;
    private double vueltoBs;

    public Pago() {}

    public Pago(TipoPago tipo, Cliente cliente) {
        this.tipo = tipo;
        this.cliente = cliente;
    }

    // getters / setters
    public TipoPago getTipo() { return tipo; }
    public void setTipo(TipoPago tipo) { this.tipo = tipo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getRedTarjeta() { return redTarjeta; }
    public void setRedTarjeta(String redTarjeta) { this.redTarjeta = redTarjeta; }

    public double getMontoUsd() { return montoUsd; }
    public void setMontoUsd(double montoUsd) { this.montoUsd = montoUsd; }

    public double getMontoBs() { return montoBs; }
    public void setMontoBs(double montoBs) { this.montoBs = montoBs; }

    public double getTasaAplicada() { return tasaAplicada; }
    public void setTasaAplicada(double tasaAplicada) { this.tasaAplicada = tasaAplicada; }

    public double getMontoRecibidoBs() { return montoRecibidoBs; }
    public void setMontoRecibidoBs(double montoRecibidoBs) { this.montoRecibidoBs = montoRecibidoBs; }

    public double getVueltoBs() { return vueltoBs; }
    public void setVueltoBs(double vueltoBs) { this.vueltoBs = vueltoBs; }

    /** Devuelve el nombre del método tal como se guardará en la factura */
    public String getFormaPagoFactura() {
        StringBuilder sb = new StringBuilder(tipo.getTitulo());
        if (redTarjeta != null && !redTarjeta.isBlank()) sb.append(" (").append(redTarjeta).append(")");
        if (banco != null && !banco.isBlank()) sb.append(" - ").append(banco);
        if (referencia != null && !referencia.isBlank()) sb.append(" Ref: ").append(referencia);
        return sb.toString();
    }
}
