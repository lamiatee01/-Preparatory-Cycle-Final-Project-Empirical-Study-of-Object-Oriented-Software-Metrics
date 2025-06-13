package app;

import java.awt.CardLayout;
import java.awt.MediaTracker;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CardLayout cardLayout;
    private JPanel mainPanel;
    private WelcomePanel welcomePanel;
    private AnalysisPanel analysisPanel;

    public MainFrame() {
        super("Defect Lens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupMainPanel();
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            System.out.println("Icon not found, continuing without it.");
        }
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        welcomePanel = new WelcomePanel(this);
        analysisPanel = new AnalysisPanel(this);
    }

    private void setupMainPanel() {
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(analysisPanel, "analysis");
        add(mainPanel);
        
        showWelcomeScreen();
    }

    public void showWelcomeScreen() {
        cardLayout.show(mainPanel, "welcome");
    }

    public void showAnalysisScreen() {
        cardLayout.show(mainPanel, "analysis");
    }
}
