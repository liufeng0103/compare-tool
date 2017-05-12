package com.ibm.spe.tool.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SwingUtils {
	
	public static void updateUILookAndFeel(Component c) {
		try {
			String plafName = UIManager.getInstalledLookAndFeels()[1].getClassName();
			UIManager.setLookAndFeel(plafName);
			SwingUtilities.updateComponentTreeUI(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showOnScreenCenter(Component c) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		c.setLocation(screenSize.width / 2 - c.getWidth() / 2, screenSize.height / 2 - c.getHeight() / 2);
	}
}
