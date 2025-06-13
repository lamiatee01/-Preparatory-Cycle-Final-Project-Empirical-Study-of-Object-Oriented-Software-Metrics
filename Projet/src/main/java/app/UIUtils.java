package app;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class UIUtils {
	public static final Color darkBackground = new Color(30, 30, 50);
    public static final Color darkPanelBackground = new Color(40, 40, 60);
    public static final Color lightText = new Color(230, 230, 250);
    public static final Color primaryColor = new Color(75, 0, 130); // Indigo
    public static final Color secondaryColor = new Color(138, 43, 226); // BlueViolet
    public static final Color accentColor = new Color(255, 105, 180); // Hot Pink
    public static final Color successColor = new Color(46, 204, 113); // Emerald Green
    public static final Color warningColor = new Color(241, 196, 15); // Yellow
    public static final Color errorColor = new Color(231, 76, 60); // Red
    public static final Color[] funColors = {
            new Color(255, 105, 180), // Hot Pink
            new Color(255, 165, 0),   // Orange
            new Color(50, 205, 50),   // Lime Green
            new Color(30, 144, 255),  // Dodger Blue
            new Color(138, 43, 226)   // Blue Violet
        };
    
    
    public static JButton createGradientButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Create gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, color1, 
                    w, h, color2
                );
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, w, h, 15, 15));
                
                // Add a subtle shine effect
                GradientPaint shine = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 100),
                    0, h/2, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(shine);
                g2d.fill(new RoundRectangle2D.Double(0, 0, w, h/2, 15, 15));
                
                // Add some sparkles for fun
                g2d.setColor(new Color(255, 255, 255, 150));
                for (int i = 0; i < 5; i++) {
                    int x = (int)(Math.random() * w);
                    int y = (int)(Math.random() * h);
                    int size = 1 + (int)(Math.random() * 2);
                    g2d.fillOval(x, y, size, size);
                }
                
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(Font.BOLD, 17));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(Font.BOLD, 16));
            }
        });
        
        return button;
    }
    
    public static JSeparator createSeparator() {
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 20));
        separator.setForeground(new Color(255, 255, 255, 100));
        return separator;
    }
    
    
    
    public static Icon createCircleIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillOval(x, y, size, size);
                
                // Add a glow effect
                for (int i = 1; i <= 3; i++) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100 - i * 30));
                    g2d.drawOval(x - i, y - i, size + i * 2, size + i * 2);
                }
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return size;
            }
            
            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
    
    
    public static Icon createTextIcon(String text, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle background
                g2d.setColor(color);
                g2d.fillOval(x, y, size, size);
                
                // Add a glow effect
                for (int i = 1; i <= 3; i++) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100 - i * 30));
                    g2d.drawOval(x - i, y - i, size + i * 2, size + i * 2);
                }
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, x + (size - textWidth) / 2, y + (size + textHeight / 2) / 2);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return size;
            }
            
            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
    
    public static JButton createOutlinedButton(String text, Color color) {
        JButton button = new JButton(text) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Draw transparent background
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                
                // Draw border
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.draw(new RoundRectangle2D.Double(1, 1, w-2, h-2, 10, 10));
                
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(color);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(Font.BOLD));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(Font.PLAIN));
            }
        });
        
        return button;
    }
    
    public static JButton createNavButton(String text) {
        JButton button = new JButton(text) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Add a subtle glow effect on hover when mouse is over
                if (getModel().isRollover()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Get a random fun color for the glow
                    Color glowColor = funColors[(int)(Math.random() * funColors.length)];
                    g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    g2d.dispose();
                }
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(Font.BOLD));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(Font.PLAIN));
            }
        });
        
        return button;
    }
    
    
    public static void addFeatureItem( String title, String description, JPanel container) {
        JPanel itemPanel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Draw rounded rectangle with subtle gradient for dark theme
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(60, 60, 80), 
                    w, 0, new Color(70, 70, 90)
                );
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                
                // Add a subtle border
                g2d.setColor(new Color(100, 100, 150));
                g2d.draw(new RoundRectangle2D.Double(0, 0, w-1, h-1, 10, 10));
                
                g2d.dispose();
            }
            
            //public Dimension setPreferedSize() {}
        };
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(UIUtils.lightText);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(UIUtils.lightText);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        
        //itemPanel.add(iconLabel, BorderLayout.WEST);
        itemPanel.add(textPanel, BorderLayout.CENTER);
        
        
        // Add hover effect
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(UIUtils.accentColor, 1)
                ));
                
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
        });
       
        
        container.add(itemPanel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    
    public static void addMetricSection(String title, JPanel container) {
        JPanel sectionPanel = new JPanel() {
        	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
          
            
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // Draw rounded rectangle with gradient for dark theme
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(60, 60, 80), 
                    w, 0, new Color(70, 70, 90)
                );
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, w, h, 10, 10));
                
                // Add a subtle border
                g2d.setColor(new Color(100, 100, 150));
                g2d.draw(new RoundRectangle2D.Double(0, 0, w-1, h-1, 10, 10));
                
                g2d.dispose();
            }
        };
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(UIUtils.lightText);
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        
        sectionPanel.add(leftPanel, BorderLayout.CENTER);
        
        // Add click listener to expand/collapse with fun animation
        sectionPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sectionPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(UIUtils.secondaryColor, 1)
                ));
                
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
            
           
        });
        
        container.add(sectionPanel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    
}
