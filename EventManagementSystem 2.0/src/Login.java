import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Login extends JFrame {
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JButton signInButton;
    private JLabel messageLabel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLayeredPane layeredPane;
    private JPanel loadingPanel;
    private Timer spinTimer;
    private int spinnerAngle = 0;
    
    // Green theme colors
    private final Color primaryGreen = new Color(34, 139, 34);
    private final Color darkGreen = new Color(0, 100, 0);
    
    public Login() {
        initUI();
        checkDatabaseConnection();
    }
    
    @SuppressWarnings("UseSpecificCatch")
    private void initUI() {
        setTitle("Event Management System");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 650, 20, 20));
        
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1100, 650));
        layeredPane.setLayout(null);
        
        mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBounds(0, 0, 1100, 650);
        
        // ========== LEFT PANEL - SOLID GREEN BACKGROUND ==========
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(primaryGreen);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));
        
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.gridwidth = GridBagConstraints.REMAINDER;
        leftGbc.anchor = GridBagConstraints.CENTER;
        
        // ========== LOGO SECTION ==========
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        logoPanel.setOpaque(false);
        
        JLabel leftLogoLabel = new JLabel();
        leftLogoLabel.setPreferredSize(new Dimension(130, 130));
        leftLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            File leftLogoFile = new File("gordon_logo.png");
            if (leftLogoFile.exists()) {
                ImageIcon leftIcon = new ImageIcon(ImageIO.read(leftLogoFile));
                Image scaledLeft = leftIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                leftLogoLabel.setIcon(new ImageIcon(scaledLeft));
            } else {
                leftLogoLabel.setText("GC");
                leftLogoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
                leftLogoLabel.setForeground(Color.WHITE);
                leftLogoLabel.setBackground(new Color(0, 0, 0, 60));
                leftLogoLabel.setOpaque(true);
                leftLogoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                leftLogoLabel.setPreferredSize(new Dimension(120, 120));
            }
        } catch (Exception e) {
            leftLogoLabel.setText("GC");
            leftLogoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            leftLogoLabel.setForeground(Color.WHITE);
            leftLogoLabel.setBackground(new Color(0, 0, 0, 60));
            leftLogoLabel.setOpaque(true);
            leftLogoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            leftLogoLabel.setPreferredSize(new Dimension(120, 120));
        }
        
        JLabel rightLogoLabel = new JLabel();
        rightLogoLabel.setPreferredSize(new Dimension(130, 130));
        rightLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            File rightLogoFile = new File("college_logo.png");
            if (rightLogoFile.exists()) {
                ImageIcon rightIcon = new ImageIcon(ImageIO.read(rightLogoFile));
                Image scaledRight = rightIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                rightLogoLabel.setIcon(new ImageIcon(scaledRight));
            } else {
                rightLogoLabel.setText("EMS");
                rightLogoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
                rightLogoLabel.setForeground(Color.WHITE);
                rightLogoLabel.setBackground(new Color(0, 0, 0, 60));
                rightLogoLabel.setOpaque(true);
                rightLogoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                rightLogoLabel.setPreferredSize(new Dimension(120, 120));
            }
        } catch (Exception e) {
            rightLogoLabel.setText("EMS");
            rightLogoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            rightLogoLabel.setForeground(Color.WHITE);
            rightLogoLabel.setBackground(new Color(0, 0, 0, 60));
            rightLogoLabel.setOpaque(true);
            rightLogoLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            rightLogoLabel.setPreferredSize(new Dimension(120, 120));
        }
        
        logoPanel.add(leftLogoLabel);
        logoPanel.add(rightLogoLabel);
        
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.insets = new Insets(10, 20, 20, 20);
        leftPanel.add(logoPanel, leftGbc);
        
        // ========== TITLE - EVENT MANAGEMENT & ATTENDANCE SYSTEM (FULL TEXT) ==========
        JLabel titleLine1 = new JLabel("EVENT MANAGEMENT");
        titleLine1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLine1.setForeground(Color.WHITE);
        titleLine1.setForeground(new Color(255, 215, 0));
        titleLine1.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(10, 20, 5, 20);
        leftPanel.add(titleLine1, leftGbc);
        
        JLabel titleLine2 = new JLabel("&");
        titleLine2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLine2.setForeground(new Color(255, 215, 0)); // Gold
        titleLine2.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 2;
        leftGbc.insets = new Insets(0, 20, 5, 20);
        leftPanel.add(titleLine2, leftGbc);
        
        JLabel titleLine3 = new JLabel("ATTENDANCE SYSTEM");
        titleLine3.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLine3.setForeground(new Color(255, 215, 0)); // Gold
        titleLine3.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 3;
        leftGbc.insets = new Insets(0, 20, 20, 20);
        leftPanel.add(titleLine3, leftGbc);
        
        // ========== DESCRIPTION ==========
        JLabel descLabel = new JLabel("<html><center>Manage your events with ease<br>and attendance tracking system.</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(255, 255, 255, 240));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 4;
        leftGbc.insets = new Insets(0, 20, 40, 20);
        leftPanel.add(descLabel, leftGbc);
        
        // ========== WELCOME BACK ==========
        JLabel welcomeLabel = new JLabel("WELCOME BACK!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftGbc.gridy = 5;
        leftGbc.insets = new Insets(20, 20, 20, 20);
        leftPanel.add(welcomeLabel, leftGbc);
        
        // ========== RIGHT PANEL - FORM SECTION ==========
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        
        JPanel closePanel = new JPanel(new BorderLayout());
        closePanel.setOpaque(false);
        closePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 0));
        
        JButton exitBtn = new JButton("X");
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exitBtn.setForeground(Color.GRAY);
        exitBtn.setBackground(new Color(0, 0, 0, 0));
        exitBtn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        exitBtn.setFocusPainted(false);
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitBtn.setContentAreaFilled(false);
        exitBtn.setOpaque(true);
        exitBtn.addActionListener(e -> System.exit(0));
        
        exitBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitBtn.setForeground(new Color(231, 76, 60));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                exitBtn.setForeground(Color.GRAY);
            }
        });
        
        closePanel.add(exitBtn, BorderLayout.EAST);
        rightPanel.add(closePanel, BorderLayout.NORTH);
        
        cardPanel = new JPanel(cardLayout = new CardLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        
        JPanel signInForm = createSignInForm();
        cardPanel.add(signInForm, "signin");
        
        JPanel signUpForm = createSignUpForm();
        cardPanel.add(signUpForm, "signup");
        
        rightPanel.add(cardPanel, BorderLayout.CENTER);
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        createLoadingPanel();
        
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(loadingPanel, JLayeredPane.PALETTE_LAYER);
        loadingPanel.setVisible(false);
        
        add(layeredPane);
        
        cardLayout.show(cardPanel, "signin");
        getRootPane().setDefaultButton(signInButton);
    }
    
    private void createLoadingPanel() {
        loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setBackground(new Color(0, 0, 0, 200));
        loadingPanel.setBounds(0, 0, 1100, 650);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JPanel spinnerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = 25;
                
                g2d.setStroke(new BasicStroke(4));
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, spinnerAngle, 300);
                
                double rad = Math.toRadians(spinnerAngle);
                int x = (int)(centerX + radius * Math.cos(rad));
                int y = (int)(centerY + radius * Math.sin(rad));
                g2d.fillOval(x - 4, y - 4, 8, 8);
            }
        };
        spinnerPanel.setOpaque(false);
        spinnerPanel.setPreferredSize(new Dimension(80, 80));
        
        JLabel loadingLabel = new JLabel("LOGGING IN...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loadingLabel.setForeground(Color.WHITE);
        
        JLabel successLabel = new JLabel("");
        successLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        successLabel.setForeground(new Color(46, 204, 113));
        
        gbc.gridy = 0;
        loadingPanel.add(spinnerPanel, gbc);
        gbc.gridy = 1;
        loadingPanel.add(loadingLabel, gbc);
        gbc.gridy = 2;
        loadingPanel.add(successLabel, gbc);
        
        loadingPanel.putClientProperty("spinnerPanel", spinnerPanel);
        loadingPanel.putClientProperty("loadingLabel", loadingLabel);
        loadingPanel.putClientProperty("successLabel", successLabel);
    }
    
    private void startSpinnerAnimation() {
        if (spinTimer != null) {
            spinTimer.stop();
        }
        spinTimer = new Timer(20, e -> {
            spinnerAngle = (spinnerAngle + 10) % 360;
            JPanel spinnerPanel = (JPanel) loadingPanel.getClientProperty("spinnerPanel");
            if (spinnerPanel != null) {
                spinnerPanel.repaint();
            }
        });
        spinTimer.start();
    }
    
    private void stopSpinnerAnimation() {
        if (spinTimer != null) {
            spinTimer.stop();
            spinTimer = null;
        }
    }
    
    private void showLoginSuccessAnimation(Runnable onComplete) {
        loadingPanel.setVisible(true);
        
        JLabel loadingLabel = (JLabel) loadingPanel.getClientProperty("loadingLabel");
        JLabel successLabel = (JLabel) loadingPanel.getClientProperty("successLabel");
        
        startSpinnerAnimation();
        
        Timer dotsTimer = new Timer(500, new ActionListener() {
            int dotCount = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                dotCount = (dotCount + 1) % 4;
                String dots = "";
                for (int i = 0; i < dotCount; i++) {
                    dots = dots + ".";
                }
                loadingLabel.setText("LOGGING IN" + dots);
            }
        });
        dotsTimer.start();
        
        Timer fadeTimer = new Timer(20, new ActionListener() {
            float alpha = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1) {
                    alpha = 1;
                    ((Timer)e.getSource()).stop();
                    
                    successLabel.setText("LOGIN SUCCESSFUL!");
                    loadingLabel.setForeground(new Color(46, 204, 113));
                    
                    Timer successTimer = new Timer(1000, ev -> {
                        dotsTimer.stop();
                        stopSpinnerAnimation();
                        loadingPanel.setVisible(false);
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
                    successTimer.setRepeats(false);
                    successTimer.start();
                }
                loadingPanel.setBackground(new Color(0, 0, 0, (int)(alpha * 200)));
            }
        });
        fadeTimer.start();
    }
    
    private JPanel createSignInForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        JLabel signInTitle = new JLabel("SIGN IN");
        signInTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        signInTitle.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(signInTitle, gbc);
        
        JLabel emailLabel = new JLabel("EMAIL");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(80, 80, 80));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(emailLabel, gbc);
        
        loginEmailField = new JTextField();
        loginEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginEmailField.setForeground(Color.BLACK);
        loginEmailField.setBackground(new Color(250, 250, 250));
        loginEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(loginEmailField, gbc);
        
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(80, 80, 80));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(passwordLabel, gbc);
        
        loginPasswordField = new JPasswordField();
        loginPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginPasswordField.setForeground(Color.BLACK);
        loginPasswordField.setBackground(new Color(250, 250, 250));
        loginPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(loginPasswordField, gbc);
        
        JLabel forgotLabel = new JLabel("FORGOT PASSWORD?");
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotLabel.setForeground(primaryGreen);
        forgotLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, 
                    "Please contact administrator to reset your password.", 
                    "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotLabel.setForeground(darkGreen);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                forgotLabel.setForeground(primaryGreen);
            }
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel.add(forgotLabel, gbc);
        
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(231, 76, 60));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(messageLabel, gbc);
        
        signInButton = new JButton("SIGN IN");
        signInButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signInButton.setForeground(Color.BLACK);
        signInButton.setBackground(primaryGreen);
        signInButton.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        signInButton.setFocusPainted(false);
        signInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInButton.setOpaque(true);
        signInButton.addActionListener(e -> attemptLogin());
        
        signInButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signInButton.setBackground(darkGreen);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signInButton.setBackground(primaryGreen);
            }
        });
        
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(signInButton, gbc);
        
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setBackground(Color.WHITE);
        
        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerLabel.setForeground(new Color(100, 100, 100));
        
        JLabel registerLink = new JLabel("REGISTER NOW");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(primaryGreen);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "signup");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setForeground(darkGreen);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setForeground(primaryGreen);
            }
        });
        
        registerPanel.add(registerLabel);
        registerPanel.add(registerLink);
        
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(registerPanel, gbc);
        
        return panel;
    }
    
    private JPanel createSignUpForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        JLabel registerTitle = new JLabel("REGISTER");
        registerTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        registerTitle.setForeground(new Color(30, 30, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(registerTitle, gbc);
        
        JLabel nameLabel = new JLabel("FULL NAME");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(80, 80, 80));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(nameLabel, gbc);
        
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setForeground(Color.BLACK);
        nameField.setBackground(new Color(250, 250, 250));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(nameField, gbc);
        
        JLabel emailLabel = new JLabel("EMAIL");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(80, 80, 80));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(emailLabel, gbc);
        
        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setForeground(Color.BLACK);
        emailField.setBackground(new Color(250, 250, 250));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(emailField, gbc);
        
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(80, 80, 80));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(passwordLabel, gbc);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(new Color(250, 250, 250));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel.add(passwordField, gbc);
        
        JButton signUpButton = new JButton("REGISTER");
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setBackground(primaryGreen);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpButton.setOpaque(true);
        signUpButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (password.length() < 6) {
                JOptionPane.showMessageDialog(panel, "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, 
                    "Account created successfully!\nPlease login with your credentials.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "signin");
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
            }
        });
        
        signUpButton.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("override")
            public void mouseEntered(MouseEvent e) {
                signUpButton.setBackground(darkGreen);
            }
            @SuppressWarnings("override")
            public void mouseExited(MouseEvent e) {
                signUpButton.setBackground(primaryGreen);
            }
        });
        
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 20, 0);
        panel.add(signUpButton, gbc);
        
        JPanel signInPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signInPanel.setBackground(Color.WHITE);
        
        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        haveAccountLabel.setForeground(new Color(100, 100, 100));
        
        JLabel signInLink = new JLabel("SIGN IN");
        signInLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signInLink.setForeground(primaryGreen);
        signInLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "signin");
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                signInLink.setForeground(darkGreen);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signInLink.setForeground(primaryGreen);
            }
        });
        
        signInPanel.add(haveAccountLabel);
        signInPanel.add(signInLink);
        
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(signInPanel, gbc);
        
        return panel;
    }
    
    private void checkDatabaseConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return DatabaseConnection.testConnection();
            }
            
            @SuppressWarnings("UseSpecificCatch")
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (!connected) {
                        int retry = JOptionPane.showConfirmDialog(Login.this, 
                            "Cannot connect to database!\nPlease start XAMPP MySQL.\n\nRetry?", 
                            "Connection Error", 
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                        if (retry == JOptionPane.YES_OPTION) {
                            checkDatabaseConnection();
                        } else {
                            System.exit(0);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Login.this, 
                        "Database connection error!\nPlease check XAMPP MySQL.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void attemptLogin() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword()).trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter email and password");
            animateShake(mainPanel);
            return;
        }
        
        String username = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
        
        signInButton.setEnabled(false);
        signInButton.setText("SIGNING IN...");
        
        showLoginSuccessAnimation(() -> {
            SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
                @Override
                protected User doInBackground() {
                    return User.login(username, password);
                }
                
                @SuppressWarnings("override")
                protected void done() {
                    try {
                        User user = get();
                        if (user != null) {
                            messageLabel.setText("");
                            new Dashboard(user).setVisible(true);
                            dispose();
                        } else {
                            messageLabel.setText("Invalid email or password");
                            animateShake(mainPanel);
                            loginPasswordField.setText("");
                            signInButton.setEnabled(true);
                            signInButton.setText("SIGN IN");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        messageLabel.setText("Login error: " + e.getMessage());
                        signInButton.setEnabled(true);
                        signInButton.setText("SIGN IN");
                    }
                }
            };
            worker.execute();
        });
    }
    
    private void animateShake(JComponent component) {
        final int[] xOffset = {0, -6, 6, -4, 4, -2, 2, 0};
        final int originalX = component.getX();
        
        Timer timer = new Timer(30, new ActionListener() {
            int index = 0;
            @SuppressWarnings("override")
            public void actionPerformed(ActionEvent e) {
                if (index < xOffset.length) {
                    component.setLocation(originalX + xOffset[index], component.getY());
                    index++;
                } else {
                    component.setLocation(originalX, component.getY());
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
    
}