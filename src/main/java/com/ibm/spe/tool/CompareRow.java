package com.ibm.spe.tool;

import java.util.HashMap;
import java.util.Map;

public class CompareRow {
    private int rowNum;
    private Map<Integer, CompareCell> cells = new HashMap<>();

    public CompareCell createCell(int index) {
        CompareCell cell = new CompareCell(index);
        cells.put(index, cell);
        return cell;
    }

    public CompareCell getCell(int index) {
        return cells.get(index);
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }
}
