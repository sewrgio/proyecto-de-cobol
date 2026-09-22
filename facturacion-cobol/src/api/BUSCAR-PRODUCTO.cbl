       IDENTIFICATION DIVISION.
       PROGRAM-ID. BUSCAR-PRODUCTO.
       
       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT ARCHIVO-INVENTARIO ASSIGN TO "datos/inventario.dat"
               ORGANIZATION IS LINE SEQUENTIAL
               FILE STATUS IS WS-STATUS-INV.
           
           SELECT ARCHIVO-ENTRADA ASSIGN TO "buscar-input.json"
               ORGANIZATION IS LINE SEQUENTIAL
               FILE STATUS IS WS-STATUS-IN.
           
           SELECT ARCHIVO-SALIDA  ASSIGN TO "buscar-output.json"
               ORGANIZATION IS LINE SEQUENTIAL
               FILE STATUS IS WS-STATUS-OUT.
       
       DATA DIVISION.
       FILE SECTION.
       FD  ARCHIVO-INVENTARIO.
       01  REG-INVENTARIO        PIC X(100).
       
       FD  ARCHIVO-ENTRADA.
       01  REG-ENTRADA           PIC X(300).
       
       FD  ARCHIVO-SALIDA.
       01  REG-SALIDA            PIC X(300).
       
       WORKING-STORAGE SECTION.
       *> Estructura de un producto
       01  PROD-REG.
           05 PROD-CODIGO        PIC X(12).
           05 PROD-NOMBRE        PIC X(40).
           05 PROD-PRECIO        PIC 9(07)V99.
           05 PROD-STOCK         PIC 9(06).
           05 PROD-ALICUOTA      PIC X(01).
           05 PROD-CATEGORIA     PIC X(15).
       
       *> Variables de control
       01  WS-STATUS-INV         PIC X(02).
       01  WS-STATUS-IN          PIC X(02).
       01  WS-STATUS-OUT         PIC X(02).
       01  WS-FIN-INVENTARIO     PIC X VALUE "N".
           88 FIN-INVENTARIO     VALUE "S".
       01  WS-FIN-ARCHIVO        PIC X VALUE "N".
           88 FIN-ARCHIVO        VALUE "S".
       01  WS-TEXTO-BUSCAR       PIC X(40).
       01  WS-CONTADOR           PIC 9(03) VALUE 0.
       01  WS-MAX-RESULTADOS     PIC 9(03) VALUE 20.
       
       *> Variables para extraer el valor del JSON
       01  WS-LINEA-TRIM         PIC X(300).
       01  WS-POS-INI            PIC 9(03).
       01  WS-POS-FIN            PIC 9(03).
       01  WS-POS-TMP            PIC 9(03).
       01  WS-LARGO              PIC 9(03).
       01  WS-VALOR              PIC X(100).
       
       *> Variables para formatear salida
       01  WS-PRECIO-TXT         PIC Z(07)9.99.
       01  WS-STOCK-TXT          PIC Z(06)9.
       
       PROCEDURE DIVISION.
       0000-MAIN.
           PERFORM 1000-INICIALIZAR
           PERFORM 2000-LEER-BUSQUEDA
           PERFORM 3000-BUSCAR-PRODUCTOS
           PERFORM 4000-ESCRIBIR-SALIDA
           STOP RUN.
       
       1000-INICIALIZAR.
           MOVE "N" TO WS-FIN-INVENTARIO WS-FIN-ARCHIVO
           MOVE 0 TO WS-CONTADOR
           MOVE SPACES TO WS-TEXTO-BUSCAR.
       
       *> Lee el JSON de entrada para obtener el texto a buscar
       2000-LEER-BUSQUEDA.
           OPEN INPUT ARCHIVO-ENTRADA
           PERFORM UNTIL FIN-ARCHIVO
               READ ARCHIVO-ENTRADA
                   AT END MOVE "S" TO WS-FIN-ARCHIVO
                   NOT AT END
                       MOVE REG-ENTRADA TO WS-LINEA-TRIM
                       IF WS-LINEA-TRIM(1:100) NOT = SPACES
                           PERFORM 2100-PARSEAR-TEXTO
                       END-IF
               END-READ
           END-PERFORM
           CLOSE ARCHIVO-ENTRADA.
       
       2100-PARSEAR-TEXTO.
           MOVE 0 TO WS-POS-TMP
           INSPECT WS-LINEA-TRIM TALLYING WS-POS-TMP
               FOR CHARACTERS BEFORE INITIAL '"textoBuscar"'
           IF WS-POS-TMP < 290
               ADD 13 TO WS-POS-TMP
               PERFORM 2200-EXTRAER-VALOR
               MOVE WS-VALOR(1:40) TO WS-TEXTO-BUSCAR
           END-IF.
       
       2200-EXTRAER-VALOR.
           MOVE SPACES TO WS-VALOR
           MOVE 0 TO WS-POS-FIN
           INSPECT WS-LINEA-TRIM(WS-POS-TMP:200) TALLYING WS-POS-FIN
               FOR CHARACTERS BEFORE INITIAL '"'
           IF WS-POS-FIN < 200
               ADD WS-POS-FIN TO WS-POS-TMP
               ADD 1 TO WS-POS-TMP
               MOVE 0 TO WS-POS-FIN
               INSPECT WS-LINEA-TRIM(WS-POS-TMP:100) TALLYING WS-POS-FIN
                   FOR CHARACTERS BEFORE INITIAL '"'
               IF WS-POS-FIN > 0
                   MOVE WS-LINEA-TRIM(WS-POS-TMP:WS-POS-FIN) TO WS-VALOR
               END-IF
           END-IF.
       
       *> Busca en el inventario
       3000-BUSCAR-PRODUCTOS.
           OPEN INPUT ARCHIVO-INVENTARIO
           PERFORM UNTIL FIN-INVENTARIO
               READ ARCHIVO-INVENTARIO
                   AT END MOVE "S" TO WS-FIN-INVENTARIO
                   NOT AT END
                       PERFORM 3100-PROCESAR-PRODUCTO
               END-READ
           END-PERFORM
           CLOSE ARCHIVO-INVENTARIO.
       
       3100-PROCESAR-PRODUCTO.
           *> Extraer los campos del registro
           MOVE REG-INVENTARIO(1:12) TO PROD-CODIGO
           MOVE REG-INVENTARIO(13:40) TO PROD-NOMBRE
           MOVE FUNCTION NUMVAL(REG-INVENTARIO(53:10)) TO PROD-PRECIO
           MOVE FUNCTION NUMVAL(REG-INVENTARIO(63:6)) TO PROD-STOCK
           MOVE REG-INVENTARIO(69:1) TO PROD-ALICUOTA
           MOVE REG-INVENTARIO(70:15) TO PROD-CATEGORIA
           
           *> Verificar si coincide con el texto de búsqueda
           IF WS-CONTADOR < WS-MAX-RESULTADOS
               IF PROD-CODIGO(1:12) CONTAINS-CODIGO 
                  OR PROD-NOMBRE CONTAINS-NOMBRE
                   ADD 1 TO WS-CONTADOR
                   *> Se encontró, guardar para la salida
               END-IF
           END-IF.
       
       *> Escribe el JSON de salida con los resultados
       4000-ESCRIBIR-SALIDA.
           OPEN OUTPUT ARCHIVO-SALIDA
           
           MOVE "{" TO REG-SALIDA
           WRITE REG-SALIDA
           
           STRING '  "total": ' DELIMITED BY SIZE
                  WS-CONTADOR DELIMITED BY SIZE
                  ',' DELIMITED BY SIZE
                  INTO REG-SALIDA
           END-STRING
           WRITE REG-SALIDA
           
           MOVE '  "productos": [' TO REG-SALIDA
           WRITE REG-SALIDA
           
           *> Aquí iría el bucle para escribir cada producto
           *> (simplificado por ahora)
           MOVE '  ]' TO REG-SALIDA
           WRITE REG-SALIDA
           
           MOVE "}" TO REG-SALIDA
           WRITE REG-SALIDA
           
           CLOSE ARCHIVO-SALIDA.