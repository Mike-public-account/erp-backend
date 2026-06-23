package com.erp.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static <T> void export(List<T> dataList, Class<T> clazz, String fileName,
                                  HttpServletResponse response) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Field[] fields = clazz.getDeclaredFields();

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            headerRow.createCell(i).setCellValue(fields[i].getName());
        }

        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            T obj = dataList.get(i);
            for (int j = 0; j < fields.length; j++) {
                try {
                    Object val = fields[j].get(obj);
                    row.createCell(j).setCellValue(val == null ? "" : val.toString());
                } catch (IllegalAccessException e) {
                    row.createCell(j).setCellValue("");
                }
            }
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
        wb.close();
    }

    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz) throws Exception {
        InputStream is = file.getInputStream();
        Workbook wb = WorkbookFactory.create(is);
        Sheet sheet = wb.getSheetAt(0);
        Field[] fields = clazz.getDeclaredFields();
        List<T> list = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (int j = 0; j < fields.length; j++) {
                fields[j].setAccessible(true);
                Cell cell = row.getCell(j);
                String val = cell == null ? "" : cell.toString();
                fields[j].set(obj, convertType(val, fields[j].getType()));
            }
            list.add(obj);
        }
        wb.close();
        return list;
    }

    private static Object convertType(String val, Class<?> type) {
        if (val.isEmpty()) return null;
        if (type == Long.class) return Long.valueOf(val.replace(".0", ""));
        if (type == Integer.class) return Integer.valueOf(val.replace(".0", ""));
        if (type == Double.class) return Double.valueOf(val);
        return val;
    }
}