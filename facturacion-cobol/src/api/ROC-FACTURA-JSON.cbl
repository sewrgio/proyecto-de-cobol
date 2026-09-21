              IDENTIFICATION DIVISION.
       PROGRAM-ID. PROC-FACTURA-JSON.
       
       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT ARCHIVO-ENTRADA ASSIGN TO "factura-input.json"
               ORGANIZATION IS LINE SEQUENTIAL
               FILE STATUS IS WS-STATUS-IN.
           
           SELECT ARCHIVO-SALIDA  ASSIGN TO "factura-output.json"
               ORGANIZATION IS LINE SEQUENTIAL
               FILE STATUS IS WS-STATUS-OUT.
       
       DATA DIVISION.
       FILE SECTION.
       FD  ARCHIVO-ENTRADA.
       01  REG-ENTRADA           PIC X(300).
       
       FD  ARCHIVO-SALIDA.
       01  REG-SALIDA            PIC X(300).
       
       WORKING-STORAGE SECTION.
       COPY "FAC-ENCABEZADO.cpy".
       COPY "FAC-RENGLON.cpy".
       COPY "FISCAL-SENIAT.cpy".
       COPY "EMPRESA.cpy".
       COPY "CAJERO.cpy".
       COPY "PAGO.cpy".
       
       01  WS-STATUS-IN          PIC X(02).
       01  WS-STATUS-OUT         PIC X(02).
       01  WS-FIN-ARCHIVO        PIC X VALUE "N".
           88 FIN-ARCHIVO        VALUE "S".
       01  WS-CONTADOR-ITEMS     PIC 9(03) VALUE 0.
       01  WS-RESPUESTA          PIC 9(02) VALUE 0.
       01  WS-LINEA-TRIM         PIC X(300).
       01  WS-VALOR              PIC X(100).
       01  WS-VALOR-NUM          PIC 9(09)V99.
       01  WS-VALOR-ENT          PIC 9(05).
       01  WS-POS-INI            PIC 9(03).
       01  WS-MONTO-TXT          PIC Z(07)9.99.
       01  WS-ENTERO-TXT         PIC Z(08)9.
       01  WS-ITEM-ACTUAL        PIC 9(03) VALUE 0.
       
       PROCEDURE DIVISION.
       0000-MAIN.
           PERFORM 1000-INICIALIZAR
           PERFORM 2000-LEER-ENTRADA
           PERFORM 3000-PROCESAR-FACTURA
           PERFORM 4000-ESCRIBIR-SALIDA
           STOP RUN.
       
       1000-INICIALIZAR.
           INITIALIZE FACTURA-CABECERA
           INITIALIZE FAC-DETALLE
           INITIALIZE DATOS-FISCALES-SENIAT
           INITIALIZE EMPRESA-SUCURSAL
           INITIALIZE CAJERO-INFO
           INITIALIZE DATOS-PAGO
           MOVE 0 TO WS-CONTADOR-ITEMS WS-ITEM-ACTUAL
           MOVE "N" TO WS-FIN-ARCHIVO.
       
       2000-LEER-ENTRADA.
           OPEN INPUT ARCHIVO-ENTRADA
           PERFORM UNTIL FIN-ARCHIVO
               READ ARCHIVO-ENTRADA
                   AT END MOVE "S" TO WS-FIN-ARCHIVO
                   NOT AT END
                       PERFORM 2100-PARSEAR-LINEA
               END-READ
           END-PERFORM
           CLOSE ARCHIVO-ENTRADA.
       
       2100-PARSEAR-LINEA.
           MOVE REG-ENTRADA TO WS-LINEA-TRIM
           
           IF WS-LINEA-TRIM CONTAINS '"nombreSucursal"'
               PERFORM 2110-EXTRAER-NOMBRE-SUCURSAL
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"rifSucursal"'
               PERFORM 2120-EXTRAER-RIF-SUCURSAL
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"direccionSucursal"'
               PERFORM 2130-EXTRAER-DIR-SUCURSAL
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"ciudadSucursal"'
               PERFORM 2140-EXTRAER-CIUDAD
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"estadoSucursal"'
               PERFORM 2150-EXTRAER-ESTADO
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"zonaPostal"'
               PERFORM 2160-EXTRAER-ZONA
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"cajeroCodigo"'
               PERFORM 2170-EXTRAER-CAJ-COD
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"cajeroNombre"'
               PERFORM 2180-EXTRAER-CAJ-NOM
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"cajaNumero"'
               PERFORM 2190-EXTRAER-CAJA
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"rifCliente"'
               PERFORM 2200-EXTRAER-RIF-CLI
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"razonSocial"'
               PERFORM 2210-EXTRAER-RAZON
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"formaPago"'
               PERFORM 2220-EXTRAER-FORMA-PAGO
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"fechaEmision"'
               PERFORM 2230-EXTRAER-FECHA
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"horaEmision"'
               PERFORM 2240-EXTRAER-HORA
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"montoPagado"'
               PERFORM 2250-EXTRAER-MONTO-PAGADO
           END-IF
           
           *> Items (múltiples líneas con "codigo")
           IF WS-LINEA-TRIM CONTAINS '"codigoItem"'
               ADD 1 TO WS-CONTADOR-ITEMS
               MOVE WS-CONTADOR-ITEMS TO WS-ITEM-ACTUAL
               PERFORM 2300-EXTRAER-COD-ITEM
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"nombreItem"'
               PERFORM 2310-EXTRAER-NOM-ITEM
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"cantidad"'
               PERFORM 2320-EXTRAER-CANT
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"precio"'
               PERFORM 2330-EXTRAER-PRECIO
           END-IF
           IF WS-LINEA-TRIM CONTAINS '"alicuota"'
               PERFORM 2340-EXTRAER-ALICUOTA
           END-IF.
       
       2110-EXTRAER-NOMBRE-SUCURSAL.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"nombreSucursal"'
           ADD 19 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:100) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:60) TO EMP-NOMBRE.
       
       2120-EXTRAER-RIF-SUCURSAL.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"rifSucursal"'
           ADD 16 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:50) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:12) TO EMP-RIF.
       
       2130-EXTRAER-DIR-SUCURSAL.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"direccionSucursal"'
           ADD 22 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:100) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:80) TO EMP-DIRECCION.
       
       2140-EXTRAER-CIUDAD.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"ciudadSucursal"'
           ADD 19 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:60) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:40) TO EMP-CIUDAD.
       
       2150-EXTRAER-ESTADO.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"estadoSucursal"'
           ADD 19 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:60) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:40) TO EMP-ESTADO.
       
       2160-EXTRAER-ZONA.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"zonaPostal"'
           ADD 15 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:30) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:10) TO EMP-ZONA-POSTAL.
       
       2170-EXTRAER-CAJ-COD.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"cajeroCodigo"'
           ADD 17 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:30) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:10) TO CAJ-CODIGO.
       
       2180-EXTRAER-CAJ-NOM.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"cajeroNombre"'
           ADD 17 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:60) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:40) TO CAJ-NOMBRE.
       
       2190-EXTRAER-CAJA.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"cajaNumero"'
           ADD 15 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:5) TO CAJ-CAJA.
       
       2200-EXTRAER-RIF-CLI.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"rifCliente"'
           ADD 15 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:50) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:12) TO FIS-RIF-CLIENTE.
       
       2210-EXTRAER-RAZON.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"razonSocial"'
           ADD 16 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:80) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:40) TO FIS-RAZON-SOCIAL.
       
       2220-EXTRAER-FORMA-PAGO.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"formaPago"'
           ADD 14 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE WS-VALOR(1:2) TO FAC-FORMA-PAGO.
       
       2230-EXTRAER-FECHA.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"fechaEmision"'
           ADD 17 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE FUNCTION NUMVAL(WS-VALOR) TO FAC-FECHA-EMISION.
       
       2240-EXTRAER-HORA.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"horaEmision"'
           ADD 16 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           MOVE FUNCTION NUMVAL(WS-VALOR) TO FAC-HORA-EMISION.
       
       2250-EXTRAER-MONTO-PAGADO.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"montoPagado"'
           ADD 15 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           COMPUTE PAG-MONTO-PAGADO = FUNCTION NUMVAL(WS-VALOR).
       
       2300-EXTRAER-COD-ITEM.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"codigoItem"'
           ADD 14 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:30) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           IF WS-ITEM-ACTUAL <= 10
               MOVE WS-VALOR(1:10) TO REN-COD-PROD(WS-ITEM-ACTUAL)
           END-IF.
       
       2310-EXTRAER-NOM-ITEM.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"nombreItem"'
           ADD 14 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:80) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           IF WS-ITEM-ACTUAL <= 10
               MOVE WS-VALOR(1:40) TO REN-NOMBRE-PROD(WS-ITEM-ACTUAL)
           END-IF.
       
       2320-EXTRAER-CANT.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"cantidad"'
           ADD 13 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           IF WS-ITEM-ACTUAL >= 1 AND WS-ITEM-ACTUAL <= 10
               COMPUTE WS-VALOR-ENT = FUNCTION NUMVAL(WS-VALOR)
               MOVE WS-VALOR-ENT TO REN-CANTIDAD(WS-ITEM-ACTUAL)
           END-IF.
       
       2330-EXTRAER-PRECIO.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"precio"'
           ADD 11 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           IF WS-ITEM-ACTUAL >= 1 AND WS-ITEM-ACTUAL <= 10
               COMPUTE WS-VALOR-NUM = FUNCTION NUMVAL(WS-VALOR)
               MOVE WS-VALOR-NUM TO REN-PRECIO-UNIT(WS-ITEM-ACTUAL)
           END-IF.
       
       2340-EXTRAER-ALICUOTA.
           MOVE 0 TO WS-POS-INI
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-INI
               FOR CHARACTERS BEFORE INITIAL '"alicuota"'
           ADD 13 TO WS-POS-INI
           MOVE WS-LINEA-TRIM(WS-POS-INI:20) TO WS-VALOR
           PERFORM 2900-LIMPIAR
           IF WS-ITEM-ACTUAL >= 1 AND WS-ITEM-ACTUAL <= 10
               MOVE WS-VALOR(1:1) TO REN-ALICUOTA(WS-ITEM-ACTUAL)
           END-IF.
       
       2900-LIMPIAR.
           INSPECT WS-VALOR REPLACING ALL '"' BY SPACE
           INSPECT WS-VALOR REPLACING ALL ',' BY SPACE
           INSPECT WS-VALOR REPLACING ALL '}' BY SPACE
           MOVE FUNCTION TRIM(WS-VALOR) TO WS-VALOR.
       
       3000-PROCESAR-FACTURA.
           MOVE WS-CONTADOR-ITEMS TO FAC-CANT-ITEMS
           CALL "PROC-FACTURA" USING FACTURA-CABECERA
                                    FAC-DETALLE
                                    DATOS-FISCALES-SENIAT
                                    WS-RESPUESTA.
       
       4000-ESCRIBIR-SALIDA.
           OPEN OUTPUT ARCHIVO-SALIDA
           MOVE "{" TO REG-SALIDA
           WRITE REG-SALIDA
           
           IF WS-RESPUESTA = 0
               MOVE '  "estado": "OK",' TO REG-SALIDA
               WRITE REG-SALIDA
               MOVE '  "codigoError": 0,' TO REG-SALIDA
               WRITE REG-SALIDA
               
               STRING '  "numeroControl": "' DELIMITED BY SIZE
                      FIS-NUM-CONTROL DELIMITED BY SIZE
                      '",' DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
               
               MOVE FAC-NUMERO TO WS-ENTERO-TXT
               STRING '  "numeroFactura": ' DELIMITED BY SIZE
                      WS-ENTERO-TXT DELIMITED BY SIZE
                      ',' DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
               
               MOVE FIS-BASE-IMP-GEN TO WS-MONTO-TXT
               STRING '  "baseImponible": ' DELIMITED BY SIZE
                      WS-MONTO-TXT DELIMITED BY SIZE
                      ',' DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
               
               MOVE FIS-BASE-EXENTO TO WS-MONTO-TXT
               STRING '  "baseExento": ' DELIMITED BY SIZE
                      WS-MONTO-TXT DELIMITED BY SIZE
                      ',' DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
               
               MOVE FIS-MONTO-IVA-GEN TO WS-MONTO-TXT
               STRING '  "iva": ' DELIMITED BY SIZE
                      WS-MONTO-TXT DELIMITED BY SIZE
                      ',' DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
               
               MOVE FIS-MONTO-IGTF TO WS-MONTO-TXT
               STRING '  "igtf": ' DELIMITED BY SIZE
                      WS-MONTO-TXT DELIMITED BY SIZE
                      ',' DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
               
               MOVE FAC-TOTAL-NETO TO WS-MONTO-TXT
               STRING '  "total": ' DELIMITED BY SIZE
                      WS-MONTO-TXT DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
           ELSE
               MOVE '  "estado": "ERROR",' TO REG-SALIDA
               WRITE REG-SALIDA
               MOVE WS-RESPUESTA TO WS-ENTERO-TXT
               STRING '  "codigoError": ' DELIMITED BY SIZE
                      WS-ENTERO-TXT DELIMITED BY SIZE
                      INTO REG-SALIDA
               END-STRING
               WRITE REG-SALIDA
           END-IF
           
           MOVE "}" TO REG-SALIDA
           WRITE REG-SALIDA
           CLOSE ARCHIVO-SALIDA.