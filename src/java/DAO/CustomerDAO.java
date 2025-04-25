/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import Model.UserGoogleDto;

public class CustomerDAO {

    public boolean saveUser(UserGoogleDto user) {
        String query = "INSERT INTO users (username, email, family_name, given_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return false;
            }

            System.out.println("Đang thực hiện INSERT cho user: " + user.getEmail());

            pstmt.setString(1, user.getName() != null ? user.getName() : "");
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                System.out.println("Lỗi: Email từ Google là null hoặc rỗng!");
                return false;
            }
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFamily_name() != null ? user.getFamily_name() : "");
            pstmt.setString(4, user.getGiven_name() != null ? user.getGiven_name() : "");

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("INSERT thành công, số dòng ảnh hưởng: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException | NullPointerException e) {
            System.out.println("Lỗi khi lưu user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUserExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return false;
            }

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Kiểm tra user " + email + " - Tồn tại: " + (count > 0));
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi SQL khi kiểm tra user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public int getUserIdByEmail(String email) {
        String query = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return -1;
            }

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi SQL khi lấy user_id: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public boolean saveChatMessage(int userId, String chatSessionId, String message, String sender) {
        String query = "INSERT INTO chat_history (user_id, chat_session_id, message, sender, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return false;
            }

            pstmt.setInt(1, userId);
            pstmt.setString(2, chatSessionId);
            pstmt.setString(3, message);
            pstmt.setString(4, sender);
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Lưu tin nhắn cho user_id " + userId + " (" + sender + "): " + rowsAffected + " dòng ảnh hưởng");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi SQL khi lưu tin nhắn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<UserGoogleDto> getAllUsers() {
        List<UserGoogleDto> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return users;
            }

            while (rs.next()) {
                UserGoogleDto user = new UserGoogleDto();
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("username"));
                user.setFamily_name(rs.getString("family_name"));
                user.setGiven_name(rs.getString("given_name"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi SQL khi lấy danh sách người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // Phương thức cho admin.jsp: lấy toàn bộ lịch sử trò chuyện của một user
    public List<ChatMessage> getChatHistory(int userId) {
        List<ChatMessage> messages = new ArrayList<>();
        String query = "SELECT chat_id, user_id, chat_session_id, message, sender, timestamp FROM chat_history WHERE user_id = ? ORDER BY timestamp ASC";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return messages;
            }

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChatMessage msg = new ChatMessage();
                    msg.setId(rs.getLong("chat_id"));
                    msg.setUserId(rs.getInt("user_id"));
                    msg.setChatSessionId(rs.getString("chat_session_id"));
                    msg.setMessage(rs.getString("message"));
                    msg.setSender(rs.getString("sender"));
                    msg.setCreatedAt(rs.getTimestamp("timestamp"));
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi SQL khi lấy lịch sử chat: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    // Phương thức mới cho chat.jsp: lấy lịch sử trò chuyện theo userId và sessionId
    public List<ChatMessage> getChatHistory(int userId, String sessionId) {
        List<ChatMessage> messages = new ArrayList<>();
        String query = "SELECT chat_session_id, message, sender, timestamp FROM chat_history WHERE user_id = ?";
        if (sessionId != null) {
            query += " AND chat_session_id = ?";
        }
        query += " ORDER BY timestamp ASC";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return messages;
            }

            pstmt.setInt(1, userId);
            if (sessionId != null) {
                pstmt.setString(2, sessionId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChatMessage msg = new ChatMessage();
                    msg.setChatSessionId(rs.getString("chat_session_id"));
                    msg.setMessage(rs.getString("message"));
                    msg.setSender(rs.getString("sender"));
                    msg.setCreatedAt(rs.getTimestamp("timestamp"));
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi SQL khi lấy lịch sử chat: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    public boolean deleteChatHistory(int userId) {
        String query = "DELETE FROM chat_history WHERE user_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (conn == null) {
                System.out.println("Lỗi: Không thể kết nối đến database!");
                return false;
            }

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Xóa lịch sử chat cho user_id " + userId + ": " + rowsAffected + " dòng bị xóa");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi SQL khi xóa lịch sử chat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static class ChatMessage {
        private long id;
        private int userId;
        private String chatSessionId;
        private String message;
        private String sender;
        private Timestamp createdAt;

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getChatSessionId() { return chatSessionId; }
        public void setChatSessionId(String chatSessionId) { this.chatSessionId = chatSessionId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }
}