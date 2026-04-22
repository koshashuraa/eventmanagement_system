import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class Attendance extends JFrame {
    private JComboBox<String> eventCombo;
    private JTable attendeeTable;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;
    private int currentEventId = -1;
    
    public Attendance() {
        setTitle("Event Management System - Mark Attendance");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createMainPanel(), BorderLayout.CENTER);
        
        add(mainPanel);
        loadEventsToCombo();
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Attendance Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JButton backBtn = createButton("← Back to Dashboard", new Color(52, 73, 94));
        backBtn.addActionListener(e -> {
            new Dashboard(new User()).setVisible(true);
            dispose();
        });
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with event selection
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));
        
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.setBackground(new Color(240, 242, 245));
        
        selectPanel.add(new JLabel("Select Event:"));
        
        eventCombo = new JComboBox<>();
        eventCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eventCombo.setPreferredSize(new Dimension(350, 35));
        eventCombo.addActionListener(e -> loadAttendees());
        
        JButton refreshBtn = createButton("Refresh", new Color(52, 152, 219));
        refreshBtn.addActionListener(e -> {
            loadEventsToCombo();
            loadAttendees();
        });
        
        selectPanel.add(eventCombo);
        selectPanel.add(refreshBtn);
        
        summaryLabel = new JLabel("");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        summaryLabel.setForeground(new Color(46, 204, 113));
        
        topPanel.add(selectPanel, BorderLayout.WEST);
        topPanel.add(summaryLabel, BorderLayout.EAST);
        
        // Table
        JPanel tablePanel = createTablePanel();
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        String[] columns = {"ID", "Full Name", "Email", "Contact Number", "Attendance Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
        };
        
        attendeeTable = new JTable(tableModel);
        attendeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        attendeeTable.setRowHeight(40);
        attendeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        attendeeTable.getTableHeader().setBackground(new Color(52, 73, 94));
        attendeeTable.getTableHeader().setForeground(Color.WHITE);
        
        // Set column widths
        attendeeTable.getColumnModel().getColumn(0).setMaxWidth(50);
        attendeeTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        attendeeTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        attendeeTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        attendeeTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        attendeeTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        // Status renderer
        attendeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 4) {
                    String status = (String) value;
                    if (null == status) {
                        c.setForeground(new Color(241, 196, 15));
                    } else switch (status) {
                        case "Present" -> c.setForeground(new Color(46, 204, 113));
                        case "Absent" -> c.setForeground(new Color(231, 76, 60));
                        default -> c.setForeground(new Color(241, 196, 15));
                    }
                }
                return c;
            }
        });
        
        // Add button editor/renderer for action column
        attendeeTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        attendeeTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(attendeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadEventsToCombo() {
        eventCombo.removeAllItems();
        List<Event> events = Event.getAll();
        
        for (Event event : events) {
            eventCombo.addItem(event.getEventId() + " - " + event.getEventName() + " (" + event.getEventDate() + ")");
        }
        
        if (eventCombo.getItemCount() > 0) {
            loadAttendees();
        }
    }
    
    private void loadAttendees() {
        if (eventCombo.getSelectedIndex() == -1) return;
        
        String selected = (String) eventCombo.getSelectedItem();
        currentEventId = Integer.parseInt(selected.split(" - ")[0]);
        
        // Update summary
        int[] summary = RegistrationManagement.getAttendanceSummary(currentEventId);
        Event event = Event.getById(currentEventId);
        int total = event != null ? event.getRegisteredCount() : 0;
        summaryLabel.setText(String.format("Total: %d | Present: %d | Absent: %d | Pending: %d", 
            total, summary[0], summary[1], summary[2]));
        
        // Load attendees
        tableModel.setRowCount(0);
        List<RegistrationManagement> registrations = RegistrationManagement.getByEventId(currentEventId);
        
        for (RegistrationManagement reg : registrations) {
            tableModel.addRow(new Object[]{
                reg.getRegistrationId(),
                reg.getFullName(),
                reg.getEmail(),
                reg.getContactNumber(),
                reg.getAttendanceStatus(),
                "Mark Attendance"
            });
        }
    }
    
    private void markAttendance(int registrationId, String status) {
        if (RegistrationManagement.updateAttendance(registrationId, status)) {
            loadAttendees(); // Refresh the table
            JOptionPane.showMessageDialog(this, "Attendance marked as " + status + "!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update attendance!");
        }
    }
    
    // Button Renderer for action column
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton button;
        
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
            button = new JButton("Mark");
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            add(button);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }
    
    // Button Editor for action column
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton presentBtn, absentBtn, pendingBtn;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            setupButtons();
        }
        
        private void setupButtons() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            
            presentBtn = new JButton("Present");
            presentBtn.setBackground(new Color(46, 204, 113));
            presentBtn.setForeground(Color.WHITE);
            presentBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            presentBtn.setFocusPainted(false);
            presentBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            presentBtn.addActionListener(e -> {
                int regId = (int) tableModel.getValueAt(currentRow, 0);
                markAttendance(regId, "Present");
                fireEditingStopped();
            });
            
            absentBtn = new JButton("Absent");
            absentBtn.setBackground(new Color(231, 76, 60));
            absentBtn.setForeground(Color.WHITE);
            absentBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            absentBtn.setFocusPainted(false);
            absentBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            absentBtn.addActionListener(e -> {
                int regId = (int) tableModel.getValueAt(currentRow, 0);
                markAttendance(regId, "Absent");
                fireEditingStopped();
            });
            
            pendingBtn = new JButton("Pending");
            pendingBtn.setBackground(new Color(241, 196, 15));
            pendingBtn.setForeground(Color.WHITE);
            pendingBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            pendingBtn.setFocusPainted(false);
            pendingBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            pendingBtn.addActionListener(e -> {
                int regId = (int) tableModel.getValueAt(currentRow, 0);
                markAttendance(regId, "Pending");
                fireEditingStopped();
            });
            
            panel.add(presentBtn);
            panel.add(absentBtn);
            panel.add(pendingBtn);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}