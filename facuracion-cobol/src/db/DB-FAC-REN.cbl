       IDENTIFICATION DIVISION.
       PROGRAM-ID. DB-FAC-REN.

       DATA DIVISION.
       LINKAGE SECTION.
       COPY "FAC-RENGLON.cpy".

       PROCEDURE DIVISION USING FAC-DETALLE.
           DISPLAY "[DB-MOCK] Renglones de factura persistidos.".
           GOBACK.
