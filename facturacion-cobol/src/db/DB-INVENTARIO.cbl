       IDENTIFICATION DIVISION.
       PROGRAM-ID. DB-INVENTARIO.

       DATA DIVISION.
       LINKAGE SECTION.
       COPY "FAC-RENGLON.cpy".

       PROCEDURE DIVISION USING FAC-DETALLE.
           DISPLAY "[DB-MOCK] Stock de inventario actualizado.".
           GOBACK.
