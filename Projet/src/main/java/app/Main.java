package app;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

public class Main {
	public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", new ColorUIResource(40, 40, 60));
            UIManager.put("Panel.background", new ColorUIResource(40, 40, 60));
            UIManager.put("OptionPane.messageForeground", new ColorUIResource(230, 230, 250));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
