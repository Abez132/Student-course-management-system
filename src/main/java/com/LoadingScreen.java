package com;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class LoadingScreen extends JFrame {
    private JLabel loadingLabel;
    private JLabel messageLabel;
    private JProgressBar progressBar;
    private Timer timer;
    private Timer rotationTimer;
    private double rotationAngle = 0;
    private String baseText = "Loading";
    private int dots = 0;

    public LoadingScreen() {
        setUndecorated(true);
        setSize(400, 200);  // Slightly larger for better spacing
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        
        // Main panel with modern shadow effect
        JPanel panel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Modern shadow effect
                for (int i = 0; i < 5; i++) {
                    g2d.setColor(new Color(0, 0, 0, 20 - i * 4));
                    g2d.fillRoundRect(5 + i, 5 + i, getWidth() - 10 - i * 2, getHeight() - 10 - i * 2, 30, 30);
                }
                
                // Main panel with gradient
                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 35), 0, getHeight(), new Color(45, 45, 50));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
                
                // Glossy border effect
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.drawRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));  // More padding
        
        // Spinner with modern look
        JPanel spinnerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int size = Math.min(width, height) - 10;
                int x = (width - size) / 2;
                int y = (height - size) / 2;
                
                // Track
                g2d.setColor(new Color(60, 60, 70));
                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawArc(x, y, size, size, 0, 360);
                
                // Animated part with gradient
                int arcStart = (int) Math.toDegrees(rotationAngle);
                GradientPaint spinnerGradient = new GradientPaint(
                    x, y, new Color(100, 180, 255),
                    x + size, y + size, new Color(0, 120, 255)
                );
                g2d.setPaint(spinnerGradient);
                g2d.drawArc(x, y, size, size, arcStart, 90);
                
                // Inner glow
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawArc(x + 2, y + 2, size - 4, size - 4, arcStart, 45);
            }
        };
        spinnerPanel.setPreferredSize(new Dimension(70, 70));
        
        // Labels with modern typography
        loadingLabel = new JLabel(baseText, SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));
        loadingLabel.setForeground(new Color(240, 240, 240));
        
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(180, 180, 180));
        
        // Progress bar with modern style
        progressBar = new JProgressBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Track
                g2d.setColor(new Color(60, 60, 70));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                
                // Progress
                if (getValue() > 0) {
                    int width = (int) (getWidth() * ((double) getValue() / getMaximum()));
                    GradientPaint progressGradient = new GradientPaint(
                        0, 0, new Color(100, 180, 255),
                        width, 0, new Color(0, 120, 255)
                    );
                    g2d.setPaint(progressGradient);
                    g2d.fillRoundRect(0, 0, width, getHeight(), 5, 5);
                    
                    // Glow effect
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.fillRoundRect(0, 0, width, getHeight() / 2, 5, 5);
                }
            }
        };
        progressBar.setPreferredSize(new Dimension(0, 8));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setStringPainted(false);
        progressBar.setVisible(false);
        
        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout(10, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(spinnerPanel, BorderLayout.CENTER);
        centerPanel.add(loadingLabel, BorderLayout.SOUTH);
        
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        add(panel);
        
        // Animation timers (same functionality)
        rotationTimer = new Timer(16, e -> {
            rotationAngle += 0.1;
            if (rotationAngle >= 2 * Math.PI) {
                rotationAngle = 0;
            }
            spinnerPanel.repaint();
        });
        
        timer = new Timer(500, e -> {
            dots = (dots + 1) % 4;
            StringBuilder loadingText = new StringBuilder(baseText);
            for (int i = 0; i < dots; i++) {
                loadingText.append(".");
            }
            loadingLabel.setText(loadingText.toString());
        });
    }

    // All remaining methods stay exactly the same as in your original code
    public void showWithFade() {
        setOpacity(0f);
        setVisible(true);
        
        Timer fadeIn = new Timer(20, new ActionListener() {
            float opacity = 0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1f) {
                    opacity += 0.05f;
                    setOpacity(opacity);
                } else {
                    ((Timer)e.getSource()).stop();
                    rotationTimer.start();
                    timer.start();
                }
            }
        });
        fadeIn.start();
    }

    public void hideWithFade() {
        Timer fadeOut = new Timer(20, new ActionListener() {
            float opacity = 1f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity > 0f) {
                    opacity -= 0.05f;
                    setOpacity(opacity);
                } else {
                    ((Timer)e.getSource()).stop();
                    rotationTimer.stop();
                    timer.stop();
                    dispose();
                }
            }
        });
        fadeOut.start();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void setProgress(int value) {
        progressBar.setVisible(true);
        progressBar.setValue(value);
    }

    public void setBaseText(String text) {
        this.baseText = text;
        loadingLabel.setText(text);
    }

    @Override
    public void setOpacity(float opacity) {
        try {
            super.setOpacity(opacity);
        } catch (Exception e) {
            // Fallback for systems that don't support opacity
        }
    }
}