package com.ibm.spe.tool.config;

public class CompareColumnMapping {
	private String sheet1;
	private String column1;
	private int column1Index;
	private String sheet2;
	private String column2;
	private int column2Index;

	public CompareColumnMapping() {
	}

	public CompareColumnMapping(String column1, int column1Index,
			String column2, int column2Index) {
		super();
		this.column1 = column1;
		this.column1Index = column1Index;
		this.column2 = column2;
		this.column2Index = column2Index;
	}

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

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}

	public int getColumn1Index() {
		return column1Index;
	}

	public void setColumn1Index(int column1Index) {
		this.column1Index = column1Index;
	}

	public int getColumn2Index() {
		return column2Index;
	}

	public void setColumn2Index(int column2Index) {
		this.column2Index = column2Index;
	}

	@Override
	public String toString() {
		return "CompareColumnMapping [column1=" + column1 + ", column1Index="
				+ column1Index + ", column2=" + column2 + ", column2Index="
				+ column2Index + "]";
	}

}
