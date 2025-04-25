<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="Model.Customer" %>
<%@page import="Model.UserGoogleDto" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Xuất - Sneaker Space</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .logout-container {
            background-color: white;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            padding: 2.5rem;
            text-align: center;
            max-width: 500px;
            width: 100%;
        }
        .logout-icon {
            font-size: 5rem;
            color: #6a11cb;
            margin-bottom: 1.5rem;
        }
        .btn-home {
            background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
            border: none;
            color: white;
            padding: 10px 20px;
            border-radius: 50px;
            transition: transform 0.3s ease;
        }
        .btn-home:hover {
            transform: scale(1.05);
        }
        .logout-message {
            color: #333;
            margin-bottom: 1.5rem;
        }
    </style>
</head>
<body>
    <%
        // Lấy thông tin người dùng trước khi đăng xuất
        Object userObj = session.getAttribute("customer");
        Object googleUserObj = session.getAttribute("user");
        
        String userName = "";
         if (googleUserObj instanceof UserGoogleDto) {
            userName = ((UserGoogleDto) googleUserObj).getName();
        }

        // Xóa các thuộc tính người dùng
        session.removeAttribute("customer");
        session.removeAttribute("user");
        
        // Hủy toàn bộ session
        session.invalidate();
    %>

    <div class="logout-container">
        <div class="logout-icon">
            <i class="fas fa-sign-out-alt"></i>
        </div>
        
        <h2 class="mb-4">Đăng Xuất Thành Công</h2>
        
        <% if (!userName.isEmpty()) { %>
            <p class="logout-message">Tạm biệt, <%= userName %>! Hẹn gặp lại bạn.</p>
        <% } else { %>
            <p class="logout-message">Bạn đã đăng xuất khỏi hệ thống.</p>
        <% } %>
        
        <div class="spinner-border text-primary mb-4" role="status">
            <span class="visually-hidden">Đang chuyển hướng...</span>
        </div>
        
        <a href="${pageContext.request.contextPath}/index.html" class="btn btn-home">
            <i class="fas fa-home me-2"></i>Về Trang Chủ
        </a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    
    <script>
        // Tự động chuyển hướng về trang chủ sau 3 giây
        setTimeout(function() {
            window.location.href = '${pageContext.request.contextPath}/index.html';
        }, 3000);
    </script>
</body>
</html>