package com.ibm.spe.tool.config;

public class CompareSheetMapping {
	private String sheet1;
	private String sheet2;

	public String getSheet1() {
		return sheet1;
	}

	public void setSheet1(String sheet1) {
		this.sheet1 = sheet1;
	}

	public String getSheet2() {
		return sheet2;
	}

	public void setSheet2(String sheet2) {
		this.sheet2 = sheet2;
	}

	@Override
	public String toString() {
		return "CompareSheetMapping [sheet1=" + sheet1 + ", sheet2=" + sheet2
				+ "]";
	}

}
