import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class Dashboard extends JFrame {
    private final User currentUser;
    private JPanel contentPanel;
    private JLabel pageTitleLabel;
    private JButton currentActiveButton;
    private JLabel totalEventsLabel;
    private JLabel totalRegistrationsLabel;
    private JLabel fullEventsLabel;
    private JLabel upcomingEventsLabel;
    
    // Colors
    private final Color primaryColor = new Color(34, 139, 34);
    private final Color sidebarColor = new Color(28, 35, 48);
    private final Color lightGray = new Color(240, 242, 245);
    private final Color textColor = new Color(50, 50, 50);
    
    // CONSTRUCTOR - This is what Login.java is looking for
    public Dashboard(User user) {
        this.currentUser = user;
        initUI();
        loadDashboardData();
    }
    
    private void initUI() {
        setTitle("Event Management System - Dashboard");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(lightGray);
        
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);
        
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);
        
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(lightGray);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        contentPanel.add(createDashboardPanel(), "dashboard");
        contentPanel.add(createEventsPanel(), "events");
        contentPanel.add(createRegistrationsPanel(), "registrations");
        contentPanel.add(createAttendancePanel(), "attendance");
        contentPanel.add(createReportsPanel(), "reports");
        contentPanel.add(createSettingsPanel(), "settings");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        showPanel("dashboard");
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 750));
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BorderLayout());
        
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(sidebarColor);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));
        
        JLabel logoLabel = new JLabel("WELCOME");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(primaryColor);
        
        JLabel logoSubLabel = new JLabel("Event Management System");
        logoSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoSubLabel.setForeground(Color.GREEN);
        
        JPanel logoTextPanel = new JPanel(new BorderLayout());
        logoTextPanel.setOpaque(false);
        logoTextPanel.add(logoLabel, BorderLayout.NORTH);
        logoTextPanel.add(logoSubLabel, BorderLayout.SOUTH);
        
        logoPanel.add(logoTextPanel, BorderLayout.WEST);
        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(sidebarColor);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        String[][] menuItems = {
            {"Dashboard", "dashboard"},
            {"Events", "events"},
            {"Registrations", "registrations"},
            {"Attendance", "attendance"},
            {"Reports", "reports"},
            {"Settings", "settings"}
        };
        
        for (String[] item : menuItems) {
            JButton menuButton = createMenuButton(item[0], item[1], item[2]);
            menuPanel.add(menuButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(sidebarColor);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        JButton logoutButton = createMenuButton("🚪", "Logout", "logout");
        logoutButton.addActionListener(e -> logout());
        bottomPanel.add(logoutButton);
        
        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    private JButton createMenuButton(String icon, String text, String panelName) {
        JButton button = new JButton(icon + "  " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(sidebarColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != currentActiveButton) {
                    button.setBackground(new Color(50, 60, 80));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != currentActiveButton) {
                    button.setBackground(sidebarColor);
                }
            }
        });
        
        button.addActionListener(e -> {
            if (currentActiveButton != null) {
                currentActiveButton.setBackground(sidebarColor);
                currentActiveButton.setForeground(Color.WHITE);
            }
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE);
            currentActiveButton = button;
            
            if ("logout".equals(panelName)) {
                logout();
            } else {
                showPanel(panelName);
                if (pageTitleLabel != null) {
                    pageTitleLabel.setText(icon + " " + text.toUpperCase());
                }
            }
        });
        
        return button;
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        pageTitleLabel = new JLabel("DASHBOARD");
        pageTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitleLabel.setForeground(textColor);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel(" Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(Color.GRAY);
        
        JButton notificationBtn = new JButton("Notify");
        notificationBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        notificationBtn.setForeground(Color.GRAY);
        notificationBtn.setBackground(Color.WHITE);
        notificationBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationBtn.setFocusPainted(false);
        notificationBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        userPanel.add(notificationBtn);
        userPanel.add(welcomeLabel);
        
        topBar.add(pageTitleLabel, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightGray);
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(lightGray);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        totalEventsLabel = createStatCard(statsPanel, "Total Events", "0", new Color(52, 152, 219), "");
        totalRegistrationsLabel = createStatCard(statsPanel, "Registrations", "0", new Color(46, 204, 113), "");
        fullEventsLabel = createStatCard(statsPanel, "Full Events", "0", new Color(241, 196, 15), "");
        upcomingEventsLabel = createStatCard(statsPanel, "Upcoming", "0", new Color(155, 89, 182), "");
        
        JPanel chartsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        chartsPanel.setBackground(lightGray);
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        chartsPanel.add(createChartCard("Report Income Monthly", "$500.00", "↑ 12.5%", new Color(46, 204, 113)));
        chartsPanel.add(createChartCard("Report Expense Monthly", "$800.00", "↑ 5.2%", new Color(231, 76, 60)));
        chartsPanel.add(createChartCard("Profit Monthly", "$300.00", "↑ 8.3%", new Color(52, 152, 219)));
        
        JPanel tablePanel = createRecentEventsTable();
        
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(chartsPanel, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createStatCard(JPanel parent, String title, String value, Color color, String icon) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        parent.add(card);
        return valueLabel;
    }
    
    private JPanel createChartCard(String title, String value, String trend, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(textColor);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JLabel trendLabel = new JLabel(trend);
        trendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        trendLabel.setForeground(color);
        
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setOpaque(false);
        valuePanel.add(valueLabel, BorderLayout.WEST);
        valuePanel.add(trendLabel, BorderLayout.EAST);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRecentEventsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("Recent Events");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(textColor);
        panel.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"Event Name", "Date", "Venue", "Registered", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        
        List<Event> events = Event.getAll();
        for (Event event : events) {
            String statusIcon = "";
            String status = event.getStatus();
            if (null != status) switch (status) {
                case "Open" -> statusIcon = "";
                case "Full" -> statusIcon = "";
                case "Concluded" -> statusIcon = "";
                default -> {
                }
            }
            model.addRow(new Object[]{
                event.getEventName(),
                event.getEventDate(),
                event.getVenue(),
                event.getRegisteredCount(),
                statusIcon + status
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightGray);
        JLabel label = new JLabel("Events Management Panel", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(textColor);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightGray);
        JLabel label = new JLabel("Registrations Management Panel", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(textColor);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightGray);
        JLabel label = new JLabel("Attendance Management Panel", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(textColor);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightGray);
        JLabel label = new JLabel("Reports Panel", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(textColor);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightGray);
        JLabel label = new JLabel("Settings Panel", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(textColor);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
    }
    
    private void loadDashboardData() {
        try {
            int totalEvents = Event.getTotalEvents();
            int totalReg = Registration.getTotalRegistrations();
            int fullEvents = Event.getFullEvents();
            int upcomingEvents = Event.getUpcomingEvents();
            
            if (totalEventsLabel != null) {
                totalEventsLabel.setText(String.valueOf(totalEvents));
            }
            if (totalRegistrationsLabel != null) {
                totalRegistrationsLabel.setText(String.valueOf(totalReg));
            }
            if (fullEventsLabel != null) {
                fullEventsLabel.setText(String.valueOf(fullEvents));
            }
            if (upcomingEventsLabel != null) {
                upcomingEventsLabel.setText(String.valueOf(upcomingEvents));
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new Login().setVisible(true);
            dispose();
        }
    }
}