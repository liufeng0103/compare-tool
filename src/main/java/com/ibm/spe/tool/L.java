package com.ibm.spe.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.spe.tool.gui.CompareToolGui;

public class L {
	
	private static Logger logger = LoggerFactory.getLogger(L.class);

	public static void info(String msg) {
		logger.info(msg);
		CompareToolGui.contentTxtArea.append(msg + "\n");
	}

}
