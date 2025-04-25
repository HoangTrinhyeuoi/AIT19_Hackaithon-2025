/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import DAO.CustomerDAO;
import Model.UserGoogleDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

public class GoogleLogin extends HttpServlet {

    // Danh sách email admin (hardcode)
    private static final List<String> ADMIN_EMAILS = Arrays.asList(
        "hoangtrinh240705@gmail.com" // Tài khoản thứ 3 làm admin
    );

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Servlet GoogleLogin processRequest() đã được gọi!");

        String code = request.getParameter("code");
        String redirectURL = null;
        String state = request.getParameter("state");
        if (state != null && !state.isEmpty()) {
            redirectURL = state;
        }
        
        try {
            String accessToken = getToken(code);
            UserGoogleDto user = getUserInfo(accessToken);
            System.out.println("Google user info: " + user);

            CustomerDAO customerDAO = new CustomerDAO();
            if (!customerDAO.isUserExists(user.getEmail())) {
                boolean saved = customerDAO.saveUser(user);
                if (!saved) {
                    System.out.println("Lỗi: Không thể lưu user vào database!");
                    request.setAttribute("errorMessage", "Không thể lưu thông tin người dùng. Vui lòng thử lại.");
                    request.getRequestDispatcher("/JSP/login.jsp").forward(request, response);
                    return;
                }
            } else {
                System.out.println("User đã tồn tại trong database: " + user.getEmail());
            }

            // Lưu thông tin vào session
            HttpSession session = request.getSession();
            session.setAttribute("customer", user);

            // Kiểm tra xem user có phải admin không
            boolean isAdmin = ADMIN_EMAILS.contains(user.getEmail());
            session.setAttribute("role", isAdmin ? "admin" : "user"); // Lưu role vào session để dùng sau
            System.out.println("User " + user.getEmail() + " có role: " + (isAdmin ? "admin" : "user"));

            // Chuyển hướng theo vai trò
            if (isAdmin) {
                response.sendRedirect(request.getContextPath() + "/JSP/admin.jsp");
                return;
            }

            // Chuyển hướng đến redirectURL hoặc Chat.jsp (qua index.jsp)
            if (redirectURL != null) {
                session.removeAttribute("redirect");
                response.sendRedirect(request.getContextPath() + "/" + redirectURL);
            } else {
                request.getRequestDispatcher("/JSP/chat.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Lỗi trong processRequest: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi đăng nhập bằng Google: " + e.getMessage());
            request.getRequestDispatcher("/JSP/login.jsp").forward(request, response);
        }
    }

    public static String getToken(String code) throws ClientProtocolException, IOException {
        String response = Request.Post(Constants.GOOGLE_LINK_GET_TOKEN)
                .bodyForm(Form.form()
                        .add("client_id", Constants.GOOGLE_CLIENT_ID)
                        .add("client_secret", Constants.GOOGLE_CLIENT_SECRET)
                        .add("redirect_uri", Constants.GOOGLE_REDIRECT_URI)
                        .add("code", code)
                        .add("grant_type", Constants.GOOGLE_GRANT_TYPE)
                        .build())
                .execute().returnContent().asString();

        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        return jobj.get("access_token").getAsString();
    }

    public static UserGoogleDto getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
        String link = Constants.GOOGLE_LINK_GET_USER_INFO + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        return new Gson().fromJson(response, UserGoogleDto.class);
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
        return "Google Login Servlet";
    }
}