package com.ibm.spe.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.spe.tool.config.CompareConfigFile;
import com.ibm.spe.tool.util.ConfigUtils;

public class MrtFile extends CompareFile {
	
	private static Logger logger = LoggerFactory.getLogger(MrtFile.class);
	
	// The sheet key column format like AR~6943-02K~6943-02K~9646~2015~SFC
	private static List<String> specialSheetName = new ArrayList<>();
	static {
        specialSheetName.add("STD_FACTORS");
        specialSheetName.add("STD_RATE");
        specialSheetName.add("LIST_PRICE_N");
        specialSheetName.add("MKT_PRICE");
    }
	
	private String path;
	private boolean needConvertSOITS = "yes".equalsIgnoreCase(ConfigUtils.getProperty("convert_soits", "yes"));
	private Map<String, String> soItsMap = CompareConfigFile.getSoItsMap();
	
	public MrtFile(String path) throws CompareToolException {
		super(path);
		this.path = path;
	}

	@Override
	public CompareSheet getCompareSheet(String sheetName) throws CompareToolException {
		Sheet sheet = getSheetByName(sheetName);
		if (sheet != null) {
			CompareSheet compareSheet = new CompareSheet(sheetName);
			Set<String> seperatedKeys = new HashSet<>();
			compareSheet.setSeperatedKeys(seperatedKeys);
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
								String[] keys = generateKey(key, row, sheetName);
								rows.put(keys[0], convertToCompareRow(row));
								if (!keys[0].equals(keys[1])) {
									rows.put(keys[1], convertToCompareRow(row));
									seperatedKeys.add(keys[0]);
								}
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
	
	/**
     * IT~6948-85D~6943-A31~7082~2013~IBM转化成
     * IT694885D7082 和 IT6943A317082
     */
    private String[] generateKey(String key, Row row, String sheetName) throws CompareToolException {
        String[] keys = new String[2];
        // validate the key format
        String[] s = key.split("~");
        if(s.length >= 5) {
            String[] s1 = convertSoIdToItsId(s[1]).split("-");
            keys[0] = s[0] + s1[0] + s1[1] + s[3];
            String[] s2 = convertSoIdToItsId(s[2]).split("-");
            keys[1] = s[0] + s2[0] + s2[1] + s[3];
        } else {
        	throw new CompareToolException("Incorrect key format " + key + " at sheet " + sheetName + " row " + (row.getRowNum() + 1));
        }
        return keys;
    }

    private String convertSoIdToItsId(String soId) {
    	if (needConvertSOITS) {
    		String tmp = soItsMap.get(soId);
    		if (tmp != null) {
    			return tmp;
    		} 
    	}
    	return soId;
    }
    
}
