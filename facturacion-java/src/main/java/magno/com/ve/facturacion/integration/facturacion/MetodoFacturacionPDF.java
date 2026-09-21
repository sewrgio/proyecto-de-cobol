package magno.com.ve.facturacion.integration.facturacion;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import magno.com.ve.facturacion.domain.facturacion.DatosFactura;
import magno.com.ve.facturacion.domain.facturacion.MetodoFacturacion;
import magno.com.ve.facturacion.domain.facturacion.ResultadoFactura;
import magno.com.ve.facturacion.exception.FacturacionException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class MetodoFacturacionPDF implements MetodoFacturacion {

    private static final String CARPETA_FACTURAS = "facturas";

    @Override
    public ResultadoFactura emitir(DatosFactura datos) throws FacturacionException {
        try {
            Path carpeta = Paths.get(CARPETA_FACTURAS);
            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }

            String nombreArchivo = "factura_" + datos.getNumeroFactura() + ".pdf";
            Path archivoPdf = carpeta.resolve(nombreArchivo);

            generarPdf(datos, archivoPdf);

            System.out.println("[PDF] Factura generada: " + archivoPdf.toAbsolutePath());
            return ResultadoFactura.exito(
                datos.getNumeroFactura(),
                datos.getNumeroControl(),
                archivoPdf
            );
        } catch (FacturacionException e) {
            throw e;
        } catch (Exception e) {
            throw new FacturacionException("ERR_PDF",
                "Error al generar el PDF: " + e.getMessage());
        }
    }

    private void generarPdf(DatosFactura datos, Path archivo) throws Exception {
        PdfWriter writer = new PdfWriter(archivo.toString());
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph(datos.getEmisorNombre() != null
                ? datos.getEmisorNombre() : "MAGNO C.A.")
            .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));

        if (datos.getEmisorRif() != null) {
            doc.add(new Paragraph("RIF: " + datos.getEmisorRif())
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10));
        }
        if (datos.getEmisorDireccion() != null) {
            doc.add(new Paragraph(datos.getEmisorDireccion())
                .setTextAlignment(TextAlignment.CENTER).setFontSize(9));
        }

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("FACTURA")
            .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("Cliente: " + datos.getClienteRazonSocial()).setFontSize(10));
        doc.add(new Paragraph("RIF: " + datos.getClienteRif()).setFontSize(10));

        doc.add(new Paragraph(" "));

        String fecha = datos.getFechaEmision() != null
            ? datos.getFechaEmision().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "-";
        String hora = datos.getFechaEmision() != null
            ? datos.getFechaEmision().format(DateTimeFormatter.ofPattern("HH:mm")) : "-";

        doc.add(new Paragraph("Factura Nº: " + datos.getNumeroFactura()).setFontSize(10));
        doc.add(new Paragraph("Nº Control: " + datos.getNumeroControl()).setFontSize(10));
        doc.add(new Paragraph("Fecha: " + fecha + "  Hora: " + hora).setFontSize(10));

        doc.add(new Paragraph(" "));

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 5, 1, 2, 2}))
            .useAllAvailableWidth();

        tabla.addHeaderCell(new Cell().add(new Paragraph("Código").setBold().setFontSize(9)));
        tabla.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold().setFontSize(9)));
        tabla.addHeaderCell(new Cell().add(new Paragraph("Cant").setBold().setFontSize(9)));
        tabla.addHeaderCell(new Cell().add(new Paragraph("P.Unit").setBold().setFontSize(9)));
        tabla.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontSize(9)));

        for (DatosFactura.ItemFactura item : datos.getItems()) {
            tabla.addCell(new Cell().add(new Paragraph(
                item.getCodigo() != null ? item.getCodigo() : "").setFontSize(9)));
            tabla.addCell(new Cell().add(new Paragraph(
                item.getNombre() != null ? item.getNombre() : "").setFontSize(9)));
            tabla.addCell(new Cell().add(new Paragraph(
                String.valueOf(item.getCantidad())).setFontSize(9)));
            tabla.addCell(new Cell().add(new Paragraph(
                String.format("%.2f", item.getPrecioUnitario())).setFontSize(9)));
            tabla.addCell(new Cell().add(new Paragraph(
                String.format("%.2f", item.getTotalLinea())).setFontSize(9)));
        }

        doc.add(tabla);
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph(String.format("SUBTOTAL:       %.2f",
            datos.getBaseImponible() + datos.getBaseExento()))
            .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
        doc.add(new Paragraph(String.format("BASE IMPONIBLE: %.2f", datos.getBaseImponible()))
            .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
        doc.add(new Paragraph(String.format("EXENTO:         %.2f", datos.getBaseExento()))
            .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
        doc.add(new Paragraph(String.format("IVA (16%%):      %.2f", datos.getIva()))
            .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
        if (datos.getIgtf() > 0) {
            doc.add(new Paragraph(String.format("IGTF (3%%):       %.2f", datos.getIgtf()))
                .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
        }
        doc.add(new Paragraph(String.format("TOTAL:          %.2f", datos.getTotal()))
            .setTextAlignment(TextAlignment.RIGHT).setBold().setFontSize(12));

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("¡Gracias por su compra!")
            .setTextAlignment(TextAlignment.CENTER).setFontSize(10).setItalic());

        doc.close();
    }

    @Override
    public String getNombre() { return "PDF (formato SENIAT)"; }

    @Override
    public boolean estaDisponible() { return true; }
}
