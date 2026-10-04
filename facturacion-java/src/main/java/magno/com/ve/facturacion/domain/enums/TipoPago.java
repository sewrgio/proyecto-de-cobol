package magno.com.ve.facturacion.domain.enums;

public enum TipoPago {
    EFECTIVO("Efectivo", "Pago en caja"),
    TARJETA_DEBITO("Tarjeta Débito", "Visa · Mastercard · Maestro"),
    TARJETA_CREDITO("Tarjeta Crédito", "Visa · Mastercard · Amex"),
    TRANSFERENCIA("Transferencia", "Bancos nacionales"),
    PAGO_MOVIL("Pago Móvil", "C2P / P2P"),
    QR("QR / Link de pago", "Escanea o envía link"),
    ZELLE("Zelle", "Transferencia USD"),
    CRIPTO("Criptomonedas", "USDT / BTC");

    private final String titulo;
    private final String subtitulo;

    TipoPago(String titulo, String subtitulo) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
    }

    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }

    @Override
    public String toString() { return titulo; }
}