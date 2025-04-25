/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.CustomerDAO;
import Model.UserGoogleDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

public class SaveChatServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        UserGoogleDto user = (UserGoogleDto) session.getAttribute("customer");

        if (user == null) {
            sendError(response, "Người dùng chưa đăng nhập");
            return;
        }

        // Đọc dữ liệu JSON từ request body
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        JSONObject json;
        String message;
        String sender;
        String chatSessionId;
        try {
            json = new JSONObject(sb.toString());
            message = json.getString("message");
            sender = json.getString("sender");
            chatSessionId = json.getString("chatSessionId");
        } catch (Exception e) {
            sendError(response, "Dữ liệu không hợp lệ: " + e.getMessage());
            return;
        }

        if (message == null || sender == null || chatSessionId == null) {
            sendError(response, "Thiếu thông tin tin nhắn, người gửi hoặc phiên chat");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        int userId = customerDAO.getUserIdByEmail(user.getEmail());
        if (userId == -1) {
            sendError(response, "Không tìm thấy user_id cho email: " + user.getEmail());
            return;
        }

        boolean success = customerDAO.saveChatMessage(userId, chatSessionId, message, sender);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            if (success) {
                out.write(gson.toJson(new Response(true, null)));
            } else {
                out.write(gson.toJson(new Response(false, "Không thể lưu tin nhắn")));
            }
        }
    }

    private void sendError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            out.write(gson.toJson(new Response(false, errorMessage)));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Save Chat Servlet";
    }

    private static class Response {
        boolean success;
        String error;

        Response(boolean success, String error) {
            this.success = success;
            this.error = error;
        }
    }
}