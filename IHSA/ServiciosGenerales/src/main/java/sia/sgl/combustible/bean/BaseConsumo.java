/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.combustible.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import sia.modelo.combustible.vo.Consumo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
public abstract class BaseConsumo {

    private String fileName;
    private String sheetName;
    protected int sheetNumber;
    private HSSFWorkbook workBook;
    private FileInputStream fis;
    private HSSFSheet workSheet;

    private List<Consumo> data;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public HSSFWorkbook getWorkBook() {
        return workBook;
    }

    public HSSFSheet getWorkSheet() {
        return workSheet;
    }

    public void setWorkSheet(HSSFSheet worksheet) {
        this.workSheet = worksheet;
    }

    public List<Consumo> getData() {
        return data;
    }

    public void setData(List<Consumo> data) {
        this.data = data;
    }

    protected String getValFromReference(String reference) {
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

    protected Cell getCellFromReference(String reference) {
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

    protected void loadFile() throws Exception {
        UtilLog4j.log.info("Cargando archivo " + getFileName());

        Path filePath = Paths.get(getFileName());

        if (Files.exists(filePath)) {
            try {
                fis = new FileInputStream(new File(filePath.toString()));
                // Finds the workbook instance for XLSX file
                UtilLog4j.log.info("Cargando libro de trabajo ...");
                workBook = new HSSFWorkbook(fis);

                fis.close();
            } catch (IOException e) {
                throw new Exception(e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        UtilLog4j.log.warn("", e);
                    }
                }
            }

        }
    }

    protected void loadSheet() throws Exception {
        UtilLog4j.log.info("Cargando hoja de trabajo " + getSheetName());

        try {
            if (getSheetName() == null) {
                sheetNumber = 0;
            } else {
                sheetNumber = workBook.getSheetIndex(getSheetName());
            }

            workSheet = workBook.getSheetAt(sheetNumber);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public abstract void doIt() throws Exception;

}
