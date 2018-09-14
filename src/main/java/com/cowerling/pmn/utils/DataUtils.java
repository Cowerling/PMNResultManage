package com.cowerling.pmn.utils;

import com.cowerling.pmn.domain.data.DataRecordCategory;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class DataUtils {
    private static final String EXCEL_XLS_EXTENSION  = "xls";
    private static final String EXCEL_XLSX_EXTENSION  = "xlsx";

    public static DataRecordCategory getDataFileCategory(MultipartFile dataFile) throws IOException {
        Workbook workbook = FilenameUtils.getExtension(dataFile.getOriginalFilename()).toLowerCase().endsWith(EXCEL_XLS_EXTENSION) ? new HSSFWorkbook(dataFile.getInputStream()) : new XSSFWorkbook(dataFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(sheet.getFirstRowNum());
        Cell cell = row.getCell(row.getFirstCellNum());
        String dataRecordCategoryName = String.valueOf(cell.getStringCellValue());
        return DataRecordCategory.valueOf(dataRecordCategoryName);
    }
}
