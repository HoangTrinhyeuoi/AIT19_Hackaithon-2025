/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;


/**
 *
 * @author ASUS
 */

import DAO.CustomerDAO;
import Model.UserGoogleDto;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;


public class LoadChatSessionsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        UserGoogleDto user = (UserGoogleDto) session.getAttribute("customer");

        if (user == null) {
            out.write("{\"error\":\"User not logged in\"}");
            return;
        }

        CustomerDAO dao = new CustomerDAO();
        int userId = dao.getUserIdByEmail(user.getEmail());
        if (userId == -1) {
            out.write("{\"error\":\"User not found\"}");
            return;
        }

        try {
            List<CustomerDAO.ChatMessage> messages = dao.getChatHistory(userId, null);
            Set<String> sessionIds = messages.stream()
                    .map(CustomerDAO.ChatMessage::getChatSessionId)
                    .collect(Collectors.toSet());

            List<Session> sessions = sessionIds.stream().map(sessionId -> {
                Timestamp timestamp = messages.stream()
                        .filter(msg -> msg.getChatSessionId().equals(sessionId))
                        .map(CustomerDAO.ChatMessage::getCreatedAt)
                        .min(Timestamp::compareTo)
                        .orElse(new Timestamp(System.currentTimeMillis()));
                return new Session(sessionId, timestamp.getTime());
            }).collect(Collectors.toList());

            Gson gson = new Gson();
            out.write(gson.toJson(new Response(sessions)));
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"error\":\"Error loading chat sessions: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Load Chat Sessions Servlet";
    }

    private static class Session {
        String sessionId;
        long timestamp;

        Session(String sessionId, long timestamp) {
            this.sessionId = sessionId;
            this.timestamp = timestamp;
        }
    }

    private static class Response {
        List<Session> sessions;

        Response(List<Session> sessions) {
            this.sessions = sessions;
        }
    }
}
