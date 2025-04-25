/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author DELL
 */

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy session hiện tại
        HttpSession session = request.getSession(false);
        
        // Kiểm tra và hủy session nếu tồn tại
        if (session != null) {
            // Xóa các thuộc tính người dùng
            session.removeAttribute("customer");
            session.removeAttribute("user");
            
            // Hủy toàn bộ session
            session.invalidate();
        }
        
        // Chuyển hướng về trang chủ khi chạy web
        response.sendRedirect("http://localhost:8080/Hackaithon/");
    }
}