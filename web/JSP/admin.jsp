<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Model.UserGoogleDto"%>
<%@page import="DAO.CustomerDAO"%>
<%@page import="DAO.CustomerDAO.ChatMessage"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Quản Trị | Chatbot Y Tế</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        :root {
            --primary-color: #007bff;
            --primary-gradient: linear-gradient(to right, #007bff, #00c4ff);
            --secondary-color: #ffffff;
            --text-color: #333333;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background: #f4f7fa;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .container {
            margin-top: 90px;
            padding: 20px;
            max-width: 1200px;
            margin-left: auto;
            margin-right: auto;
            padding-bottom: 90px;
            min-height: calc(100vh - 180px);
        }

        .admin-content {
            background: var(--secondary-color);
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }

        .admin-content h1 {
            font-size: 2rem;
            font-weight: 700;
            color: var(--text-color);
            margin-bottom: 1rem;
            text-align: center;
        }

        .table {
            margin-bottom: 20px;
        }

        .table th, .table td {
            vertical-align: middle;
        }

        .chat-history {
            max-height: 400px;
            overflow-y: auto;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .chat-message {
            margin-bottom: 10px;
            padding: 10px;
            border-radius: 8px;
        }

        .chat-message.user {
            background: #007bff;
            color: white;
            margin-left: auto;
            max-width: 70%;
        }

        .chat-message.bot {
            background: #e9ecef;
            max-width: 70%;
        }

        .session-divider {
            margin: 20px 0;
            border-top: 1px solid #ccc;
            text-align: center;
            font-weight: bold;
            color: #666;
        }

        .btn-danger {
            transition: all 0.3s ease;
        }

        .btn-danger:hover {
            box-shadow: 0 5px 15px rgba(255, 59, 48, 0.4);
        }

        footer {
            background-color: var(--dark-color);
            color: white;
            padding: 2rem;
            text-align: center;
            margin-top: 3rem;
        }

        @media (max-width: 768px) {
            .container {
                padding: 10px;
                margin-top: 70px;
                padding-bottom: 70px;
            }

            .navbar, footer {
                max-width: 100%;
            }
        }
    </style>
</head>
<body>

    <%
        UserGoogleDto user = (UserGoogleDto) session.getAttribute("customer");
        String role = (String) session.getAttribute("role");
        if (user == null || !"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/JSP/login.jsp");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        List<UserGoogleDto> users = customerDAO.getAllUsers();
        String selectedEmail = request.getParameter("email");
        List<ChatMessage> chatHistory = null;
        if (selectedEmail != null) {
            int selectedUserId = customerDAO.getUserIdByEmail(selectedEmail);
            if (selectedUserId != -1) {
                chatHistory = customerDAO.getChatHistory(selectedUserId);
            }
        }

        // Xử lý xóa lịch sử chat
        if ("POST".equalsIgnoreCase(request.getMethod()) && "delete".equals(request.getParameter("action"))) {
            String emailToDelete = request.getParameter("email");
            if (emailToDelete != null) {
                int userIdToDelete = customerDAO.getUserIdByEmail(emailToDelete);
                if (userIdToDelete != -1) {
                    customerDAO.deleteChatHistory(userIdToDelete);
                }
                response.sendRedirect(request.getContextPath() + "/JSP/admin.jsp");
                return;
            }
        }
    %>

    <div class="container">
        <div class="admin-content">
            <h1>Trang Quản Trị</h1>
            <h2>Xin chào, <%= user.getName() != null ? user.getName() : "Admin" %>!</h2>
            <form action="${pageContext.request.contextPath}/logout" method="get" style="display:inline;">
                    <button type="submit" class="logout-btn">Đăng xuất</button>
            </form>

            <!-- Danh sách người dùng -->
            <h3>Danh sách người dùng</h3>
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Email</th>
                        <th>Tên</th>
                        <th>Họ</th>
                        <th>Tên đệm</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (UserGoogleDto u : users) { %>
                    <tr>
                        <td><a href="admin.jsp?email=<%= u.getEmail() %>"><%= u.getEmail() %></a></td>
                        <td><%= u.getName() != null ? u.getName() : "" %></td>
                        <td><%= u.getFamily_name() != null ? u.getFamily_name() : "" %></td>
                        <td><%= u.getGiven_name() != null ? u.getGiven_name() : "" %></td>
                        <td>
                            <form action="admin.jsp" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="email" value="<%= u.getEmail() %>">
                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Bạn có chắc muốn xóa lịch sử chat của <%= u.getEmail() %>?')">Xóa lịch sử chat</button>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>

            <!-- Lịch sử chat -->
            <% if (selectedEmail != null && chatHistory != null && !chatHistory.isEmpty()) { %>
            <h3>Lịch sử chat của <%= selectedEmail %></h3>
            <div class="chat-history">
                <% 
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String lastSessionId = null;
                    for (ChatMessage msg : chatHistory) { 
                        if (lastSessionId == null || !lastSessionId.equals(msg.getChatSessionId())) {
                            if (lastSessionId != null) {
                %>
                <div class="session-divider"></div>
                <% 
                            }
                            lastSessionId = msg.getChatSessionId();
                %>
                <div class="session-divider">Phiên chat: <%= msg.getChatSessionId() %></div>
                <% } %>
                <div class="chat-message <%= msg.getSender().equals("USER") ? "user" : "bot" %>">
                    <strong><%= msg.getSender().equals("USER") ? "Người dùng" : "Bot" %>:</strong>
                    <%= msg.getMessage() %>
                    <br>
                    <small><%= sdf.format(msg.getCreatedAt()) %></small>
                </div>
                <% } %>
            </div>
            <% } else if (selectedEmail != null) { %>
            <p>Không có lịch sử chat cho <%= selectedEmail %>.</p>
            <% } %>
        </div>
    </div>

    <footer>
        <p>&copy; 2025 MediChat - Trợ lý y tế thông minh. Mọi quyền được bảo lưu.</p>
        <p>Lưu ý: MediChat không thay thế tư vấn y tế chuyên nghiệp. Luôn tham khảo ý kiến bác sĩ trong trường hợp khẩn cấp.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>