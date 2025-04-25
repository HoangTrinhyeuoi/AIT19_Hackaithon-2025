<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Model.UserGoogleDto"%>
<%@page import="DAO.CustomerDAO"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chatbot Y Tế</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="CSS/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
    <%
        UserGoogleDto user = (UserGoogleDto) session.getAttribute("customer");
        boolean isLoggedIn = user != null;
        CustomerDAO customerDAO = new CustomerDAO();
        int userId = isLoggedIn ? customerDAO.getUserIdByEmail(user.getEmail()) : -1;
        session.setAttribute("userId", userId);
    %>

    <div class="container">
        <%
            if (isLoggedIn) {
        %>
        <aside class="sidebar">
            <button class="new-chat-btn" id="newChatBtn">+ Cuộc trò chuyện mới</button>
            <div class="search-bar">
                <input type="text" placeholder="Tìm kiếm cuộc trò chuyện...">
                <span class="search-icon">🔍</span>
            </div>
            <nav class="nav-links" id="chatHistory">
                <div class="chat-history-title">Lịch sử trò chuyện</div>
            </nav>
            <div class="user-profile">
                <img src="https://ui-avatars.com/api/?name=<%= user.getName() != null ? user.getName().replace(" ", "+") : "Guest" %>" alt="User" class="avatar" id="userAvatar">
                <span id="usernameDisplay"><%= user.getName() != null ? user.getName() : "Guest" %></span>
                <form action="${pageContext.request.contextPath}/logout" method="get" style="display:inline;">
                    <button type="submit" class="logout-btn">Đăng xuất</button>
                </form>
            </div>
        </aside>

        <main class="main-content">
            <div class="chat-container">
                <div class="chat-messages" id="chatMessages"></div>
                <div class="message-input">
                    <input type="text" id="messageInput" placeholder="Nhập câu hỏi y tế của bạn...">
                    <button id="sendButton">Gửi</button>
                </div>
            </div>
        </main>
        <%
            } else {
        %>
        <main class="main-content">
            <div class="center-welcome">
                <h1>Chào mừng đến với Chatbot Y Tế</h1>
                <p>Vui lòng <a href="${pageContext.request.contextPath}/JSP/login.jsp">đăng nhập</a> để bắt đầu trò chuyện.</p>
            </div>
        </main>
        <%
            }
        %>
    </div>

    <footer>
        <p>&copy; 2025 MediChat - Trợ lý y tế thông minh. Mọi quyền được bảo lưu.</p>
        <p>Lưu ý: MediChat không thay thế tư vấn y tế chuyên nghiệp. Luôn tham khảo ý kiến bác sĩ trong trường hợp khẩn cấp.</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="Script/script.js"></script>
</body>
</html>