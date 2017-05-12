package com.ibm.spe.tool.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ibm.spe.tool.CompareTool;
import com.ibm.spe.tool.Constants;
import com.ibm.spe.tool.util.SwingUtils;

public class CompareToolGui extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 542;
    public static JTextArea contentTxtArea = new JTextArea(20, 62);
    
    static {
    	contentTxtArea.setEditable(false);
    }

	public CompareToolGui() {
		setTitle("SPE Compare Tool");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		final JComboBox<String> compareTypeBox = new JComboBox<>(Constants.compareTypes);
        final JTextField file1Txt = new JTextField(15);
        final JTextField file2Txt = new JTextField(15);
        final JButton startBtn = new JButton("Start");

        JPanel componentPanel = new JPanel();
        componentPanel.add(compareTypeBox);
        componentPanel.add(new JLabel("From File:"));
        componentPanel.add(file1Txt);
        componentPanel.add(new JLabel("To File:  "));
        componentPanel.add(file2Txt);
        componentPanel.add(startBtn);
        
        JPanel componentLayoutPanel = new JPanel();
        componentLayoutPanel.setLayout(new BorderLayout());
        componentLayoutPanel.add(componentPanel, BorderLayout.WEST);
        
        setLayout(new BorderLayout());
        add(componentLayoutPanel, BorderLayout.NORTH);
        add(new JScrollPane(contentTxtArea));

		SwingUtils.showOnScreenCenter(CompareToolGui.this);
		SwingUtils.updateUILookAndFeel(CompareToolGui.this);
		
		file1Txt.addMouseListener(new FileChooseEvent());
        file2Txt.addMouseListener(new FileChooseEvent());
        
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				file1Txt.setText("mrt1.xlsx");
//				file2Txt.setText("mrt2.xlsx");
				String file1 = file1Txt.getText();
				String file2 = file2Txt.getText();
				if (!"".equals(file1) && !"".equals(file2)) {
					new Thread(() -> {
						long start = System.currentTimeMillis();
						try {
							startBtn.setEnabled(false);
							contentTxtArea.setText("Start compare...\n");
							CompareTool.process(file1, file2, compareTypeBox.getSelectedItem().toString());
						} catch (Exception e1) {
							contentTxtArea.append("Error: " + e1.getMessage() + "\n");
							e1.printStackTrace();
						} finally {
							contentTxtArea.append("Done, total " + (System.currentTimeMillis() - start) / 1000 + "s");
							startBtn.setEnabled(true);
						}
					}).start();
				}
				
			}
		});
	}
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	CompareToolGui mainFrame = new CompareToolGui();
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
        });
    }
}

class FileChooseEvent extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
        JFileChooser fileChoose = new JFileChooser();
        String desktop = ".";
        fileChoose.setCurrentDirectory(new File(desktop));
        fileChoose.setDialogTitle("Please select the file");
        fileChoose.setFileFilter(new FileNameExtensionFilter("Excel(xls,xlsx)", "xls", "xlsx"));
        if (JFileChooser.APPROVE_OPTION == fileChoose.showOpenDialog(null)) {
            ((JTextField) e.getSource()).setText(fileChoose.getSelectedFile().getAbsolutePath());
        }
    }
}
