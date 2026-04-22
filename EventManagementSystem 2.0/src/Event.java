import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private int eventId;
    private String eventName;
    private String description;
    private Date eventDate;
    private String venue;
    private int maxSlots;
    private Timestamp createdAt;
    
    public Event() {}
    
    public Event(String eventName, String description, Date eventDate, String venue, int maxSlots) {
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
        this.venue = venue;
        this.maxSlots = maxSlots;
    }
    
    // Getters and Setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    
    public int getMaxSlots() { return maxSlots; }
    public void setMaxSlots(int maxSlots) { this.maxSlots = maxSlots; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // ========== BUSINESS LOGIC METHODS ==========
    
    public int getRegisteredCount() {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, this.eventId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getRemainingSlots() {
        return maxSlots - getRegisteredCount();
    }
    
    public String getStatus() {
        int registered = getRegisteredCount();
        Date today = new Date(System.currentTimeMillis());
        
        if (eventDate.before(today)) {
            return "Concluded";
        } else if (registered >= maxSlots) {
            return "Full";
        } else {
            return "Open";
        }
    }
    
    // ========== STATISTICS METHODS FOR DASHBOARD ==========
    
    public static int getTotalEvents() {
        String sql = "SELECT COUNT(*) FROM events";
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
    
    public static int getFullEvents() {
        String sql = "SELECT COUNT(*) FROM events e WHERE e.max_slots <= (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id)";
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
    
    public static int getUpcomingEvents() {
        String sql = "SELECT COUNT(*) FROM events WHERE event_date >= CURDATE()";
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
    
    // ========== CRUD OPERATIONS ==========
    
    public static boolean create(Event event) {
        String sql = "INSERT INTO events (event_name, description, event_date, venue, max_slots) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, event.eventName);
            pstmt.setString(2, event.description);
            pstmt.setDate(3, event.eventDate);
            pstmt.setString(4, event.venue);
            pstmt.setInt(5, event.maxSlots);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Event> getAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date ASC";
        
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setEventName(rs.getString("event_name"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("event_date"));
                event.setVenue(rs.getString("venue"));
                event.setMaxSlots(rs.getInt("max_slots"));
                event.setCreatedAt(rs.getTimestamp("created_at"));
                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public static Event getById(int id) {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setEventName(rs.getString("event_name"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("event_date"));
                event.setVenue(rs.getString("venue"));
                event.setMaxSlots(rs.getInt("max_slots"));
                event.setCreatedAt(rs.getTimestamp("created_at"));
                return event;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean update(Event event) {
        boolean hasRegistrations = event.getRegisteredCount() > 0;
        String sql;
        
        if (hasRegistrations) {
            sql = "UPDATE events SET event_name = ?, description = ?, venue = ?, max_slots = ? WHERE event_id = ?";
        } else {
            sql = "UPDATE events SET event_name = ?, description = ?, event_date = ?, venue = ?, max_slots = ? WHERE event_id = ?";
        }
        
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            int index = 1;
            pstmt.setString(index++, event.eventName);
            pstmt.setString(index++, event.description);
            
            if (!hasRegistrations) {
                pstmt.setDate(index++, event.eventDate);
            }
            
            pstmt.setString(index++, event.venue);
            pstmt.setInt(index++, event.maxSlots);
            pstmt.setInt(index++, event.eventId);
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean delete(int eventId) {
        String deleteRegistrations = "DELETE FROM registrations WHERE event_id = ?";
        String deleteEvent = "DELETE FROM events WHERE event_id = ?";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteRegistrations);
                 PreparedStatement pstmt2 = conn.prepareStatement(deleteEvent)) {
                
                pstmt1.setInt(1, eventId);
                pstmt1.executeUpdate();
                
                pstmt2.setInt(1, eventId);
                pstmt2.executeUpdate();
                
                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}