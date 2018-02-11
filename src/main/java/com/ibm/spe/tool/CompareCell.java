package com.ibm.spe.tool;

public class CompareCell {
	private int columnIndex;
	private String stringCellValue;

	public CompareCell(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public void setStringCellValue(String stringCellValue) {
		this.stringCellValue = stringCellValue;
	}

	public String getStringCellValue() {
		return stringCellValue;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

}
