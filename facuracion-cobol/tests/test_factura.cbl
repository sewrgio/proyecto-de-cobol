       IDENTIFICATION DIVISION.
       PROGRAM-ID. TEST-FACTURA.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       COPY "FAC-ENCABEZADO.cpy".
       COPY "FAC-RENGLON.cpy".
       COPY "FISCAL-SENIAT.cpy".
       01 WS-RESPUESTA PIC 9(02) VALUE ZERO.

       PROCEDURE DIVISION.
       0000-MAIN.
           DISPLAY "==============================================".
           DISPLAY "INICIANDO PRUEBA DE FACTURACION COBOL".
           DISPLAY "==============================================".

           MOVE "CLI001"                 TO FAC-COD-CLIENTE.
           MOVE "DV"                     TO FAC-FORMA-PAGO.
           MOVE "J-123456789"            TO FIS-RIF-CLIENTE.
           MOVE "EMPRESA DE PRUEBA C.A." TO FIS-RAZON-SOCIAL.

           MOVE "PROD01"                 TO REN-COD-PROD(1).
           MOVE 2                        TO REN-CANTIDAD(1).
           MOVE 50.00                    TO REN-PRECIO-UNIT(1).
           MOVE "G"                      TO REN-ALICUOTA(1).

           MOVE "PROD02"                 TO REN-COD-PROD(2).
           MOVE 1                        TO REN-CANTIDAD(2).
           MOVE 20.00                    TO REN-PRECIO-UNIT(2).
           MOVE "E"                      TO REN-ALICUOTA(2).

           CALL "PROC-FACTURA" USING FACTURA-CABECERA FAC-DETALLE DATOS-FISCALES-SENIAT WS-RESPUESTA.

           IF WS-RESPUESTA = 0
              DISPLAY "----------------------------------------------"
              DISPLAY "RESULTADOS DE PRUEBA OK:"
              DISPLAY "Nro Control     : " FIS-NUM-CONTROL
              DISPLAY "Base Imponible  : " FIS-BASE-IMP-GEN
              DISPLAY "Monto Exento    : " FIS-BASE-EXENTO
              DISPLAY "Monto IVA (16%) : " FIS-MONTO-IVA-GEN
              DISPLAY "Monto IGTF (3%) : " FIS-MONTO-IGTF
              DISPLAY "Total Factura   : " FAC-TOTAL-NETO
              DISPLAY "----------------------------------------------"
           ELSE
              DISPLAY "ERROR EN PROCESAMIENTO. CODIGO: " WS-RESPUESTA
           END-IF.

           STOP RUN.
