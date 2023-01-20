/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author mluis
 */
@Slf4j
public class LecturaLibro implements Serializable {

    public XSSFWorkbook loadFileXLSX(File file) throws Exception {
        log.info("Cargando archivo " + file.getName());
        XSSFWorkbook workBook = null;
        if (file.exists()) {
            try {
                log.info("Cargando libro de trabajo ...");
                workBook = new XSSFWorkbook(new FileInputStream(file));
                //
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
        return workBook;
    }

    public HSSFWorkbook loadFile(File file) throws Exception {
        log.info("Cargando archivo " + file.getName());
        HSSFWorkbook workBook = null;
        FileInputStream fis = null;
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                //
                log.info("Cargando libro de trabajo ...");
                workBook = new HSSFWorkbook(fis);
                fis.close();
            } catch (IOException e) {
                throw new Exception(e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        log.warn("", e);
                    }
                }
            }
        }
        return workBook;
    }

    public HSSFSheet loadSheet(HSSFWorkbook workBook) throws Exception {
        String sheetName = null;
        log.info("Cargando hoja de trabajo " + sheetName);
        HSSFSheet workSheet = null;
        int sheetNumber;
        try {
            if (sheetName == null) {
                sheetNumber = 0;
            } else {
                sheetNumber = workBook.getSheetIndex(sheetName);
            }

            workSheet = workBook.getSheetAt(sheetNumber);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return workSheet;
    }

    public Cell getCellFromReference(HSSFSheet workSheet, String reference) {
        Cell retVal = null;
        CellReference cellReference = new CellReference(reference);

        if (workSheet != null) {
            Row row = workSheet.getRow(cellReference.getRow());

            if (row != null) {
                retVal = row.getCell((int) cellReference.getCol());
            }
        }

        return retVal;
    }

    public String getValFromReference(HSSFSheet workSheet, String reference) {
        String retVal = null;
        CellReference ref = new CellReference(reference);

        Row row = workSheet.getRow(ref.getRow());

        if (row != null) {
            Cell cell = row.getCell(ref.getCol());

            if (cell != null) {
                switch (cell.getCellType()) {
                    case FORMULA:
                        retVal
                                = workSheet.getRow(ref.getRow()).getCell((int) ref.getCol()).getCellFormula();
                        break;

                    case BOOLEAN:
                        retVal = String.valueOf(cell.getBooleanCellValue());
                        break;

                    case NUMERIC:
                        retVal = String.valueOf(cell.getNumericCellValue());
                        break;

                    case STRING:
                        retVal = cell.getStringCellValue();
                        break;
                    default:
                        retVal = cell.toString();
                        break;
                }
            }
        }

        return retVal;
    }

    public XSSFSheet loadSheet(XSSFWorkbook workBook) throws Exception {
        String sheetName = null;
        log.info("Cargando hoja de trabajo " + sheetName);
        XSSFSheet workSheet = null;
        int sheetNumber;
        try {
            if (sheetName == null) {
                sheetNumber = 0;
            } else {
                sheetNumber = workBook.getSheetIndex(sheetName);
            }

            workSheet = workBook.getSheetAt(sheetNumber);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return workSheet;
    }

    public XSSFSheet loadSheet(XSSFWorkbook workBook, int hoja) throws Exception {

        log.info("Cargando hoja de trabajo " + hoja);
        XSSFSheet workSheet = null;
        try {
            workSheet = workBook.getSheetAt(hoja);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return workSheet;
    }

    public Cell getCellFromReference(XSSFSheet workSheet, String reference) {
        XSSFCell retVal = null;
        CellReference cellReference = new CellReference(reference);
        if (workSheet != null) {
            XSSFRow row = workSheet.getRow(cellReference.getRow());

            if (row != null) {
                retVal = row.getCell(cellReference.getCol());
            }
        }
        return retVal;
    }

    public String getValFromReference(XSSFSheet workSheet, String reference) {
        String retVal = "";
        CellReference ref = new CellReference(reference);

        XSSFRow row = workSheet.getRow(ref.getRow());

        if (row != null) {
            XSSFCell cell = row.getCell(ref.getCol());

            if (cell != null) {
                switch (cell.getCellType()) {
                    case FORMULA:
                        retVal = cell.getStringCellValue().replaceAll("'", "");
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date fecha = cell.getDateCellValue();
                            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
                            retVal = DATE_FORMAT.format(fecha);
                        } else {
                            cell.setCellType(CellType.STRING);
                            retVal = String.valueOf(cell.getStringCellValue());
                        }
                        break;
                    case STRING:
                        retVal = String.valueOf(cell.getStringCellValue());
                        break;
                    default:
                        retVal = String.valueOf(cell.getStringCellValue());
                }
            }
        }
        return retVal;
    }

    public String getValFromReference(XSSFSheet workSheet, int rowV, int columnV) {
        String retVal = "";
        CellReference ref = new CellReference(rowV, columnV);

        XSSFRow row = workSheet.getRow(ref.getRow());

        if (row != null) {
            XSSFCell cell = row.getCell(ref.getCol());

            if (cell != null) {
                switch (cell.getCellType()) {
                    case FORMULA:
                        retVal = cell.getStringCellValue().replaceAll("'", "");
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date fecha = cell.getDateCellValue();
                            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
                            retVal = DATE_FORMAT.format(fecha);
                        } else {
                            cell.setCellType(CellType.STRING);
                            retVal = String.valueOf(cell.getStringCellValue());
                        }
                        break;
                    case STRING:
                        retVal = String.valueOf(cell.getStringCellValue());
                        break;
                    default:
                        retVal = cell.getStringCellValue();
                }
            }
        }
        return retVal;
    }
    
    public Date getValFromReferenceDate(XSSFSheet workSheet, int rowV, int columnV) {
        Date retVal = null;
        CellReference ref = new CellReference(rowV, columnV);
        XSSFRow row = workSheet.getRow(ref.getRow());
        if (row != null) {
            XSSFCell cell = row.getCell(ref.getCol());

            if (cell != null) {
                switch (cell.getCellType()) {                    
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            retVal = cell.getDateCellValue();
                            
                        } else {                           
                            retVal = null;
                        }
                        break;                    
                    default:
                        retVal = null;
                }
            }
        }
        return retVal;
    }

    public void setValueExcel(XSSFSheet my_worksheet, Object valor, int x, int y) throws Exception {
        try {
            XSSFCell cell = my_worksheet.getRow(x).getCell(y);
            if (valor instanceof String) {
                cell.setCellValue(String.valueOf(valor));
            }
            if (valor instanceof Double) {
                Double valorAux = (Double) valor;
                cell.setCellValue(valorAux);
            }
            if (valor instanceof Integer) {
                Integer valorAux = (Integer) valor;
                cell.setCellValue(valorAux);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void setValueExcelFormulas(XSSFSheet my_worksheet, XSSFWorkbook wb, int x, int y) throws Exception {
        XSSFCell cell = my_worksheet.getRow(x).getCell(y);

        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        if (cell.getCellType() == CellType.FORMULA) {
            evaluator.evaluateFormulaCell(cell);
        }
    }

}
