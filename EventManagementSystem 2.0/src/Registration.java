import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Registration {
    private int registrationId;
    private int eventId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String attendanceStatus;
    private Timestamp registeredAt;
    
    public Registration() {}
    
    public Registration(int eventId, String firstName, String lastName, String email, String contactNumber) {
        this.eventId = eventId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.attendanceStatus = "Pending";
    }
    
    // ========== GETTERS AND SETTERS ==========
    
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
    
    // ========== VALIDATION METHODS ==========
    
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    public static boolean isValidContactNumber(String contact) {
        return contact.matches("\\d{11}");
    }
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    public static boolean hasAvailableSlots(int eventId) {
        Event event = Event.getById(eventId);
        if (event == null) return false;
        return event.getRemainingSlots() > 0;
    }
    
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
    
    // ========== CRUD OPERATIONS ==========
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean create(Registration registration) {
        if (!hasAvailableSlots(registration.eventId)) {
            return false;
        }
        
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
    public static List<Registration> getByEventId(int eventId) {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE event_id = ? ORDER BY registered_at DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Registration reg = new Registration();
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
    public static List<Registration> getAllRegistrations() {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations ORDER BY registered_at DESC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Registration reg = new Registration();
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
    
    public static Registration getById(int registrationId) {
        String sql = "SELECT * FROM registrations WHERE registration_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Registration reg = new Registration();
                reg.setRegistrationId(rs.getInt("registration_id"));
                reg.setEventId(rs.getInt("event_id"));
                reg.setFirstName(rs.getString("first_name"));
                reg.setLastName(rs.getString("last_name"));
                reg.setEmail(rs.getString("email"));
                reg.setContactNumber(rs.getString("contact_number"));
                reg.setAttendanceStatus(rs.getString("attendance_status"));
                reg.setRegisteredAt(rs.getTimestamp("registered_at"));
                return reg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean update(Registration registration) {
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
    
    // ========== ATTENDANCE METHODS ==========
    
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
    
    public static int[] getAttendanceSummary(int eventId) {
        int[] summary = new int[3]; // [present, absent, pending]
        String sql = "SELECT attendance_status, COUNT(*) FROM registrations WHERE event_id = ? GROUP BY attendance_status";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String status = rs.getString(1);
                int count = rs.getInt(2);
                
                if ("Present".equals(status)) {
                    summary[0] = count;
                } else if ("Absent".equals(status)) {
                    summary[1] = count;
                } else if ("Pending".equals(status)) {
                    summary[2] = count;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }
    
    public static int getPresentCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND attendance_status = 'Present'";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getAbsentCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND attendance_status = 'Absent'";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getPendingCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND attendance_status = 'Pending'";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ========== STATISTICS METHODS ==========
    
    public static int getTotalRegistrations() {
        String sql = "SELECT COUNT(*) FROM registrations";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getRegistrationsByEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getTodayRegistrations() {
        String sql = "SELECT COUNT(*) FROM registrations WHERE DATE(registered_at) = CURDATE()";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getWeekRegistrations() {
        String sql = "SELECT COUNT(*) FROM registrations WHERE YEARWEEK(registered_at) = YEARWEEK(CURDATE())";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getMonthRegistrations() {
        String sql = "SELECT COUNT(*) FROM registrations WHERE MONTH(registered_at) = MONTH(CURDATE()) AND YEAR(registered_at) = YEAR(CURDATE())";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ========== SEARCH METHODS ==========
    
    public static List<Registration> searchByEmail(String email) {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE email LIKE ? ORDER BY registered_at DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + email + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Registration reg = new Registration();
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
    
    public static List<Registration> searchByName(String name) {
        List<Registration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE first_name LIKE ? OR last_name LIKE ? ORDER BY registered_at DESC";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            pstmt.setString(2, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Registration reg = new Registration();
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
    
    // ========== DELETE METHODS ==========
    
    public static boolean deleteByEventId(int eventId) {
        String sql = "DELETE FROM registrations WHERE event_id = ?";
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteAllRegistrations() {
        String sql = "DELETE FROM registrations";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}