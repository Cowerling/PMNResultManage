package com.cowerling.pmn.utils;

import com.cowerling.pmn.domain.data.CPIBaseDataContent;
import com.cowerling.pmn.domain.data.DataContent;
import com.cowerling.pmn.domain.data.DataRecord;
import com.cowerling.pmn.domain.data.DataRecordCategory;
import com.cowerling.pmn.exception.DataParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataUtils {
    public static final String EXCEL_XLS_EXTENSION  = "xls";
    private static final String EXCEL_XLSX_EXTENSION  = "xlsx";
    private static final int DATA_ROW_OFFSET = 2;


    public static DataRecordCategory getDataFileCategory(MultipartFile dataFile) throws DataParseException {
        try {
            Workbook workbook = FilenameUtils.getExtension(dataFile.getOriginalFilename()).toLowerCase().endsWith(EXCEL_XLS_EXTENSION) ? new HSSFWorkbook(dataFile.getInputStream()) : new XSSFWorkbook(dataFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Row categoryRow = sheet.getRow(sheet.getFirstRowNum()), attributeRow = sheet.getRow(sheet.getFirstRowNum() + 1);
            Cell categoryCell = categoryRow.getCell(categoryRow.getFirstCellNum());
            String dataRecordCategoryName = String.valueOf(categoryCell.getStringCellValue().trim());
            DataRecordCategory dataRecordCategory = DataRecordCategory.valueOf(dataRecordCategoryName);

            DataContent dataContent = null;

            switch (dataRecordCategory) {
                case CPI_BASE:
                    dataContent = new CPIBaseDataContent();
                    break;
                default:
                    break;
            }

            if (dataContent == null || dataContent.attributeNames().size() != attributeRow.getLastCellNum()) {
                throw new RuntimeException();
            }

            for (int i = 0; i < dataContent.attributeNames().size(); i++) {
                if (!dataContent.attributeNames().get(i).toLowerCase().equals(attributeRow.getCell(i).getStringCellValue().toLowerCase())) {
                    throw new RuntimeException();
                }
            }

            return dataRecordCategory;
        } catch (Exception e) {
            throw new DataParseException();
        }
    }

    public static List<? extends DataContent> getDataFileContents(DataRecord dataRecord, String dataFileLocation) throws DataParseException {
        try {
            Workbook workbook = FilenameUtils.getExtension(dataRecord.getFile()).toLowerCase().equals(EXCEL_XLS_EXTENSION) ? new HSSFWorkbook(new FileInputStream(dataFileLocation + dataRecord.getFile())) : new XSSFWorkbook(new FileInputStream(dataFileLocation + dataRecord.getFile()));
            Sheet sheet = workbook.getSheetAt(0);

            List<? extends DataContent> dataContents = null;

            for (int i = sheet.getFirstRowNum() + DATA_ROW_OFFSET; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                switch (dataRecord.getCategory()) {
                    case CPI_BASE:
                        if (dataContents == null) {
                            dataContents = new ArrayList<CPIBaseDataContent>();
                        }

                        CPIBaseDataContent cpiBaseDataContent = new CPIBaseDataContent();
                        cpiBaseDataContent.setId((long) dataContents.size());
                        cpiBaseDataContent.setX(row.getCell(0).getNumericCellValue());
                        cpiBaseDataContent.setY(row.getCell(1).getNumericCellValue());
                        cpiBaseDataContent.setH(row.getCell(2).getNumericCellValue());

                        ((List<CPIBaseDataContent>) dataContents).add(cpiBaseDataContent);
                        break;
                    default:
                        break;
                }
            }

            if (dataContents == null) {
                throw new RuntimeException();
            }

            return dataContents;
        } catch (Exception e) {
            throw new DataParseException();
        }
    }

    public static byte[] getDataFile(DataRecordCategory dataRecordCategory, List<? extends DataContent> dataContents) throws DataParseException {
        try {
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row categoryRow = sheet.createRow(0), attributeRow = sheet.createRow(1);
            Cell categoryCell = categoryRow.createCell(0);
            categoryCell.setCellValue(dataRecordCategory.name());

            for (int i = 0; i < dataContents.size(); i++) {
                DataContent dataContent = dataContents.get(i);
                Row row = sheet.createRow(i + DATA_ROW_OFFSET);

                if (i == 0) {
                    for (int j = 0; j < dataContent.attributeNames().size(); j++) {
                        Cell attributeCell = attributeRow.createCell(j);
                        attributeCell.setCellValue(dataContent.attributeNames().get(j));
                    }
                }

                for (int j = 0; j < dataContent.values().size(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = dataContent.values().get(j);
                    if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
                        cell.setCellValue((Double) dataContent.values().get(j));
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) dataContent.values().get(j));
                    } else if (value instanceof Date) {
                        cell.setCellValue((Date) dataContent.values().get(j));
                    } else {
                        cell.setCellValue(dataContent.values().get(j).toString());
                    }
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e) {
            throw new DataParseException();
        }
    }
}
