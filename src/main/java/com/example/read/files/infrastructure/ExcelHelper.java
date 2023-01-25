package com.example.read.files.infrastructure;

import com.example.read.files.model.Tutorial;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String TYPE_VND = "application/vnd.ms-excel";

    static String[] HEADERs = { "Id", "Title", "Description", "Published" };
    static String SHEET = "Tutorials";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (TYPE.equals(file.getContentType())) {
            return true;
        }

        if (TYPE_VND.equals(file.getContentType())) {
            return true;
        }

        return false;
    }

    public static Workbook getWorkbook(InputStream is, String fileName) throws IOException {
        Workbook workbook = null;
        if (fileName.endsWith("xlsx")){
            workbook = new XSSFWorkbook(is);
        } else if (fileName.endsWith("xls")){
            workbook = new HSSFWorkbook(is);
        }
        return workbook;
    }

    public static List<Tutorial> excelToTutorials(Workbook workbook) {
        try {

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<Tutorial> tutorials = new ArrayList<Tutorial>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Tutorial tutorial = new Tutorial();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            tutorial.setId((long) currentCell.getNumericCellValue());
                            break;

                        case 1:
                            tutorial.setTitle(currentCell.getStringCellValue());
                            break;

                        case 2:
                            tutorial.setDescription(currentCell.getStringCellValue());
                            break;

                        case 3:
                            tutorial.setPublished(currentCell.getBooleanCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }

                tutorials.add(tutorial);
            }

            workbook.close();

            return tutorials;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }


}