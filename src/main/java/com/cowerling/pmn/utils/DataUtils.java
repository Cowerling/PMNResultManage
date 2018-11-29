package com.cowerling.pmn.utils;

import com.cowerling.pmn.domain.data.*;
import com.cowerling.pmn.exception.DataParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.*;

public class DataUtils {
    public static final String EXCEL_XLS_EXTENSION  = "xls";
    private static final String EXCEL_XLSX_EXTENSION  = "xlsx";
    private static final int DATA_ROW_OFFSET = 3;


    public static Map.Entry<DataRecordCategory, Map<GeoUtils.GeoDefine, String>> getDataFileCategory(MultipartFile dataFile) throws DataParseException {
        try {
            Workbook workbook = FilenameUtils.getExtension(dataFile.getOriginalFilename()).toLowerCase().endsWith(EXCEL_XLS_EXTENSION) ? new HSSFWorkbook(dataFile.getInputStream()) : new XSSFWorkbook(dataFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            Row categoryRow = sheet.getRow(sheet.getFirstRowNum()), sourceProJRow = sheet.getRow(sheet.getFirstRowNum() + 1), attributeRow = sheet.getRow(sheet.getFirstRowNum() + 2);
            Cell categoryCell = categoryRow.getCell(categoryRow.getFirstCellNum()), sourceProJCell = sourceProJRow.getCell(sourceProJRow.getFirstCellNum());

            String dataRecordCategoryName = String.valueOf(categoryCell.getStringCellValue().trim());
            DataRecordCategory dataRecordCategory = DataRecordCategory.valueOf(dataRecordCategoryName);

            Map<GeoUtils.GeoDefine, String> geoDefines = GeoUtils.getGeoDefines(String.valueOf(sourceProJCell.getStringCellValue().trim()));

            DataContent dataContent = null;

            switch (dataRecordCategory) {
                case CP0:
                    dataContent = new CP0DataContent();
                    break;
                case CPI_2D:
                    dataContent = new CPI2DDataContent();
                    break;
                case CPI_3D:
                    dataContent = new CPI3DDataContent();
                    break;
                case CPII:
                    dataContent = new CPIIDataContent();
                    break;
                case CPIII:
                    dataContent = new CPIIIDataContent();
                    break;
                case CPII_LE:
                    dataContent = new CPIILEDataContent();
                    break;
                case TSIT:
                    dataContent = new TSITDataContent();
                    break;
                case EC:
                    dataContent = new ECDataContent();
                    break;
                default:
                    break;
            }

            List<String> attributeNames = dataContent.attributeNames();

            if (attributeNames.size() != attributeRow.getLastCellNum()) {
                throw new RuntimeException();
            }

            for (int i = 0; i < attributeNames.size(); i++) {
                if (!attributeNames.get(i).toLowerCase().equals(attributeRow.getCell(i).getStringCellValue().toLowerCase())) {
                    throw new RuntimeException();
                }
            }

            return new AbstractMap.SimpleEntry<>(dataRecordCategory, geoDefines);
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
                    case CP0:
                        if (dataContents == null) {
                            dataContents = new ArrayList<CP0DataContent>();
                        }

                        CP0DataContent cp0DataContent = new CP0DataContent();
                        cp0DataContent.setId((long) dataContents.size());
                        cp0DataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        cp0DataContent.setX(row.getCell(1).getNumericCellValue());
                        cp0DataContent.setY(row.getCell(2).getNumericCellValue());
                        cp0DataContent.setZ(row.getCell(3).getNumericCellValue());
                        cp0DataContent.setMx(row.getCell(4).getNumericCellValue());
                        cp0DataContent.setMy(row.getCell(5).getNumericCellValue());
                        cp0DataContent.setMz(row.getCell(6).getNumericCellValue());

                        ((List<CP0DataContent>) dataContents).add(cp0DataContent);
                        break;
                    case CPI_2D:
                        if (dataContents == null) {
                            dataContents = new ArrayList<CPI2DDataContent>();
                        }

                        CPI2DDataContent cpi2dDataContent = new CPI2DDataContent();
                        cpi2dDataContent.setId((long) dataContents.size());
                        cpi2dDataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        cpi2dDataContent.setX(row.getCell(1).getNumericCellValue());
                        cpi2dDataContent.setY(row.getCell(2).getNumericCellValue());
                        cpi2dDataContent.setMx(row.getCell(3).getNumericCellValue());
                        cpi2dDataContent.setMy(row.getCell(4).getNumericCellValue());
                        cpi2dDataContent.setMp(row.getCell(5).getNumericCellValue());

                        ((List<CPI2DDataContent>) dataContents).add(cpi2dDataContent);
                        break;
                    case CPI_3D:
                        if (dataContents == null) {
                            dataContents = new ArrayList<CPI3DDataContent>();
                        }

                        CPI3DDataContent cpi3dDataContent = new CPI3DDataContent();
                        cpi3dDataContent.setId((long) dataContents.size());
                        cpi3dDataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        cpi3dDataContent.setX(row.getCell(1).getNumericCellValue());
                        cpi3dDataContent.setY(row.getCell(2).getNumericCellValue());
                        cpi3dDataContent.setZ(row.getCell(3).getNumericCellValue());
                        cpi3dDataContent.setMx(row.getCell(4).getNumericCellValue());
                        cpi3dDataContent.setMy(row.getCell(5).getNumericCellValue());
                        cpi3dDataContent.setMz(row.getCell(6).getNumericCellValue());

                        ((List<CPI3DDataContent>) dataContents).add(cpi3dDataContent);
                        break;
                    case CPII:
                        if (dataContents == null) {
                            dataContents = new ArrayList<CPIIDataContent>();
                        }

                        CPIIDataContent cpiiDataContent = new CPIIDataContent();
                        cpiiDataContent.setId((long) dataContents.size());
                        cpiiDataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        cpiiDataContent.setX(row.getCell(1).getNumericCellValue());
                        cpiiDataContent.setY(row.getCell(2).getNumericCellValue());
                        cpiiDataContent.setMx(row.getCell(3).getNumericCellValue());
                        cpiiDataContent.setMy(row.getCell(4).getNumericCellValue());
                        cpiiDataContent.setMp(row.getCell(5).getNumericCellValue());

                        ((List<CPIIDataContent>) dataContents).add(cpiiDataContent);
                        break;
                    case CPIII:
                        if (dataContents == null) {
                            dataContents = new ArrayList<CPIIIDataContent>();
                        }

                        CPIIIDataContent cpiiiDataContent = new CPIIIDataContent();
                        cpiiiDataContent.setId((long) dataContents.size());
                        cpiiiDataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        cpiiiDataContent.setX(row.getCell(1).getNumericCellValue());
                        cpiiiDataContent.setY(row.getCell(2).getNumericCellValue());
                        cpiiiDataContent.setZenithHeight(row.getCell(3).getNumericCellValue());
                        cpiiiDataContent.setPrismHeight(row.getCell(4).getNumericCellValue());

                        ((List<CPIIIDataContent>) dataContents).add(cpiiiDataContent);
                        break;
                    case CPII_LE:
                        if (dataContents == null) {
                            dataContents = new ArrayList<CPIILEDataContent>();
                        }

                        CPIILEDataContent cpiileDataContent = new CPIILEDataContent();
                        cpiileDataContent.setId((long) dataContents.size());
                        cpiileDataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        cpiileDataContent.setX(row.getCell(1).getNumericCellValue());
                        cpiileDataContent.setY(row.getCell(2).getNumericCellValue());
                        cpiileDataContent.setMx(row.getCell(3).getNumericCellValue());
                        cpiileDataContent.setMy(row.getCell(4).getNumericCellValue());
                        cpiileDataContent.setMp(row.getCell(5).getNumericCellValue());

                        ((List<CPIILEDataContent>) dataContents).add(cpiileDataContent);
                        break;
                    case TSIT:
                        if (dataContents == null) {
                            dataContents = new ArrayList<TSITDataContent>();
                        }

                        TSITDataContent tsitDataContent = new TSITDataContent();
                        tsitDataContent.setId((long) dataContents.size());
                        tsitDataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        tsitDataContent.setX(row.getCell(1).getNumericCellValue());
                        tsitDataContent.setY(row.getCell(2).getNumericCellValue());
                        tsitDataContent.setMx(row.getCell(3).getNumericCellValue());
                        tsitDataContent.setMy(row.getCell(4).getNumericCellValue());

                        ((List<TSITDataContent>) dataContents).add(tsitDataContent);
                        break;
                    case EC:
                        if (dataContents == null) {
                            dataContents = new ArrayList<ECDataContent>();
                        }

                        ECDataContent ecDataContent = new ECDataContent();
                        ecDataContent.setId((long) dataContents.size());
                        ecDataContent.setName(row.getCell(0).getCellType() == CellType.STRING ? row.getCell(0).getStringCellValue() : Integer.toString((int) row.getCell(0).getNumericCellValue()));
                        ecDataContent.setMeanDeviation(row.getCell(1).getNumericCellValue());
                        ecDataContent.setSquareError(row.getCell(2).getNumericCellValue());

                        ((List<ECDataContent>) dataContents).add(ecDataContent);
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

    public static byte[] getDataFile(DataRecord dataRecord, List<? extends DataContent> dataContents) throws DataParseException {
        try {
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row categoryRow = sheet.createRow(0), sourceProJRow = sheet.createRow(1),  attributeRow = sheet.createRow(2);
            Cell categoryCell = categoryRow.createCell(0), sourceProJCell = sourceProJRow.createCell(0);
            categoryCell.setCellValue(dataRecord.getCategory().name());
            sourceProJCell.setCellValue(dataRecord.getRemark() != null ? dataRecord.getRemark() : "");

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
