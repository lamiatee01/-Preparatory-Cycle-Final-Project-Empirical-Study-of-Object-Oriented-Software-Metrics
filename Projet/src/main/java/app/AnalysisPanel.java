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
import java.awt.GridLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;







public class AnalysisPanel extends JPanel implements AnalysisResultHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton homeButton;
    private JButton analyzeProjectButton;
    private JButton analyzeFileButton;
    private JButton exportResultsButton;
    private JButton historyButton;
    private JLabel statusLabel;
    private FileAnalyzer fileAnalyzer;
    private List<MetricsData> analysisResults = new ArrayList<>();
    private List<AnalysisRecord> analysisHistory = new ArrayList<>();
    private DefaultListModel<String> historyListModel = new DefaultListModel<>();
    private JList<String> historyList;
    private JButton deleteButton, deleteAllButton,closeHistoryButton;
    private static final String HISTORY_FILE = "analysis_history.dat";
    
    
    
    public AnalysisPanel(MainFrame mainFrame) {
    	fileAnalyzer = new FileAnalyzer(this);
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIUtils.darkBackground);
        loadHistory();
        initComponents();
    }

    private void initComponents() {
        // Create navigation panel
        JPanel navPanel = createNavPanel();
        
        // Create results table
        createResultsTable();
        
        // Create metrics panel
        JPanel metricsPanel = createMetricsPanel();
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(resultsTable),
            new JScrollPane(metricsPanel)
        );
        splitPane.setDividerLocation(800);
        
        // Add components to analysis panel
        add(navPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
    	JPanel navPanel = new JPanel() {
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
                    0, 0, UIUtils.primaryColor, 
                    w, 0, UIUtils.secondaryColor
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                // Add some fun elements
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                for (int i = 0; i < 5; i++) {
                    int x = (int)(Math.random() * w);
                    int y = (int)(Math.random() * h);
                    int colorIndex = i % UIUtils.funColors.length;
                    g2d.setColor(new Color(UIUtils.funColors[colorIndex].getRed(), 
                                          UIUtils.funColors[colorIndex].getGreen(), 
                                          UIUtils.funColors[colorIndex].getBlue(), 80));
                    g2d.fillOval(x, y, 10, 10);
                }
                
                g2d.dispose();
            }
        };
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        homeButton = UIUtils.createNavButton("Home");
        homeButton.addActionListener(e -> {mainFrame.showWelcomeScreen();});
        
        analyzeProjectButton = UIUtils.createNavButton("Analyze Project");
        analyzeProjectButton.addActionListener(e -> selectAndAnalyzeProject());
        
        analyzeFileButton = UIUtils.createNavButton("Analyze File");
        analyzeFileButton.addActionListener(e -> selectAndAnalyzeFile());    
        historyButton = UIUtils.createNavButton("History");
        historyButton.addActionListener(e -> showHistoryDialog());
        
        exportResultsButton = UIUtils.createNavButton("Export Results");
        exportResultsButton.addActionListener(e -> exportResultsToCSV());
        
        
        statusLabel = new JLabel("Ready to analyze");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setIcon(UIUtils.createCircleIcon(10, UIUtils.successColor));
        
        // Add components to nav panel
        navPanel.add(homeButton);
        navPanel.add(UIUtils.createSeparator());
        navPanel.add(analyzeProjectButton);
        navPanel.add(UIUtils.createSeparator());
        navPanel.add(analyzeFileButton);
        navPanel.add(UIUtils.createSeparator());
        navPanel.add(historyButton);
        navPanel.add(UIUtils.createSeparator());
        navPanel.add(exportResultsButton);
        
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(statusLabel);
        return navPanel;
    }

    private void createResultsTable() {
        String[] columnNames = {
                "Class Name", "ICT","ICU","ICUN","ICD","ICUJ","ICC","ICW","CBO","TA","PUBA","PRIA","PROA","DA","LCOM5","JAXTL","JAXCL","JAXCD","JAXNC","JAXID","JAXII","JAXAC","JAXAM","NOC","TNE","JEC","CEC","JCKE","RTE","ERR","DIT","TM","EDM","ORM","OLM","ORR","OLR","ORMNA","ECORM","EOLM","AOLM","OOM","OMSC"
            };
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        
        
        //setting the size of each column
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel columnModel = resultsTable.getColumnModel();
     
    	columnModel.getColumn(0).setPreferredWidth(200);
    	for (int i = 1; i < 42; i++) {
    	    columnModel.getColumn(i).setPreferredWidth(70);
    	}
        
        
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(30);
        resultsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultsTable.setGridColor(new Color(80, 80, 100));
        resultsTable.setBackground(UIUtils.darkPanelBackground);
        resultsTable.setForeground(UIUtils.lightText);
        resultsTable.setSelectionBackground(new Color(138, 43, 226, 100));
        resultsTable.setSelectionForeground(UIUtils.lightText);
        resultsTable.getTableHeader().setBackground(UIUtils.primaryColor);
        resultsTable.getTableHeader().setForeground(UIUtils.primaryColor);
        resultsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UIUtils.secondaryColor));
        
    }

    
    private JPanel createMetricsPanel() {
    	JPanel metricsPanel = new JPanel() {
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
                    0, 0, new Color(30, 30, 50), 
                    w, h, new Color(40, 40, 70)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                // Add some fun elements
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                for (int i = 0; i < 3; i++) {
                  
                    int colorIndex = i % UIUtils.funColors.length;
                    
                    g2d.setColor(new Color(UIUtils.funColors[colorIndex].getRed(), 
                                          UIUtils.funColors[colorIndex].getGreen(), 
                                          UIUtils.funColors[colorIndex].getBlue(), 40));
                }
                
                g2d.dispose();
            }
        };
        metricsPanel.setLayout(new BoxLayout(metricsPanel, BoxLayout.Y_AXIS));
        metricsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel metricsLabel = new JLabel("Metrics Analyzed");
        metricsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        metricsLabel.setForeground(UIUtils.accentColor);
        metricsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metricsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        metricsPanel.add(metricsLabel);
        
        
        
        // Add metric sections 
        //Attributes
        UIUtils.addMetricSection("Total Imports (ICT)", metricsPanel);
        UIUtils.addMetricSection("Used Imports (ICU)", metricsPanel);
        UIUtils.addMetricSection("Unused Imports (ICUN)", metricsPanel);
        UIUtils.addMetricSection("Import Dublicates (ICD)", metricsPanel);
        UIUtils.addMetricSection("Ungudged Imports (ICUJ)", metricsPanel);
        UIUtils.addMetricSection("Import Conflicts (ICC)", metricsPanel);
        
        UIUtils.addMetricSection("Total Attributes (TA)", metricsPanel);
        UIUtils.addMetricSection("Public Attributes (PUBA)", metricsPanel);
        UIUtils.addMetricSection("Private Attributes (PRIA)", metricsPanel);
        UIUtils.addMetricSection("Protected Attributes (PROA)", metricsPanel);
        UIUtils.addMetricSection("Default Attributes (DA)", metricsPanel);
        
        UIUtils.addMetricSection("LCOM5 (LCOM5)", metricsPanel);
        
        UIUtils.addMetricSection("Total Lines (JAXTL)", metricsPanel);
        UIUtils.addMetricSection("Comment Lines (JAXCL)", metricsPanel);
        UIUtils.addMetricSection("Classes Declared (JAXCD)", metricsPanel);
        UIUtils.addMetricSection("Nested Classes(JAXNC)", metricsPanel);
        UIUtils.addMetricSection("Interfaces Declared (JAXID)", metricsPanel);
        UIUtils.addMetricSection("Implemented Interfaces (JAXII)", metricsPanel);
        UIUtils.addMetricSection("Abstract CLasses (JAXAC)", metricsPanel);
        UIUtils.addMetricSection("Abstract Methods (JAXAM)", metricsPanel);
        
        UIUtils.addMetricSection("Number of Children (NOC)", metricsPanel);
        
        UIUtils.addMetricSection("JDK Exceptions Count (JEC)", metricsPanel);        
        UIUtils.addMetricSection("Custom Exceptions Count (CEC)", metricsPanel);
        UIUtils.addMetricSection("JDK Checked Exceptions (JCKE)", metricsPanel);
        UIUtils.addMetricSection("Runtime Exceptions (RTE)", metricsPanel);
        UIUtils.addMetricSection("Errors (ERR)", metricsPanel);
    
        UIUtils.addMetricSection("Depth of Inheritance Tree (DIT)", metricsPanel);
        
        UIUtils.addMetricSection("Total Methods (TM)", metricsPanel);
        UIUtils.addMetricSection("Explicitly Declared Methods (EDM)", metricsPanel);
        UIUtils.addMetricSection("Overridden Methods (ORM)", metricsPanel);    
        UIUtils.addMetricSection("Overloaded Methods (OLM)", metricsPanel);
        UIUtils.addMetricSection("Override Ratio (ORR)", metricsPanel);
        UIUtils.addMetricSection("Overload Ratio (OLR)", metricsPanel);
        UIUtils.addMetricSection("Overridden Methods no Anotation (ORMNA) ", metricsPanel);
        UIUtils.addMetricSection("Empty or Constant Overriden Methods (ECORM)", metricsPanel);
        UIUtils.addMetricSection("Excessively Overloaded Methods (EOLM)", metricsPanel);
        UIUtils.addMetricSection("Ambiguously Overloaded Methods (AOLM)", metricsPanel);
        UIUtils.addMetricSection("Methods Overloaded and Overridden (OOM)", metricsPanel);
        UIUtils.addMetricSection("Overridden Methods with super() Call (OMSC)", metricsPanel);
        
        return metricsPanel;
    }
    
    
    


@Override
	public void handleResults(List<MetricsData> results,String path) {
    	SwingUtilities.invokeLater(() -> {
    		// Store results
    		analysisResults.clear();
    		analysisResults.addAll(results);
        
    		// Update table
    		tableModel.setRowCount(0);
    		results.forEach(this::addMetricsToTable);
        
    		
    		boolean isProject = results.size() > 1;
    		String name;
        
    		try {
    			if (isProject) {
    				//File parent = new File(results.get(0).getClassName()).getParentFile();
    				name = defectsUtils.Utility.getClassName(path);
    			} else {
    				name = new File(results.get(0).getClassName()).getName();
    			}
    		} catch (Exception e) {
    			name = "unknown";
    		}
        
        analysisHistory.add(new AnalysisRecord(name, new ArrayList<>(results), isProject));
        saveHistory();
        updateHistoryList();
        
        updateStatus(isProject ? 
            "Analysis complete. Found " + results.size() + " files." : 
            "Analysis complete.", 
            UIUtils.successColor);
    });
	}

    @Override
    public void handleError(Exception e) {
        SwingUtilities.invokeLater(() -> {
            updateStatus("Analysis failed: " + e.getMessage(), UIUtils.errorColor);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    public void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setIcon(UIUtils.createCircleIcon(10, color));
    }

    
    private void selectAndAnalyzeProject() {
        JFileChooser chooser = createFileChooser("Select Project Directory",JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            updateStatus("Analyzing...", UIUtils.warningColor);
            fileAnalyzer.analyzeProject(chooser.getSelectedFile());
        }
    }

    private void selectAndAnalyzeFile() {
        JFileChooser chooser = createFileChooser("Select Java File",JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("Java Files", "java"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            updateStatus("Analyzing...", UIUtils.warningColor);
            fileAnalyzer.analyzeFile(chooser.getSelectedFile());
        }
    }
    
    private void addMetricsToTable(MetricsData metrics) {
        Object[] row = {
        		metrics.getClassName(),
        		
        		metrics.getTotalImports(),
        		metrics.getUsedImports(),
        		metrics.getUnusedImports(),
        		metrics.getDublicateimports(),
        		metrics.getUnjudgedImports(),
        		metrics.getImportConflicts(),
        		metrics.getWildCardImports(),

        		metrics.getCBO(),

        		metrics.getTotalEnc(),
        		metrics.getPublic(),
        		metrics.getPrivate(),
        		metrics.getProtected(),
        		metrics.getDefault(),
        		metrics.getLCOM5(),

        		metrics.getTotalLines(),
        		metrics.getLinesWithComments(),
        		metrics.getClassDeclaration(),
        		metrics.getNestedClasses(),
        		metrics.getInterfaceDeclaration(),
        		metrics.getImplementedInterfaces(),
        		metrics.getAbstractClasses(),
        		metrics.getAbstractMethods(),

        		metrics.getNOC(),

        		metrics.getTotal(),
        		metrics.getJdkExceptions(),
        		metrics.getCustomExceptions(),
        		metrics.getJdkCheckedExceptions(),
        		metrics.getJdkRuntimeExceptions(),
        		metrics.getJdkErrors(),

        		metrics.getDIT(),
        		metrics.getTotalMethods(),
        		metrics.getExplictmethods(),
        		metrics.getOrmethods(),
        		metrics.getOlmethods(),
        		metrics.getOrratio(),
        		metrics.getOlration(),
        		metrics.getOrwithnoano(),
        		metrics.getMtconstmethods(),
        		metrics.getExcesiveol(),
        		metrics.getAmbgmethods(),
        		metrics.getOlandor(),
        		metrics.getOrwithsuper()

        };
        
        tableModel.addRow(row);
    }

    
    private JFileChooser createFileChooser(String title,int mode) {
    	JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(mode);
        
        // Style the file chooser for dark theme
        UIManager.put("FileChooser.background", new ColorUIResource(40, 40, 60));
        UIManager.put("FileChooser.foreground", new ColorUIResource(230, 230, 250));
        UIManager.put("FileChooser.listBackground", new ColorUIResource(50, 50, 70));
        UIManager.put("FileChooser.listForeground", new ColorUIResource(230, 230, 250));
		return fileChooser;
    }
    
    
    private void exportResultsToCSV() {
        if (analysisResults.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, 
                "No analysis results to export.", 
                "Export Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setSelectedFile(new File("metrics_analysis.csv"));
        
        // Style the file chooser for dark theme
        UIManager.put("FileChooser.background", new ColorUIResource(40, 40, 60));
        UIManager.put("FileChooser.foreground", new ColorUIResource(230, 230, 250));
        UIManager.put("FileChooser.listBackground", new ColorUIResource(50, 50, 70));
        UIManager.put("FileChooser.listForeground", new ColorUIResource(230, 230, 250));
        
        int result = fileChooser.showSaveDialog(mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Add .csv extension if not present
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
            	
         
                writer.println("Class Name ,ICT,ICU,ICUN,ICD,ICUJ,ICC,ICW,CBO,TA,PUBA,PRIA,PROA,DA,LCOM5,JAXTL,JAXCL,JAXCD,JAXNC,JAXID,JAXII,JAXAC,JAXAM,NOC,TNE,JEC,CEC,JCKE,RTE,ERR,DIT,TM,EDM,ORM,OLM,ORR,OLR,ORMNA,ECORM,EOLM,AOLM,OOM,OMSC");
                
                // Write data
                for (MetricsData metrics : analysisResults) {
                    writer.println(
                    	
                    	    metrics.getClassName() + "," +
                    	    metrics.getTotalImports() + "," +
                    	    metrics.getUsedImports() + "," +
                    	    metrics.getUnusedImports() + "," +
                    	    metrics.getDublicateimports() + "," +
                    	    metrics.getUnjudgedImports() + "," +
                    	    metrics.getImportConflicts() + "," +
                    	    metrics.getWildCardImports() + "," +
                    	    

                    	    // CBO
                    	    metrics.getCBO() + "," +

                    	    // Encapsulation
                    	    metrics.getTotalEnc() + "," +
                    	    metrics.getPublic() + "," +
                    	    metrics.getPrivate() + "," +
                    	    metrics.getProtected() + "," +
                    	    metrics.getDefault() + "," +
                    	    metrics.getLCOM5() + "," +
                    	    
                    	    // Javalyzerx
                    	    
                    	    metrics.getTotalLines() + "," +
                    	    metrics.getLinesWithComments() + "," +
                    	    metrics.getClassDeclaration() + "," +
                    	    metrics.getNestedClasses() + "," +
                    	    metrics.getInterfaceDeclaration() + "," +
                    	    metrics.getImplementedInterfaces() + "," +
                    	    metrics.getAbstractClasses() + "," +
                    	    metrics.getAbstractMethods() + "," +
                    	   
                    	    
                    	    

                    	    // NOC 
                    	    metrics.getNOC() + "," +

                    	    // Exceptions
                    	    metrics.getTotal() + "," +
                    	    metrics.getJdkExceptions() + "," +
                    	    metrics.getCustomExceptions() + "," +
                    	    metrics.getJdkCheckedExceptions() + "," +
                    	    metrics.getJdkRuntimeExceptions() + "," +
                    	    metrics.getJdkErrors() + "," +
                    	   

                    	    //OOMR
                    	    metrics.getDIT() + "," +
                    	    metrics.getTotalMethods() + "," +
                    	    metrics.getExplictmethods() + "," +
                    	    metrics.getOrmethods() + "," +
                    	    metrics.getOlmethods() + "," +
                    	    metrics.getOrratio() + "," +
                    	    metrics.getOlration() + "," +
                    	    metrics.getOrwithnoano() + "," +
                    	    metrics.getMtconstmethods() + "," +
                    	    metrics.getExcesiveol() + "," +
                    	    metrics.getAmbgmethods() + "," +
                    	    metrics.getOlandor() + "," +
                    	    metrics.getOrwithsuper()

                    );
                }
                
                // Show success dialog with custom styling
                JDialog successDialog = new JDialog(mainFrame, "Export Successful", true);
                successDialog.setLayout(new BorderLayout());
                
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
                        
                        // Add some celebration confetti
                        for (int i = 0; i < 30; i++) {
                            int x = (int)(Math.random() * w);
                            int y = (int)(Math.random() * h);
                            int size = 2 + (int)(Math.random() * 4);
                            int colorIndex = i % UIUtils.funColors.length;
                            g2d.setColor(UIUtils.funColors[colorIndex]);
                            g2d.fillRect(x, y, size, size);
                        }
                        
                        g2d.dispose();
                    }
                };
                dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                
                JLabel messageLabel = new JLabel("Results exported successfully to:");
                messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                messageLabel.setForeground(UIUtils.lightText);
                messageLabel.setHorizontalAlignment(JLabel.CENTER);
                
                JLabel pathLabel = new JLabel(file.getAbsolutePath());
                pathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                pathLabel.setForeground(UIUtils.lightText);
                pathLabel.setHorizontalAlignment(JLabel.CENTER);
                
                JButton okButton = UIUtils.createGradientButton("OK", UIUtils.successColor, new Color(46, 204, 113, 200));
                okButton.setPreferredSize(new Dimension(100, 40));
                okButton.addActionListener(e -> successDialog.dispose());
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.setOpaque(false);
                buttonPanel.add(okButton);
                
                JPanel messagePanel = new JPanel(new GridLayout(3, 1, 0, 10));
                
                messagePanel.setOpaque(false);
                messagePanel.add(messageLabel);
                messagePanel.add(pathLabel);
                
                dialogPanel.add(messagePanel, BorderLayout.CENTER);
                dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                successDialog.add(dialogPanel);
                successDialog.pack();
                successDialog.setSize(400, 250);
                successDialog.setLocationRelativeTo(mainFrame);
                successDialog.setResizable(false);
                successDialog.setVisible(true);
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error exporting results: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showHistoryDialog() {
        

        JDialog historyDialog = new JDialog(mainFrame, "Analysis History", true);
        historyDialog.setLayout(new BorderLayout());

        JPanel dialogPanel = new JPanel(new BorderLayout()) {
            
		
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                int w = getWidth();
                int h = getHeight();

                // Gradient background
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

        JLabel titleLabel = new JLabel("Analysis History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIUtils.accentColor);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        historyList = new JList<>(historyListModel);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyList.setBackground(new Color(30, 30, 45));
        historyList.setForeground(Color.WHITE);
        historyList.setSelectionBackground(UIUtils.accentColor);
        historyList.setSelectionForeground(Color.WHITE);
        historyList.setOpaque(true);
        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !historyList.isSelectionEmpty()) {
                loadSelectedAnalysis();
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        deleteButton = UIUtils.createGradientButton("Delete Selected", UIUtils.primaryColor, UIUtils.secondaryColor);
        deleteButton.addActionListener(e -> deleteSelectedAnalysis());
        deleteButton.setPreferredSize(new Dimension(150,40));

        deleteAllButton = UIUtils.createGradientButton("Delete All", UIUtils.primaryColor, UIUtils.secondaryColor);
        deleteAllButton.addActionListener(e -> deleteAllAnalyses());
        deleteAllButton.setPreferredSize(new Dimension(150,40));

        closeHistoryButton = UIUtils.createGradientButton("Close", UIUtils.accentColor, UIUtils.secondaryColor);
        closeHistoryButton.setPreferredSize(new Dimension(150,40));
        closeHistoryButton.addActionListener(e -> historyDialog.dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(deleteAllButton);
        buttonPanel.add(closeHistoryButton);
        updateHistoryList();
        dialogPanel.add(titleLabel, BorderLayout.NORTH);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        historyDialog.add(dialogPanel);
        historyDialog.pack();
       // historyDialog.setSize(500, 450);
        historyDialog.setLocationRelativeTo(mainFrame);
        historyDialog.setResizable(false);
        historyDialog.setVisible(true);
    }


	private void updateHistoryList() {
	    historyListModel.clear();
	    analysisHistory.forEach(record -> 
	        historyListModel.addElement(record.getDisplayName()));
	}
	
	private void loadSelectedAnalysis() {
	    int index = historyList.getSelectedIndex();
	    if (index >= 0) {
	        AnalysisRecord record = analysisHistory.get(index);
	        tableModel.setRowCount(0);
	        record.metrics.forEach(this::addMetricsToTable);
	        analysisResults.clear();
	        analysisResults.addAll(record.metrics);
	        statusLabel.setText("Viewing: " + record.getDisplayName());
	    }
	}
	
	private void deleteSelectedAnalysis() {
	    int index = historyList.getSelectedIndex();
	    if (index >= 0) {
	        analysisHistory.remove(index);
	        saveHistory();
	        updateHistoryList();
	        tableModel.setRowCount(0);
	        statusLabel.setText("Analysis deleted");
	    }
	}
	
	private void deleteAllAnalyses() {
	    int confirm = JOptionPane.showConfirmDialog(mainFrame, 
	        "Delete ALL analysis history?", "Confirm", 
	    JOptionPane.YES_NO_OPTION);
	
	if (confirm == JOptionPane.YES_OPTION) {
	    analysisHistory.clear();
	    saveHistory();
	    updateHistoryList();
	    tableModel.setRowCount(0);
	    statusLabel.setText("All history cleared");
	    }
	}
	
	
	
	private void saveHistory() {
	    try (ObjectOutputStream oos = new ObjectOutputStream(
	            new FileOutputStream(HISTORY_FILE))) {
	        oos.writeObject(analysisHistory);
	    } catch (IOException e) {
	        System.err.println("Error saving history: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void loadHistory() {
	    File file = new File(HISTORY_FILE);
	    if (file.exists()) {
	        try (ObjectInputStream ois = new ObjectInputStream(
	                new FileInputStream(file))) {
	            analysisHistory = (List<AnalysisRecord>) ois.readObject();
	            updateHistoryList();
	        } catch (IOException | ClassNotFoundException e) {
	            System.err.println("Error loading history: " + e.getMessage());
	        }
	    }
	}


}
