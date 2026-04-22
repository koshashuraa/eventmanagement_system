import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class EventManagement extends JFrame {
    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public EventManagement() {
        setTitle("Event Management System - Manage Events");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Toolbar
        mainPanel.add(createToolbar(), BorderLayout.CENTER);
        
        // Table Panel
        mainPanel.add(createTablePanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        loadEvents();
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Event Management");
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
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(new Color(240, 242, 245));
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 242, 245));
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JButton searchBtn = createButton("Search", new Color(52, 152, 219));
        searchBtn.addActionListener(e -> searchEvents());
        
        JButton resetBtn = createButton("Reset", new Color(149, 165, 166));
        resetBtn.addActionListener(e -> loadEvents());
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 242, 245));
        
        JButton addBtn = createButton("+ Add New Event", new Color(46, 204, 113));
        addBtn.addActionListener(e -> showEventDialog(null));
        
        buttonPanel.add(addBtn);
        
        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(buttonPanel, BorderLayout.EAST);
        
        return toolbar;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Create table
        String[] columns = {"ID", "Event Name", "Date", "Venue", "Max Slots", "Registered", "Remaining", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        eventTable = new JTable(tableModel);
        eventTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        eventTable.setRowHeight(30);
        eventTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        eventTable.getTableHeader().setBackground(new Color(52, 73, 94));
        eventTable.getTableHeader().setForeground(Color.WHITE);
        
        // Set column widths
        eventTable.getColumnModel().getColumn(0).setMaxWidth(50);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        eventTable.getColumnModel().getColumn(4).setMaxWidth(80);
        eventTable.getColumnModel().getColumn(5).setMaxWidth(80);
        eventTable.getColumnModel().getColumn(6).setMaxWidth(80);
        eventTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        
        // Add right-click menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Edit Event");
        JMenuItem deleteItem = new JMenuItem("Delete Event");
        JMenuItem viewItem = new JMenuItem("View Details");
        
        editItem.addActionListener(e -> editEvent());
        deleteItem.addActionListener(e -> deleteEvent());
        viewItem.addActionListener(e -> viewEventDetails());
        
        popupMenu.add(viewItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        
        eventTable.addMouseListener(new MouseAdapter() {
            @SuppressWarnings("override")
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = eventTable.rowAtPoint(e.getPoint());
                    eventTable.setRowSelectionInterval(row, row);
                    popupMenu.show(eventTable, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = eventTable.rowAtPoint(e.getPoint());
                    eventTable.setRowSelectionInterval(row, row);
                    popupMenu.show(eventTable, e.getX(), e.getY());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadEvents() {
        tableModel.setRowCount(0);
        List<Event> events = Event.getAll();
        
        for (Event event : events) {
            int registered = event.getRegisteredCount();
            int remaining = event.getRemainingSlots();
            String status = event.getStatus();
            
            // Color code status
            String statusDisplay = status;
            
            tableModel.addRow(new Object[]{
                event.getEventId(),
                event.getEventName(),
                event.getEventDate(),
                event.getVenue(),
                event.getMaxSlots(),
                registered,
                remaining,
                statusDisplay
            });
        }
        
        // Apply status colors
        eventTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) tableModel.getValueAt(row, 7);
                    if (null != status) switch (status) {
                        case "Open" -> c.setForeground(new Color(46, 204, 113));
                        case "Full" -> c.setForeground(new Color(241, 196, 15));
                        case "Concluded" -> c.setForeground(new Color(231, 76, 60));
                        default -> {
                        }
                    }
                }
                return c;
            }
        });
    }
    
    private void searchEvents() {
        String searchTerm = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        List<Event> events = Event.getAll();
        for (Event event : events) {
            if (event.getEventName().toLowerCase().contains(searchTerm) ||
                event.getVenue().toLowerCase().contains(searchTerm)) {
                tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getEventName(),
                    event.getEventDate(),
                    event.getVenue(),
                    event.getMaxSlots(),
                    event.getRegisteredCount(),
                    event.getRemainingSlots(),
                    event.getStatus()
                });
            }
        }
    }
    
    private void showEventDialog(Event eventToEdit) {
        JDialog dialog = new JDialog(this, eventToEdit == null ? "Add New Event" : "Edit Event", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Form fields
        JTextField nameField = new JTextField(20);
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        JTextField dateField = new JTextField();
        JTextField venueField = new JTextField(20);
        JTextField slotsField = new JTextField(10);
        
        if (eventToEdit != null) {
            nameField.setText(eventToEdit.getEventName());
            descArea.setText(eventToEdit.getDescription());
            dateField.setText(eventToEdit.getEventDate().toString());
            venueField.setText(eventToEdit.getVenue());
            slotsField.setText(String.valueOf(eventToEdit.getMaxSlots()));
        }
        
        // Labels and fields
        addFormField(panel, gbc, "Event Name:*", nameField, 0);
        addFormField(panel, gbc, "Description:", new JScrollPane(descArea), 1);
        addFormField(panel, gbc, "Event Date (YYYY-MM-DD):*", dateField, 2);
        addFormField(panel, gbc, "Venue:*", venueField, 3);
        addFormField(panel, gbc, "Maximum Slots:*", slotsField, 4);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveBtn = createButton("Save", new Color(46, 204, 113));
        JButton cancelBtn = createButton("Cancel", new Color(149, 165, 166));
        
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descArea.getText().trim();
                Date date = Date.valueOf(dateField.getText().trim());
                String venue = venueField.getText().trim();
                int maxSlots = Integer.parseInt(slotsField.getText().trim());
                
                // Validation
                if (name.isEmpty() || venue.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all required fields!");
                    return;
                }
                
                if (date.before(Date.valueOf(LocalDate.now())) && eventToEdit == null) {
                    JOptionPane.showMessageDialog(dialog, "Event date cannot be in the past!");
                    return;
                }
                
                if (maxSlots <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Maximum slots must be positive!");
                    return;
                }
                
                if (eventToEdit != null && maxSlots < eventToEdit.getRegisteredCount()) {
                    JOptionPane.showMessageDialog(dialog, "Cannot reduce slots below current registrations (" + eventToEdit.getRegisteredCount() + ")!");
                    return;
                }
                
                Event event = new Event(name, desc, date, venue, maxSlots);
                boolean success;
                
                if (eventToEdit == null) {
                    success = Event.create(event);
                } else {
                    event.setEventId(eventToEdit.getEventId());
                    success = Event.update(event);
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Event saved successfully!");
                    dialog.dispose();
                    loadEvents();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save event!");
                }
            } catch (HeadlessException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage());
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, Component field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel jlabel = new JLabel(label);
        jlabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(jlabel, gbc);
        
        gbc.gridx = 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gbc);
    }
    
    private void editEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to edit!");
            return;
        }
        
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        Event event = Event.getById(eventId);
        if (event != null) {
            showEventDialog(event);
        }
    }
    
    private void deleteEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deleting this event will also remove all registrations.\nAre you sure?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int eventId = (int) tableModel.getValueAt(selectedRow, 0);
            if (Event.delete(eventId)) {
                JOptionPane.showMessageDialog(this, "Event deleted successfully!");
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete event!");
            }
        }
    }
    
    private void viewEventDetails() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        String details = """
                         Event Details:
                         
                         Name: """ + tableModel.getValueAt(selectedRow, 1) + "\n" +
            "Date: " + tableModel.getValueAt(selectedRow, 2) + "\n" +
            "Venue: " + tableModel.getValueAt(selectedRow, 3) + "\n" +
            "Maximum Slots: " + tableModel.getValueAt(selectedRow, 4) + "\n" +
            "Registered: " + tableModel.getValueAt(selectedRow, 5) + "\n" +
            "Remaining Slots: " + tableModel.getValueAt(selectedRow, 6) + "\n" +
            "Status: " + tableModel.getValueAt(selectedRow, 7);
        
        JOptionPane.showMessageDialog(this, details, "Event Details", JOptionPane.INFORMATION_MESSAGE);
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
