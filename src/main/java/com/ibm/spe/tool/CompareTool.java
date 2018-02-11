package com.ibm.spe.tool;

import static com.ibm.spe.tool.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.spe.tool.config.CompareColumnMapping;
import com.ibm.spe.tool.config.CompareConfigFile;
import com.ibm.spe.tool.config.CompareSheetMapping;
import com.ibm.spe.tool.util.ConfigUtils;

public class CompareTool {
	
	private static Logger logger = LoggerFactory.getLogger(CompareTool.class);

	public static void process(String fromFile, String toFile, String compareType) throws CompareToolException, IOException {
		L.info("Compare type: " + compareType);
		L.info("Compare file [" + fromFile + "] with file [" + toFile + "]");
		CompareFile file1 = null;
		CompareFile file2 = null;
		List<CompareSheetMapping> sheetMappings = null;
		try (ResultFile resultFile = new ResultFile()) {
			if (MM.equals(compareType)) {
				file1 = new MrtFile(fromFile);
				file2 = new MrtFile(toFile);
				sheetMappings = CompareConfigFile.getMMCompareSheets();
			} else if (FM.equals(compareType)) {
				file1 = new FinancialFile(fromFile);
				file2 = new MrtFile(toFile);
				sheetMappings = CompareConfigFile.getFMCompareSheets();
			} else {
				throw new CompareToolException("Unsupport compare type " + compareType);
			}
			for (CompareSheetMapping sheetMapping : sheetMappings) {
				ResultSummary summary = new ResultSummary();
				resultFile.createSheet(sheetMapping.getSheet1());
				// sheet1
				L.info(String.format("Compare sheet [%s] with sheet [%s]", sheetMapping.getSheet1(), sheetMapping.getSheet2()));
				L.info("Loading sheet1: " + sheetMapping.getSheet1());
				CompareSheet sheet1 = file1.getCompareSheet(sheetMapping.getSheet1());
				L.info("Loaded " + sheet1.getTotalRows() + " rows on sheet1 " + sheetMapping.getSheet1());
				summary.setTotalRowCount(sheet1.getTotalRows());
				// sheet2
				L.info("Loading sheet2: " + sheetMapping.getSheet2());
				CompareSheet sheet2 = file2.getCompareSheet(sheetMapping.getSheet2());
				L.info("Loaded " + sheet2.getTotalRows() + " rows on sheet2 " + sheetMapping.getSheet2());
				
				Row titleRow1 = sheet1.getTitleRow();
				Row titleRow2 = sheet2.getTitleRow();
				List<CompareColumnMapping> columnMappings = getColumnMappings(compareType, sheetMapping.getSheet1(), titleRow1, titleRow2);
				logger.info("Compare columns " + columnMappings);
				
				Map<String, Row> rows1 = sheet1.getRows();
				Map<String, Row> rows2 = sheet2.getRows();
				rows1.forEach((key1, row1) -> {
					if (FM.equals(compareType)) {
						key1 = convertKey(key1);
					}
					Row row2 = rows2.get(key1);
					if (row2 == null) {
						resultFile.info(key1, " not found in file " + new File(toFile).getName(), String.valueOf(row1.getRowNum() + 1), "Not Found");
                        if (MM.equals(compareType) && !sheet1.getSeperatedKeys().contains(key1)) {
    						summary.plusNotFoundErrorRow();
                        } else if (FM.equals(compareType)) {
                        	summary.plusNotFoundErrorRow();
                        }
					} else {
						int errorColumnCount = 0;
						for (CompareColumnMapping columnMapping : columnMappings) {
							Cell cell1 = row1.getCell(columnMapping.getColumn1Index());
							Cell cell2 = row2.getCell(columnMapping.getColumn2Index());
							String cellValue1 = cell1 == null ? "" : cell1.getStringCellValue().trim();
							String cellValue2 =  cell2 == null ? "" : cell2.getStringCellValue().trim();
							if (!cellValue1.equals(cellValue2)) {
								 /**
                                 * Special case
                                 * 1. USD VS USD-U for Currency
                                 * 2. "" VS 0.000000
                                 * 3. Feature Long Description
                                 * 4. Unit Cost, SERVICE_MONTHLY_COST, ONE_TIME_CHARGE
                                 */
                                if("Currency".equals(columnMapping.getColumn1()) && cellValue2.equals(cellValue1 + "-U")) {
                                    continue;
                                }
                                if(("0.000000".equals(cellValue1) || "".equals(cellValue1)) && ("0.000000".equals(cellValue2) || "".equals(cellValue2))) {
                                    continue;
                                }
                                if(FM.equals(compareType) && "Feature Long Description".equals(columnMapping.getColumn1()) && cellValue2.contains(cellValue1)) {
                                    continue;
                                }
                                /*
                                 * if column"Feature Code Type" = Monthly Recurring Charge
                                 * 	 Unit Cost vs SERVICE_MONTHLY_COST
                                 * else
                                 *   Unit Cost vs ONE_TIME_CHARGE
                                 */
                                if(FM.equals(compareType) && "Unit Cost".equals(columnMapping.getColumn1())) {
                                    String featureCodeType = row1.getCell(CompareFile.getColumnIndex(titleRow1, "Feature Code Type", sheetMapping.getSheet1())).getStringCellValue().trim();
                                    if("Monthly Recurring Charge".equals(featureCodeType)) {
                                        if(!"SERVICE_MONTHLY_COST".equals(columnMapping.getColumn2())) {
                                            continue;
                                        }
                                    } else if(!"ONE_TIME_CHARGE".equals(columnMapping.getColumn2())) {
                                        continue;
                                    }
                                }
                                errorColumnCount++;
                                resultFile.info(key1, String.format("Column[%s] value[%s] was updated to column[%s] value[%s]", columnMapping.getColumn1(), cellValue1, columnMapping.getColumn2(), cellValue2), String.valueOf(row1.getRowNum() + 1), "Not Match");
							}
						}
						if (errorColumnCount > 0) {
							if (MM.equals(compareType) && !sheet1.getSeperatedKeys().contains(key1)) {
								summary.plusNotMatchErrorRow();
							} else if (FM.equals(compareType)) {
								summary.plusNotMatchErrorRow();
							}
						}
					}
					int maxErrorCount = Integer.valueOf(ConfigUtils.getProperty("max_error_count"));
					if (summary.getTotalErrorRowCount() > maxErrorCount) {
						throw new RuntimeException("More than " + maxErrorCount + " errors, pleae check your files");
					}
//					rows1.put(key1, null);
//					rows2.put(key1, null);
				});
				L.info("Found " + summary.getTotalErrorRowCount() + " errors on sheet " + sheetMapping.getSheet1());
				resultFile.setSheetCompareResult(summary);
			}
		} finally {
			try {
				if (file1 != null) {
					file1.close();
				}
				if (file2 != null) {
					file2.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static List<CompareColumnMapping> getColumnMappings(String compareType, String sheetName, Row titleRow1, Row titleRow2) throws CompareToolException {
		List<CompareColumnMapping> maping = new ArrayList<>();
		Set<String> ignoreColums = getIgnoreColumns(compareType);
		if (MM.equals(compareType)) {
			for (Cell cell1 : titleRow1) {
				int index1 = cell1.getColumnIndex();
				if (index1 != 0) {
					String title1 = cell1.getStringCellValue().trim();
					if (ignoreColums.contains(title1)) {
						logger.info("Do not compare column {}", title1);
						continue;
					}
					CompareColumnMapping indexs = null;
					for (Cell cell2 : titleRow2) {
						int index2 = cell2.getColumnIndex();
						String title2 = cell2.getStringCellValue().trim();
						if (title1.equals(title2)) {
							indexs = new CompareColumnMapping(title1, index1, title2, index2);
							maping.add(indexs);
							break;
						}
					}
					if (indexs == null) {
						throw new CompareToolException("not found column " + title1 + " on sheet2");
					}
				}
			}
			return maping;
		} else if (FM.equals(compareType)) {
			List<CompareColumnMapping> columns = CompareConfigFile.getFMCompareColumns(sheetName);
			if (columns.size() > 0) {
				for (CompareColumnMapping column : columns) {
					if (ignoreColums.contains(column.getColumn1())) {
						logger.info("Do not compare column {}", column.getColumn1());
						continue;
					}
					for (Cell cell : titleRow1) {
						String title = cell.getStringCellValue().trim();
						int index = cell.getColumnIndex();
						if (column.getColumn1().equals(title)) {
							column.setColumn1Index(index);
							break;
						}
					}
					if (column.getColumn1Index() == 0) {
						throw new CompareToolException("not found column " + column.getColumn1() + " on sheet " + sheetName);
					}
					for (Cell cell : titleRow2) {
						String title = cell.getStringCellValue().trim();
						int index = cell.getColumnIndex();
						if (column.getColumn2().equals(title)) {
							column.setColumn2Index(index);
							break;
						}
					}
					if (column.getColumn2Index() == 0) {
						throw new CompareToolException("not found column " + column.getColumn2() + " on sheet " + column.getSheet2());
					}
				}
			} else {
				throw new CompareToolException("not found compare columns for " + sheetName);
			}
			return columns;
		} else {
			throw new CompareToolException("Unsupport compare type " + compareType);
		}
	}

	private static Set<String> getIgnoreColumns(String compareType) throws CompareToolException {
		Set<String> columns = new HashSet<>();
		if (MM.equals(compareType)) {
			for (String column : ConfigUtils.getProperty("mm_ignore_columns", "").split(",")) {
				columns.add(column.trim());
			}
			return columns;
		} else if (FM.equals(compareType)) {
			for (String column : ConfigUtils.getProperty("fm_ignore_columns", "").split(",")) {
				columns.add(column.trim());
			}
			return columns;
		} else {
			throw new CompareToolException("Unsupport compare type " + compareType);
		}
	}
	
	/**
     * USG to US
     * @return
     */
	private static String convertKey(String key) {
    	if(key.length() >= 3) {
    		if("USG".equals(key.substring(0,3))) {
                key = "US" + key.substring(3);
            }
    	}
        return key;
    }
    
	public static void main(String[] args) {
		try {
			CompareTool.process("mrt1.xlsx", "mrt2.xlsx", MM);
//			CompareTool.process("Financial1.xlsx", "mrt2.xlsx", FM);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
