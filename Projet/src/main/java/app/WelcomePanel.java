package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;

import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class WelcomePanel extends JPanel {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private JButton startAnalysisButton;
	private JButton aboutUsButton;
	private JButton descriptionButton;

    public WelcomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        
        initUI();
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Create gradient background
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(75, 0, 130, 240), 
            w, h, new Color(138, 43, 226, 200)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        
        // Add some decorative elements
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 5; i++) {
            int size = 100 + i * 50;
            g2d.drawOval(w/2 - size/2, h/2 - size/2, size, size);
        }
        
        // Add some fun floating elements
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        for (int i = 0; i < 10; i++) {
            int colorIndex = i % UIUtils.funColors.length;
            g2d.setColor(new Color(UIUtils.funColors[colorIndex].getRed(), 
                                  UIUtils.funColors[colorIndex].getGreen(), 
                                  UIUtils.funColors[colorIndex].getBlue(), 50));
        }
        
        g2d.dispose();
    }

    private void initUI() {
        // Create center panel with title and button
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(10, 0, 10, 0);
		
		
		// Create content card
		JPanel contentCard = createContentCard();
		
		// Create bottom panel with buttons
		JPanel bottomPanel = createBottomPanel();
		centerPanel.add(contentCard,gbc);
		
		// Add panels to welcome panel
	    add(centerPanel, BorderLayout.CENTER);
	    add(bottomPanel, BorderLayout.SOUTH);
	}

	private JPanel createContentCard() {
		JPanel contentCard = new JPanel() {
	          /**
	 * 
	 */
			private static final long serialVersionUID = 1L;
	
	@Override
	  		protected void paintComponent(Graphics g) {
			    super.paintComponent(g);
			    Graphics2D g2d = (Graphics2D) g.create();
			    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		      
		      // Draw rounded rectangle with semi-transparent white background
				g2d.setColor(new Color(255, 255, 255, 220));
				g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
		  
				// Add a subtle border
		        g2d.setColor(new Color(200, 200, 255, 100));
		        g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, 20, 20));
		          
		        g2d.dispose();
	      }
	  };
	  contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
	  contentCard.setOpaque(false);
	  contentCard.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
  
	  // Add logo/icon at the top
	  ImageIcon logoIcon = new ImageIcon(getClass().getResource("/ICON.PNG"));
	  Image scaledImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
	  JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
	  logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	  logoLabel.setToolTipText("Defect Lens");
	  
	  // Add title
	  JLabel titleLabel = new JLabel("Welcome to Defect Lens!");
	  titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
	  titleLabel.setForeground(new Color(50, 50, 70));
	  titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	  
	  // Add subtitle
	  JLabel subtitleLabel = new JLabel("Discover insights into your Java projects with just a few clicks.");
	  subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
	  subtitleLabel.setForeground(new Color(50, 50, 70));
	  subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	  
	  // Add start analysis button
	  startAnalysisButton = UIUtils.createGradientButton("Start Analysis", UIUtils.primaryColor, UIUtils.secondaryColor);
	  startAnalysisButton.setPreferredSize(new Dimension(150,40));
	  startAnalysisButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	  startAnalysisButton.addActionListener(e -> {mainFrame.showAnalysisScreen();});
  
  // Add components to content card
      contentCard.add(logoLabel);
      contentCard.add(Box.createRigidArea(new Dimension(0, 20)));
      contentCard.add(titleLabel);
      contentCard.add(Box.createRigidArea(new Dimension(0, 15)));
      contentCard.add(subtitleLabel);
      contentCard.add(Box.createRigidArea(new Dimension(0, 30)));
      contentCard.add(startAnalysisButton);
      
      return contentCard;
	}

	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		bottomPanel.setOpaque(false);
    
		// Add about us button
		aboutUsButton = UIUtils.createOutlinedButton("About Us", UIUtils.lightText);
		aboutUsButton.addActionListener(e -> showAboutDialog());

		// Add description button
		descriptionButton = UIUtils.createOutlinedButton("Description", UIUtils.lightText);
		descriptionButton.addActionListener(e -> showDescriptionDialog());
    
   
    
	    // Add buttons to bottom panel
	    bottomPanel.add(aboutUsButton);
	    bottomPanel.add(descriptionButton);
	    return bottomPanel;
	}


	private void showAboutDialog() {
		// Create custom about dialog
		JDialog aboutDialog = new JDialog(mainFrame, "About", true);
        aboutDialog.setLayout(new BorderLayout());
        
   
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/ICON.PNG"));
  	  	Image scaledImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
  	  	JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
  	  	logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
  	  	logoLabel.setToolTipText("Defect Lens");

		JLabel titleLabel = new JLabel("Defect Lens");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		titleLabel.setForeground(UIUtils.accentColor);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		
		
		JLabel descriptionLabel = new JLabel("<html><center>Made by a group of USTHB INFO ING 2 students: <br> </center></html>");
		descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		descriptionLabel.setForeground(Color.WHITE);
		descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
		
		JLabel name1Label = new JLabel("<html><center>BENSLIMANE Salima Lamia <br></center></html>");
		name1Label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		name1Label.setForeground(Color.WHITE);
		name1Label.setHorizontalAlignment(JLabel.CENTER);
		
		JLabel name2Label = new JLabel("<html><center>BOUDIAF Aicha <br></center></html>");
		name2Label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		name2Label.setForeground(Color.WHITE);
		name2Label.setHorizontalAlignment(JLabel.CENTER);
		
		JLabel name3Label = new JLabel("<html><center>KAMIRI Lilia<br></center></html>");
		name3Label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		name3Label.setForeground(Color.WHITE);
		name3Label.setHorizontalAlignment(JLabel.CENTER);
		
		JLabel name4Label = new JLabel("<html><center>TAIB BENABBES Nesrine<br></center></html>");
		name4Label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		name4Label.setForeground(Color.WHITE);
		name4Label.setHorizontalAlignment(JLabel.CENTER);
			
		JLabel name5Label = new JLabel("<html><center>YAHIAOUI Chahinez </center></html>");
		name5Label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		name5Label.setForeground(Color.WHITE);
		name5Label.setHorizontalAlignment(JLabel.CENTER);
		
		JButton closeButton = UIUtils.createGradientButton("Close", UIUtils.accentColor, UIUtils.secondaryColor);
		closeButton.setPreferredSize(new Dimension(100, 40));
		closeButton.addActionListener(e -> aboutDialog.dispose());
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.add(closeButton);
		
		logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		name1Label.setAlignmentX(Component.CENTER_ALIGNMENT);
		name2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
		name3Label.setAlignmentX(Component.CENTER_ALIGNMENT);
		name4Label.setAlignmentX(Component.CENTER_ALIGNMENT);
		name5Label.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		contentPanel.setOpaque(false);
		contentPanel.add(logoLabel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		contentPanel.add(titleLabel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); 
		contentPanel.add(descriptionLabel);
		contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	    contentPanel.add(name1Label);
	    contentPanel.add(name2Label);
	    contentPanel.add(name3Label);
	    contentPanel.add(name4Label);
	    contentPanel.add(name5Label);
	    
	    dialogPanel.add(contentPanel, BorderLayout.CENTER);
	    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
	    
	    aboutDialog.add(dialogPanel);
	    aboutDialog.pack();
	    aboutDialog.setSize(500, 450);
	    aboutDialog.setLocationRelativeTo(mainFrame);
	    aboutDialog.setResizable(false);
	    aboutDialog.setVisible(true);
	}


	private void showDescriptionDialog() {
		// Create custom description dialog
		JDialog descDialog = new JDialog(mainFrame, "Description", true);
		descDialog.setLayout(new BorderLayout());

		JPanel dialogPanel = new JPanel(new BorderLayout()) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
			    super.paintComponent(g);
			    Graphics2D g2d = (Graphics2D) g.create();
			    
			    // Create gradient background
			        int w = getWidth();
			        int h = getHeight();
			        GradientPaint gp = new GradientPaint(
			            0, 0, new Color(40, 40, 60), 
			            w, h, new Color(60, 60, 80)
			        );
			        g2d.setPaint(gp);
			        g2d.fillRect(0, 0, w, h);
			        
			        
			        g2d.dispose();
			    }
			};
		dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel titleLabel = new JLabel("Defect Lens Main Features");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		titleLabel.setForeground(UIUtils.accentColor);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
   
	    JPanel featuresPanel = new JPanel();
		featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
		featuresPanel.setOpaque(true);
		featuresPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		JScrollPane ScrollPane = new JScrollPane(featuresPanel);
		ScrollPane.setPreferredSize(new Dimension(100, 0));
		ScrollPane.setBorder(BorderFactory.createEmptyBorder());
   
    
		// Add feature items
		UIUtils.addFeatureItem("Import Analysis", "Detects used, unused,duplicate imports, and more", featuresPanel);
		UIUtils.addFeatureItem("Exception Analysis", "Analyzes exception handling patterns", featuresPanel);
		UIUtils.addFeatureItem("Encapsulation Analysis", "Measures attribute visibility and encapsulation", featuresPanel);
		UIUtils.addFeatureItem("Method Analysis", "Analyzes method overloading, overriding, and more", featuresPanel);
		UIUtils.addFeatureItem("Inheritance Analysis", "Calculates depth of inheritance tree", featuresPanel);
		UIUtils.addFeatureItem("Size Analysis", "Calculates number of lines, declared classes, and more ", featuresPanel);
		  
		UIUtils.addFeatureItem("Export", "Results can be exported to CSV for further use", featuresPanel);
		UIUtils.addFeatureItem("History", "All previous results are saved for later access", featuresPanel);

		JButton closeButton = UIUtils.createGradientButton("Close", UIUtils.accentColor, UIUtils.secondaryColor);
		closeButton.setPreferredSize(new Dimension(100, 40));
		closeButton.addActionListener(e -> descDialog.dispose());
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.add(closeButton);

		dialogPanel.add(titleLabel, BorderLayout.NORTH);
		//dialogPanel.add(featuresPanel, BorderLayout.CENTER);
		dialogPanel.add(ScrollPane, BorderLayout.CENTER);
		dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

	    descDialog.add(dialogPanel);
	    descDialog.pack();
	    descDialog.setSize(450, 450);
	    descDialog.setLocationRelativeTo(mainFrame);
	    descDialog.setResizable(false);
	    descDialog.setVisible(true);
	}
	    

	    
	    
}
