import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationManagement {
    private int registrationId;
    private int eventId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String attendanceStatus;
    private Timestamp registeredAt;
    
    public RegistrationManagement() {}
    
    public RegistrationManagement(int eventId, String firstName, String lastName, String email, String contactNumber) {
        this.eventId = eventId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.attendanceStatus = "Pending";
    }
    
    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }
    
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    
    public String getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
    
    public Timestamp getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Timestamp registeredAt) { this.registeredAt = registeredAt; }
    
    // Check if event has available slots
    public static boolean hasAvailableSlots(int eventId) {
        Event event = Event.getById(eventId);
        if (event == null) return false;
        return event.getRemainingSlots() > 0;
    }
    
    // Check for duplicate registration
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean isDuplicateRegistration(int eventId, String email) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND email = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Validate email format
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    // Validate contact number (exactly 11 digits)
    public static boolean isValidContactNumber(String contact) {
        return contact.matches("\\d{11}");
    }
    
    // CRUD Operations
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean create(RegistrationManagement registration) {
        // Check slot availability
        if (!hasAvailableSlots(registration.eventId)) {
            return false;
        }
        
        // Check for duplicate
        if (isDuplicateRegistration(registration.eventId, registration.email)) {
            return false;
        }
        
        String sql = "INSERT INTO registrations (event_id, first_name, last_name, email, contact_number, attendance_status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, registration.eventId);
            pstmt.setString(2, registration.firstName);
            pstmt.setString(3, registration.lastName);
            pstmt.setString(4, registration.email);
            pstmt.setString(5, registration.contactNumber);
            pstmt.setString(6, registration.attendanceStatus);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static List<RegistrationManagement> getByEventId(int eventId) {
        List<RegistrationManagement> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE event_id = ? ORDER BY registered_at DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RegistrationManagement reg = new RegistrationManagement();
                reg.setRegistrationId(rs.getInt("registration_id"));
                reg.setEventId(rs.getInt("event_id"));
                reg.setFirstName(rs.getString("first_name"));
                reg.setLastName(rs.getString("last_name"));
                reg.setEmail(rs.getString("email"));
                reg.setContactNumber(rs.getString("contact_number"));
                reg.setAttendanceStatus(rs.getString("attendance_status"));
                reg.setRegisteredAt(rs.getTimestamp("registered_at"));
                registrations.add(reg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registrations;
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean update(RegistrationManagement registration) {
        String sql = "UPDATE registrations SET first_name = ?, last_name = ?, email = ?, contact_number = ? WHERE registration_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, registration.firstName);
            pstmt.setString(2, registration.lastName);
            pstmt.setString(3, registration.email);
            pstmt.setString(4, registration.contactNumber);
            pstmt.setInt(5, registration.registrationId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean delete(int registrationId) {
        String sql = "DELETE FROM registrations WHERE registration_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean updateAttendance(int registrationId, String status) {
        String sql = "UPDATE registrations SET attendance_status = ? WHERE registration_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, registrationId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get total registrations across all events
    @SuppressWarnings("CallToPrintStackTrace")
    public static int getTotalRegistrations() {
        String sql = "SELECT COUNT(*) FROM registrations";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get attendance summary for an event
    @SuppressWarnings("CallToPrintStackTrace")
    public static int[] getAttendanceSummary(int eventId) {
        int[] summary = new int[3]; // [present, absent, pending]
        String sql = "SELECT attendance_status, COUNT(*) FROM registrations WHERE event_id = ? GROUP BY attendance_status";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String status = rs.getString(1);
                int count = rs.getInt(2);
                
                switch (status) {
                    case "Present" -> summary[0] = count;
                    case "Absent" -> summary[1] = count;
                    case "Pending" -> summary[2] = count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }

    public void setVisible1(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setVisible'");
    }

    @SuppressWarnings("unused")
    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}