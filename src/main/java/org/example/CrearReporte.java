package org.example;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CrearReporte {

    public void crearExcel(List<Listado> listadoList) {

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Reporte");

        crearCabecera(sheet);

        crearDetalles(sheet, listadoList);

        try {
            File archivo = new File("reporte.xls");
            FileOutputStream out = new FileOutputStream(archivo);
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            System.err.println("ERROR AL CREAR EL ARCHIVO!");
            e.printStackTrace();
        }

    }

    private void crearCabecera(HSSFSheet sheet){

        List<String> titulos = Arrays.asList("Identificación", "Nombre", "Correo 1", "Correo 2");

        AtomicInteger columnaTitulo = new AtomicInteger(0);
        Row encabezado = sheet.createRow(0);

        titulos.forEach(titulo -> {
            Cell celda = encabezado.createCell(columnaTitulo.getAndIncrement());
            celda.setCellValue(titulo);
        });

    }

    private void crearDetalles(HSSFSheet sheet, List<Listado> listadoList){

        listadoList.forEach(listado -> {

            Row fila = sheet.createRow(listadoList.indexOf(listado));

            Cell celda = fila.createCell(0);
            celda.setCellValue(listado.getIdentificacion());

            celda = fila.createCell(1);
            celda.setCellValue(listado.getNombre());

            celda = fila.createCell(2);
            celda.setCellValue(listado.getCorreo1());

            celda = fila.createCell(3);
            celda.setCellValue(listado.getCorreo2());

        });

    }

}
