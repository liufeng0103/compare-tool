package com.ibm.spe.tool;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.monitorjbl.xlsx.StreamingReader;

public abstract class CompareFile implements Closeable {
	
	private InputStream is;
	private Workbook workbook;
	
	public CompareFile(String path) throws CompareToolException {
		try {
			is = new FileInputStream(new File(path));
			workbook = StreamingReader.builder()
			          .rowCacheSize(100)
			          .bufferSize(4096)
			          .open(is);
		} catch (FileNotFoundException e) {
			throw new CompareToolException(path + " File not Found", e);
		}
	}

	@Override
	public void close() throws IOException {
		if (workbook != null) {
			workbook.close();
		}
		if (is != null) {
			is.close();
		}
	}
	
	protected Sheet getSheetByName(String sheetName) {
		return workbook.getSheet(sheetName);
	}
	
	public static int getColumnIndex(Row row, String columnName, String sheetName) {
		for (Cell cell : row) {
			if (cell.getStringCellValue().trim().equalsIgnoreCase(columnName)) {
				return cell.getColumnIndex();
			}
		}
		throw new RuntimeException(
			String.format("Error, Not found column[%s] on sheet[%s]", columnName, sheetName));
	}
	
	public abstract CompareSheet getCompareSheet(String sheetName) throws CompareToolException;
	
}
