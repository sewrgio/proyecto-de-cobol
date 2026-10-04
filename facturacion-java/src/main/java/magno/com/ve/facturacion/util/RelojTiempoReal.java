package magno.com.ve.facturacion.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RelojTiempoReal {

    private static final DateTimeFormatter FMT_HORA =
            DateTimeFormatter.ofPattern("hh:mm a", new Locale("es", "VE"));
    private static final DateTimeFormatter FMT_FECHA =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "VE"));

    private final Timeline timeline;

    public RelojTiempoReal(Label lblHora, Label lblFecha) {
        actualizar(lblHora, lblFecha);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> actualizar(lblHora, lblFecha)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void actualizar(Label lblHora, Label lblFecha) {
        LocalDateTime ahora = LocalDateTime.now();
        lblHora.setText(FMT_HORA.format(ahora));
        lblFecha.setText(FMT_FECHA.format(ahora));
    }

    public void detener() {
        if (timeline != null) timeline.stop();
    }
}