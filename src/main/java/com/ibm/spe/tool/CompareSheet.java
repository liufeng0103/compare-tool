package com.ibm.spe.tool;

import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;

public class CompareSheet {
	private Map<String, Row> rows;
	private Row titleRow;
	private String sheetName;
	private int totalRows;
	private Set<String> seperatedKeys;

	public CompareSheet(String sheetName) {
		this.sheetName = sheetName;
	}

	public Map<String, Row> getRows() {
		return rows;
	}

	public void setRows(Map<String, Row> rows) {
		this.rows = rows;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public Row getTitleRow() {
		return titleRow;
	}

	public void setTitleRow(Row titleRow) {
		this.titleRow = titleRow;
	}

	public Set<String> getSeperatedKeys() {
		return seperatedKeys;
	}

	public void setSeperatedKeys(Set<String> seperatedKeys) {
		this.seperatedKeys = seperatedKeys;
	}

}
