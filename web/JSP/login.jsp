<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập | Hệ Thống Chatbot Y Tế</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        :root {
            --primary-color: #007bff; /* Xanh lam y tế */
            --primary-gradient: linear-gradient(to right, #007bff, #00c4ff);
            --secondary-color: #ffffff; /* Trắng sạch sẽ */
            --text-color: #333333;
            --google-color: #db4437;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Roboto', sans-serif;
            background: #f4f7fa;
            background-image: url('https://images.unsplash.com/photo-1576091160550-2173dba999ef?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80');
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            position: relative;
        }

        body::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.3);
            z-index: 0;
        }

        .navbar {
            background-color: rgba(0, 0, 0, 0.7);
            backdrop-filter: blur(10px);
            padding: 15px 0;
            z-index: 100;
            position: relative;
        }

        .navbar-brand {
            font-weight: 700;
            font-size: 1.8rem;
            color: white !important;
            letter-spacing: 1px;
        }

        .navbar-brand i {
            color: var(--primary-color);
            margin-right: 8px;
        }

        .login-container {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: calc(100vh - 132px);
            position: relative;
            z-index: 10;
            padding: 30px 0;
        }

        .login-card {
            width: 400px;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
            padding: 2.5rem;
            position: relative;
            overflow: hidden;
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
        }

        .login-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
        }

        .login-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 5px;
            background: var(--primary-gradient);
        }

        .login-card h2 {
            font-weight: 700;
            color: var(--text-color);
            text-align: center;
            margin-bottom: 1.5rem;
            font-size: 2rem;
            letter-spacing: 0.5px;
        }

        .login-card h2::after {
            content: '';
            display: block;
            width: 50px;
            height: 3px;
            background: var(--primary-gradient);
            margin: 0.7rem auto 0;
            border-radius: 5px;
        }

        .login-card p {
            text-align: center;
            color: var(--text-color);
            margin-bottom: 2rem;
            font-size: 1rem;
        }

        .btn-google {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 10px;
            background: var(--google-color);
            color: white;
            font-weight: 600;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 5px 15px rgba(219, 68, 55, 0.3);
            font-size: 1rem;
            text-decoration: none;
        }

        .btn-google i {
            margin-right: 10px;
            font-size: 1.2rem;
        }

        .btn-google:hover {
            box-shadow: 0 8px 20px rgba(219, 68, 55, 0.4);
            transform: translateY(-2px);
            text-decoration: none;
            color: white;
        }

        .error-message {
            background-color: #fff5f5;
            border-left: 4px solid #ff3b30;
            color: #ff3b30;
            padding: 10px 15px;
            margin-bottom: 1.5rem;
            border-radius: 5px;
            font-size: 0.9rem;
            display: flex;
            align-items: center;
        }

        .error-message i {
            margin-right: 10px;
            font-size: 1.1rem;
        }

        footer {
            background-color: rgba(0, 0, 0, 0.85);
            color: white;
            padding: 15px 0;
            position: relative;
            z-index: 10;
            margin-top: auto;
            backdrop-filter: blur(10px);
        }

        footer p {
            margin-bottom: 0.3rem;
            font-size: 0.9rem;
            opacity: 0.9;
        }

        footer p:first-child {
            font-weight: 600;
        }

        .social-icons {
            display: flex;
            justify-content: center;
            margin-top: 0.5rem;
        }

        .social-icons a {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 36px;
            height: 36px;
            margin: 0 8px;
            border-radius: 50%;
            background-color: rgba(255, 255, 255, 0.1);
            color: white;
            transition: all 0.3s ease;
        }

        .social-icons a:hover {
            background-color: var(--primary-color);
            transform: translateY(-3px);
        }

        @media (max-width: 576px) {
            .login-card {
                width: 90%;
                padding: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/chat.jsp">
                <i class="fas fa-stethoscope"></i>Chatbot Y Tế
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
        </div>
    </nav>

    <!-- Form đăng nhập -->
    <div class="login-container">
        <div class="login-card">
            <h2>Đăng Nhập</h2>
            <p>Đăng nhập bằng tài khoản Google để sử dụng Chatbot Y Tế</p>
            
            <%-- Hiển thị thông báo lỗi nếu có --%>
            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i>
                    <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>
            
            <%-- Nút đăng nhập Google --%>
            <% 
                // Tạo URL Google đăng nhập
                String googleLoginUrl = "https://accounts.google.com/o/oauth2/auth" +
                    "?scope=email%20profile%20openid" +
                    "&redirect_uri=http://localhost:8080/Hackaithon/loginGoogle" +
                    "&response_type=code" +
                    "&client_id=862714784669-f9cstu0rme5fr9hui1hbfleno5ck71vf.apps.googleusercontent.com" +
                    "&access_type=offline" +
                    "&prompt=consent";
                    
                // Thêm tham số redirect vào URL nếu cần
                String redirectURL = (String) request.getAttribute("redirect");
                if (redirectURL == null) {
                    redirectURL = request.getParameter("redirect");
                }
                if (redirectURL != null && !redirectURL.isEmpty()) {
                    googleLoginUrl += "&state=" + redirectURL;
                }
            %>
            
            <a href="<%= googleLoginUrl %>" class="btn-google">
                <i class="fab fa-google"></i> Đăng nhập bằng Google
            </a>
        </div>
    </div>
    
    <!-- Footer -->
    <footer>
        <div class="container text-center">
                <p>&copy; 2025 MediChat - Trợ lý y tế thông minh. Mọi quyền được bảo lưu.</p>
                <p>Lưu ý: MediChat không thay thế tư vấn y tế chuyên nghiệp. Luôn tham khảo ý kiến bác sĩ trong trường hợp khẩn cấp.</p>
        </div>
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>