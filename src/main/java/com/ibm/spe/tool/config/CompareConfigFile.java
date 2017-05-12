package com.ibm.spe.tool.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.spe.tool.L;

public class CompareConfigFile {

	private static Logger logger = LoggerFactory.getLogger(CompareConfigFile.class);
	
	private static final String CONFIG_FILE_NAME = "./config/CompareConfig.xls";
	private static final String COUNTRY_MAPPING = "CountryMapping";
	private static final String FM_SHEET_MAPPING = "FMSheetMapping";
	private static final String MM_SHEET_MAPPING = "MMSheetMapping";
	private static final String COLUMN_MAPPING = "ColumnMapping";
	private static final String SO_ITS_MAPPING = "SO_ITS";
	
	private static Map<String, String> countryMap;
	private static Map<String, String> soItsMap;
	private static List<CompareSheetMapping> mmCompareSheets;
	private static List<CompareSheetMapping> fmCompareSheets;
	private static List<CompareColumnMapping> fmCompareColumns;
	
	static {
		logger.info("Loading compare config file");
		loadConfig();
		logger.info("Loaded compare config file");
	}
	
	public static void loadConfig() {
		try (Workbook wb = WorkbookFactory.create(new File(CONFIG_FILE_NAME))) {
			readMMCompareSheets(wb);
			readFMCompareSheets(wb);
			readFMCompareColumns(wb);
			readSoItsMap(wb);
			readCountryMap(wb);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			L.info("Error when loading configuration file: " + e.getMessage());
		}
	}
	
	public static List<CompareSheetMapping> getMMCompareSheets() {
		return mmCompareSheets;
	}
	
	public static List<CompareSheetMapping> getFMCompareSheets() {
		return fmCompareSheets;
	}
	
	public static List<CompareColumnMapping> getFMCompareColumns(String sheetName) {
		List<CompareColumnMapping> result = new ArrayList<>();
		for (CompareColumnMapping colum : fmCompareColumns) {
			if (colum.getSheet1().equals(sheetName)) {
				result.add(colum);
			}
		}
		return result;
	}

	public static Map<String, String> getSoItsMap() {
		return soItsMap;
	}
	
	public static Map<String, String> getCountryMap() {
		return countryMap;
	}

	private static void readMMCompareSheets(Workbook wb) {
		mmCompareSheets = new ArrayList<>();
		Sheet sheet = wb.getSheet(MM_SHEET_MAPPING);
		for(Row row : sheet) {
			CompareSheetMapping mapping = new CompareSheetMapping();
			if ("true".equalsIgnoreCase(row.getCell(2).getStringCellValue())) {
				String sheet1 = row.getCell(0).getStringCellValue();
				String sheet2 = row.getCell(1).getStringCellValue();
				mapping.setSheet1(sheet1);
				mapping.setSheet2(sheet2);
				mmCompareSheets.add(mapping);
			}	                
	    }
	}

	private static void readFMCompareSheets(Workbook wb) {
		fmCompareSheets = new ArrayList<>();
		Sheet sheet = wb.getSheet(FM_SHEET_MAPPING);
		for(Row row : sheet) {
			CompareSheetMapping mapping = new CompareSheetMapping();
			if ("true".equalsIgnoreCase(row.getCell(2).getStringCellValue())) {
				String sheet1 = row.getCell(0).getStringCellValue();
				String sheet2 = row.getCell(1).getStringCellValue();
				mapping.setSheet1(sheet1);
				mapping.setSheet2(sheet2);
				fmCompareSheets.add(mapping);
			}	                
	    }
	}

	private static void readFMCompareColumns(Workbook wb) {
		fmCompareColumns = new ArrayList<>();
		Sheet sheet = wb.getSheet(COLUMN_MAPPING);
		for(Row row : sheet) {
			CompareColumnMapping mapping = new CompareColumnMapping();
			if ("true".equalsIgnoreCase(row.getCell(4).getStringCellValue())) {
				String sheet1 = row.getCell(0).getStringCellValue();
				String column1 = row.getCell(1).getStringCellValue();
				String sheet2 = row.getCell(2).getStringCellValue();
				String column2 = row.getCell(3).getStringCellValue();
				mapping.setSheet1(sheet1);
				mapping.setColumn1(column1);
				mapping.setSheet2(sheet2);
				mapping.setColumn2(column2);
				fmCompareColumns.add(mapping);
			}	                
	    }
	}

	private static void readSoItsMap(Workbook wb) {
		soItsMap = new HashMap<>();
		Sheet sheet = wb.getSheet(SO_ITS_MAPPING);
		for(Row row : sheet) {
			String value1 = row.getCell(0).getStringCellValue();
			String value2 = row.getCell(1).getStringCellValue();
			if (!"".equals(value1) && !"".equals(value2)) {
				soItsMap.put(value1, value2);
			} else {
				logger.info("skip empty SOITS for " + value1 + " " + value2);
			} 
	    }
	}

	private static void readCountryMap(Workbook wb) {
		countryMap = new HashMap<>();
		Sheet sheet = wb.getSheet(COUNTRY_MAPPING);
		for(Row row : sheet) {
			String value1 = row.getCell(1).getStringCellValue();
			String value2 = row.getCell(0).getStringCellValue();
			if (!"".equals(value1) && !"".equals(value2)) {
				countryMap.put(value1, value2);
			} else {
				logger.info("skip empty country for " + value1 + " " + value2);
			} 
	    }
		logger.info("Country size " + countryMap.size());
	}

}
