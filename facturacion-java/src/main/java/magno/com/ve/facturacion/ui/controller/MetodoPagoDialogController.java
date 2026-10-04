package magno.com.ve.facturacion.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import magno.com.ve.facturacion.domain.enums.TipoPago;
import magno.com.ve.facturacion.domain.model.Cliente;
import magno.com.ve.facturacion.domain.model.Pago;

public class MetodoPagoDialogController {

    @FXML private Label lblTitulo;
    @FXML private Label lblMontoUsd;
    @FXML private Label lblMontoBs;
    @FXML private ComboBox<TipoPago> cmbMetodo;
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private VBox panelCamposDinamicos;
    @FXML private Button btnConfirmar;

    private TipoPago tipo;
    private double montoUsd, montoBs, tasa;
    private boolean confirmado = false;
    private Pago resultado;

    // Campos dinámicos
    private TextField txtReferencia;
    private ComboBox<String> cmbBanco;
    private ComboBox<String> cmbRed;
    private TextField txtMontoRecibido;
    private Label lblVuelto;

    public void inicializar(TipoPago tipo, double montoUsd, double montoBs, double tasa) {
        this.montoUsd = montoUsd;
        this.montoBs = montoBs;
        this.tasa = tasa;
        this.tipo = tipo;

        lblMontoUsd.setText(String.format("$ %,.2f", montoUsd));
        lblMontoBs.setText(String.format("Bs. %,.2f  (Tasa: %.2f)", montoBs, tasa));

        cmbMetodo.getItems().setAll(TipoPago.values());
        cmbMetodo.setValue(tipo != null ? tipo : TipoPago.EFECTIVO);

        cmbMetodo.valueProperty().addListener((o, a, b) -> {
            this.tipo = b;
            reconstruirCampos();
        });

        reconstruirCampos();
    }

    private void reconstruirCampos() {
        if (tipo == null) return;
        lblTitulo.setText("Pago con " + tipo.getTitulo());
        panelCamposDinamicos.getChildren().clear();
        txtReferencia = null; cmbBanco = null; cmbRed = null;
        txtMontoRecibido = null; lblVuelto = null;

        switch (tipo) {
            case EFECTIVO -> camposEfectivo();
            case TARJETA_DEBITO, TARJETA_CREDITO -> camposTarjeta();
            case TRANSFERENCIA, PAGO_MOVIL, QR -> camposTransferencia();
            case ZELLE, CRIPTO -> camposReferenciaSimple();
        }
    }

    private void camposEfectivo() {
        txtMontoRecibido = new TextField();
        txtMontoRecibido.setPromptText("Monto recibido en Bs.");
        lblVuelto = new Label("Vuelto: Bs. 0,00");
        lblVuelto.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");

        txtMontoRecibido.textProperty().addListener((o, a, b) -> {
            try {
                double r = Double.parseDouble(b.replace(",", "."));
                double vuelto = r - montoBs;
                if (vuelto >= 0) {
                    lblVuelto.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");
                    lblVuelto.setText(String.format("Vuelto: Bs. %,.2f", vuelto));
                } else {
                    lblVuelto.setStyle("-fx-font-weight: bold; -fx-text-fill: #dc3545;");
                    lblVuelto.setText(String.format("Faltan: Bs. %,.2f", Math.abs(vuelto)));
                }
            } catch (Exception e) {
                lblVuelto.setText("Vuelto: Bs. 0,00");
            }
        });

        panelCamposDinamicos.getChildren().addAll(
                new Label("Monto recibido (Bs.):"), txtMontoRecibido, lblVuelto);
    }

    private void camposTarjeta() {
        cmbRed = new ComboBox<>();
        cmbRed.getItems().addAll("Visa", "Mastercard", "American Express", "Maestro");
        cmbRed.setValue("Visa");
        cmbRed.setMaxWidth(Double.MAX_VALUE);

        txtReferencia = new TextField();
        txtReferencia.setPromptText("Últimos 4 dígitos / Nº aprobación");

        panelCamposDinamicos.getChildren().addAll(
                new Label("Red de tarjeta:"), cmbRed,
                new Label("Referencia:"), txtReferencia);
    }

    private void camposTransferencia() {
        cmbBanco = new ComboBox<>();
        cmbBanco.getItems().addAll(
                "Banesco", "Banco de Venezuela", "Mercantil", "BBVA Provincial",
                "BNC", "Bancamiga", "Banplus", "Banco Plaza", "Otro");
        cmbBanco.setValue("Banesco");
        cmbBanco.setMaxWidth(Double.MAX_VALUE);

        txtReferencia = new TextField();
        txtReferencia.setPromptText("Número de referencia / aprobación");

        panelCamposDinamicos.getChildren().addAll(
                new Label("Banco emisor:"), cmbBanco,
                new Label("Referencia:"), txtReferencia);
    }

    private void camposReferenciaSimple() {
        txtReferencia = new TextField();
        txtReferencia.setPromptText("Referencia / Hash / ID de transacción");
        panelCamposDinamicos.getChildren().addAll(
                new Label("Referencia:"), txtReferencia);
    }

    @FXML
    private void onConfirmar() {
        // Validar efectivo
        if (tipo == TipoPago.EFECTIVO) {
            try {
                double r = Double.parseDouble(txtMontoRecibido.getText().replace(",", "."));
                if (r < montoBs) {
                    new Alert(Alert.AlertType.WARNING,
                            "El monto recibido es menor al total.").showAndWait();
                    return;
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.WARNING, "Ingrese un monto válido.").showAndWait();
                return;
            }
        }

        // Construir cliente
        Cliente cliente = new Cliente();
        if (!txtCedula.getText().isBlank()) {
            cliente.setCedula(txtCedula.getText().trim());
            cliente.setNombres(txtNombres.getText().trim());
            cliente.setApellidos(txtApellidos.getText().trim());
            if (cliente.getContacto() != null) {
                cliente.getContacto().setTelefonoPrincipal(txtTelefono.getText().trim());
                cliente.getContacto().setEmail(txtEmail.getText().trim());
            }
        } else {
            cliente.setCedula("V-00000000");
            cliente.setNombres("Cliente Mostrador");
        }

        // Construir pago
        resultado = new Pago(tipo, cliente);
        resultado.setMontoUsd(montoUsd);
        resultado.setMontoBs(montoBs);
        resultado.setTasaAplicada(tasa);

        if (txtReferencia != null) resultado.setReferencia(txtReferencia.getText().trim());
        if (cmbBanco != null)      resultado.setBanco(cmbBanco.getValue());
        if (cmbRed != null)        resultado.setRedTarjeta(cmbRed.getValue());

        if (txtMontoRecibido != null) {
            try {
                double r = Double.parseDouble(txtMontoRecibido.getText().replace(",", "."));
                resultado.setMontoRecibidoBs(r);
                resultado.setVueltoBs(Math.max(r - montoBs, 0));
            } catch (Exception ignored) {}
        }

        confirmado = true;
        ((Stage) btnConfirmar.getScene().getWindow()).close();
    }

    @FXML
    private void onCancelar() {
        confirmado = false;
        ((Stage) btnConfirmar.getScene().getWindow()).close();
    }

    public boolean isConfirmado() { return confirmado; }
    public Pago getResultado() { return resultado; }
}