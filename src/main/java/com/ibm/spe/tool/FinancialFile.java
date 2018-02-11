package com.ibm.spe.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.spe.tool.config.CompareConfigFile;


public class FinancialFile extends CompareFile {
	
	private static Logger logger = LoggerFactory.getLogger(FinancialFile.class);
	
	private static List<String> specialSheetName = new ArrayList<String>();
	static {
        specialSheetName.add("FINANCIAL_FACTORS");
        specialSheetName.add("UNIT_COST");
        specialSheetName.add("LIST_PRICE_AND_DELEGATION");
        specialSheetName.add("MARKET_REF_PRICE");
    }
	
	private String path;
	
	public FinancialFile(String path) throws CompareToolException {
		super(path);
		this.path = path;
	}

	@Override
	public CompareSheet getCompareSheet(String sheetName) throws CompareToolException {
		Sheet sheet = getSheetByName(sheetName);
		if (sheet != null) {
			CompareSheet compareSheet = new CompareSheet(sheetName);
			int totalRowNum = 0;
			boolean isSpecialSheet = false;
			if (specialSheetName.contains(sheetName)) {
				isSpecialSheet = true;
				logger.info("Special sheet {}", sheetName);
			}
			Map<String, CompareRow> rows = new HashMap<>();
			for (Row row : sheet) {
				if (row.getRowNum() == 0) { // first row
					compareSheet.setTitleRow(row);
				} else {
					Cell cell = row.getCell(0);
					if (cell != null && !"".equals(cell.getStringCellValue().trim())) {
						String key = cell.getStringCellValue().trim();
						if (key != null && !"".equals(key)) {
							totalRowNum++;
							if (isSpecialSheet) {
								rows.put(generateKey(sheetName, compareSheet.getTitleRow(), row), convertToCompareRow(row));
							} else {
								rows.put(key, convertToCompareRow(row));
							}
						} else {
							logger.warn("Ignore empty key at index A row {}", row.getRowNum() + 1);
							continue;
						}
					} else {
						logger.warn("Ignore empty cell at index A row {}", row.getRowNum() + 1);
						continue;
					}
				}
			}
			compareSheet.setRows(rows);
			compareSheet.setTotalRows(totalRowNum);
			return compareSheet;
		} else {
			throw new CompareToolException("not found sheet " + sheetName + " in " + path); 
		}
	}

	private String generateKey(String sheetName, Row titleRow, Row row) throws CompareToolException {
		int countryColumnNumber = getColumnIndex(titleRow, "Country", sheetName);
        int offeringColumnNumber = getColumnIndex(titleRow, "Offering", sheetName);
        int idColumnNumber = getColumnIndex(titleRow, "ID", sheetName);
        int featureCodeColumnNumber = getColumnIndex(titleRow, "Feature Code", sheetName);

        String country = row.getCell(countryColumnNumber).getStringCellValue();
        String countryCode = CompareConfigFile.getCountryMap().get(country);
        if(countryCode == null) {
            throw new CompareToolException(String.format("Not found country code for country[%s] ", country));
        }
        return countryCode + row.getCell(offeringColumnNumber).getStringCellValue() + row.getCell(idColumnNumber).getStringCellValue() + row.getCell(featureCodeColumnNumber).getStringCellValue();
    }

}
