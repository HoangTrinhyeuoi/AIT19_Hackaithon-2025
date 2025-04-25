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
import java.util.List;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class LoadChatServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        UserGoogleDto user = (UserGoogleDto) session.getAttribute("customer");
        String sessionId = request.getParameter("sessionId");

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
            List<CustomerDAO.ChatMessage> messages = dao.getChatHistory(userId, sessionId);
            Gson gson = new Gson();
            out.write(gson.toJson(new Response(messages)));
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"error\":\"Error loading chat history: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Load Chat Servlet";
    }

    private static class Response {
        List<CustomerDAO.ChatMessage> messages;

        Response(List<CustomerDAO.ChatMessage> messages) {
            this.messages = messages;
        }
    }
}