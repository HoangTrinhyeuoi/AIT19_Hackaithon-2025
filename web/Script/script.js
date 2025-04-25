        let currentChatSessionId = 'chat_' + new Date().getTime(); // Khởi tạo session ID ban đầu

        const chatMessages = document.getElementById('chatMessages');
        const messageInput = document.getElementById('messageInput');
        const sendButton = document.getElementById('sendButton');
        const newChatBtn = document.getElementById('newChatBtn');
        const chatHistory = document.getElementById('chatHistory');

        const questions = [
            { prompt: "Bạn là nam hay nữ? (Nam/Nữ)", field: "gender", mapping: { "nam": "Male", "nữ": "Female", "nu": "Female" } },
            { prompt: "Tuổi của bạn là bao nhiêu?", field: "age", type: "number" },
            { prompt: "Bạn có bị tăng huyết áp không? (0 = Không, 1 = Có)", field: "hypertension", type: "number", options: ["0", "1"] },
            { prompt: "Bạn có bị bệnh tim không? (0 = Không, 1 = Có)", field: "heart_disease", type: "number", options: ["0", "1"] },
            { prompt: "Bạn đã từng kết hôn chưa? (Đã/Chưa)", field: "ever_married", mapping: { "đã": "Yes", "rồi": "Yes", "có": "Yes", "chưa": "No", "không": "No" } },
            { prompt: "Bạn đang làm công việc gì? (Trẻ em/Công chức/Chưa làm/Tư nhân/Tự làm)", field: "work_type", mapping: { "trẻ em": "children", "công chức": "Govt_job", "nhà nước": "Govt_job", "chưa làm": "Never_worked", "không làm": "Never_worked", "tư nhân": "Private", "công ty tư": "Private", "tự làm": "Self-employed", "freelancer": "Self-employed" } },
            { prompt: "Bạn sống ở nông thôn hay thành thị? (Thành thị/Nông thôn)", field: "Residence_type", mapping: { "thành thị": "Urban", "thành phố": "Urban", "nông thôn": "Rural", "quê": "Rural" } },
            { prompt: "Chỉ số đường huyết trung bình của bạn là bao nhiêu? (mg/dL)", field: "avg_glucose_level", type: "number" },
            { prompt: "Chiều cao của bạn là bao nhiêu (cm)?", field: "height", type: "number" },
            { prompt: "Cân nặng của bạn là bao nhiêu (kg)?", field: "weight", type: "number" },
            { prompt: "Tình trạng hút thuốc của bạn? (Đã từng hút/Chưa bao giờ hút/Đang hút/Không rõ)", field: "smoking_status", mapping: { "đã từng hút": "formerly smoked", "đã hút": "formerly smoked", "chưa bao giờ hút": "never smoked", "chưa hút": "never smoked", "đang hút": "smokes", "hút": "smokes", "không rõ": "Unknown", "không biết": "Unknown" } }
        ];

        let currentQuestionIndex = 0;
        let userData = {};
        let isCollectingData = false;

        // Tải danh sách phiên trò chuyện khi trang được tải
        window.onload = function() {
            loadChatSessions();
            displayWelcomeMessage();
        };

        function loadChatSessions() {
            fetch('/Hackaithon/LoadChatSessionsServlet')
            .then(response => response.json())
            .then(data => {
                console.log('Chat sessions:', data);
                if (data.sessions && data.sessions.length > 0) {
                    data.sessions.forEach(session => {
                        addChatSession(session.sessionId, session.timestamp);
                    });
                }
            })
            .catch(error => {
                console.error('Error loading chat sessions:', error);
            });
        }

        function addChatSession(sessionId, timestamp) {
            const historyItem = document.createElement('div');
            historyItem.className = 'chat-session';
            historyItem.setAttribute('data-session-id', sessionId);
            const date = timestamp ? new Date(timestamp).toLocaleDateString() : new Date().toLocaleDateString();
            historyItem.textContent = 'Cuộc trò chuyện ngày ' + date;
            historyItem.addEventListener('click', () => {
                loadChatHistory(sessionId);
            });
            chatHistory.appendChild(historyItem);
        }

        function loadChatHistory(sessionId) {
            fetch('/Hackaithon/LoadChatServlet?sessionId=' + sessionId)
            .then(response => {
                console.log('Response status:', response.status);
                return response.json();
            })
            .then(data => {
                console.log('Data received:', data);
                chatMessages.innerHTML = '';
                currentChatSessionId = sessionId; // Cập nhật session ID hiện tại
                if (data.messages && data.messages.length > 0) {
                    data.messages.forEach(msg => {
                        console.log('Message:', msg.message, 'Sender:', msg.sender);
                        const messageDiv = document.createElement('div');
                        messageDiv.textContent = msg.message;
                        messageDiv.style.marginBottom = '10px';
                        messageDiv.style.padding = '10px';
                        messageDiv.style.borderRadius = '8px';
                        messageDiv.style.maxWidth = '70%';
                        if (msg.sender === 'USER') {
                            messageDiv.style.background = '#007bff';
                            messageDiv.style.color = '#fff';
                            messageDiv.style.marginLeft = 'auto';
                        } else {
                            messageDiv.style.background = '#f8f9fa';
                        }
                        chatMessages.appendChild(messageDiv);
                    });
                    chatMessages.scrollTop = chatMessages.scrollHeight;
                    const botMessage = document.createElement('div');
                    botMessage.textContent = 'Đã tải cuộc trò chuyện cũ...';
                    botMessage.style.marginBottom = '10px';
                    botMessage.style.padding = '10px';
                    botMessage.style.background = '#f8f9fa';
                    botMessage.style.borderRadius = '8px';
                    botMessage.style.maxWidth = '70%';
                    chatMessages.appendChild(botMessage);
                } else {
                    const botMessage = document.createElement('div');
                    botMessage.textContent = 'Không có lịch sử trò chuyện.';
                    botMessage.style.marginBottom = '10px';
                    botMessage.style.padding = '10px';
                    botMessage.style.background = '#f8f9fa';
                    botMessage.style.borderRadius = '8px';
                    botMessage.style.maxWidth = '70%';
                    chatMessages.appendChild(botMessage);
                }
            })
            .catch(error => {
                console.error('Error loading chat history:', error);
                const botMessage = document.createElement('div');
                botMessage.textContent = '❗ Lỗi khi tải lịch sử trò chuyện: ' + error;
                botMessage.style.marginBottom = '10px';
                botMessage.style.padding = '10px';
                botMessage.style.background = '#f8f9fa';
                botMessage.style.borderRadius = '8px';
                botMessage.style.maxWidth = '70%';
                chatMessages.appendChild(botMessage);
            });
        }

        function displayWelcomeMessage() {
            const welcomeMessage = document.createElement('div');
            welcomeMessage.textContent = 'Chào bạn! Tôi có thể giúp bạn kiểm tra nguy cơ đột quỵ. Gõ "kiểm tra nguy cơ đột quỵ" để bắt đầu!';
            welcomeMessage.style.marginBottom = '10px';
            welcomeMessage.style.padding = '10px';
            welcomeMessage.style.background = '#f8f9fa';
            welcomeMessage.style.borderRadius = '8px';
            welcomeMessage.style.maxWidth = '70%';
            chatMessages.appendChild(welcomeMessage);
            saveChatMessage(welcomeMessage.textContent, 'BOT');
        }

        sendButton.addEventListener('click', sendMessage);
        messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') sendMessage();
        });

        function sendMessage() {
            const message = messageInput.value.trim();
            if (!message) return;

            const userMessage = document.createElement('div');
            userMessage.textContent = message;
            userMessage.style.marginBottom = '10px';
            userMessage.style.padding = '10px';
            userMessage.style.background = '#007bff';
            userMessage.style.color = 'white';
            userMessage.style.borderRadius = '8px';
            userMessage.style.maxWidth = '70%';
            userMessage.style.marginLeft = 'auto';
            chatMessages.appendChild(userMessage);

            // Lưu tin nhắn người dùng
            saveChatMessage(message, 'USER');

            if (!isCollectingData) {
                if (message.toLowerCase().includes("kiểm tra nguy cơ đột quỵ")) {
                    isCollectingData = true;
                    currentQuestionIndex = 0;
                    userData = {};
                    askQuestion();
                } else {
                    const botMessage = document.createElement('div');
                    botMessage.textContent = 'Tôi có thể giúp bạn kiểm tra nguy cơ đột quỵ. Gõ "kiểm tra nguy cơ đột quỵ" để bắt đầu!';
                    botMessage.style.marginBottom = '10px';
                    botMessage.style.padding = '10px';
                    botMessage.style.background = '#f8f9fa';
                    botMessage.style.borderRadius = '8px';
                    botMessage.style.maxWidth = '70%';
                    chatMessages.appendChild(botMessage);
                    saveChatMessage(botMessage.textContent, 'BOT');
                }
            } else {
                processAnswer(message);
            }

            messageInput.value = '';
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }

        function askQuestion() {
            if (currentQuestionIndex < questions.length) {
                const botMessage = document.createElement('div');
                botMessage.textContent = questions[currentQuestionIndex].prompt;
                botMessage.style.marginBottom = '10px';
                botMessage.style.padding = '10px';
                botMessage.style.background = '#f8f9fa';
                botMessage.style.borderRadius = '8px';
                botMessage.style.maxWidth = '70%';
                chatMessages.appendChild(botMessage);
                saveChatMessage(botMessage.textContent, 'BOT');
                chatMessages.scrollTop = chatMessages.scrollHeight;
            } else {
                predictStroke();
            }
        }

        function processAnswer(answer) {
            const question = questions[currentQuestionIndex];
            let processedAnswer = answer.toLowerCase();

            if (question.mapping) {
                for (const [key, value] of Object.entries(question.mapping)) {
                    if (processedAnswer.includes(key)) {
                        processedAnswer = value;
                        break;
                    }
                }
            }

            if (question.type === 'number') {
                if (!/^\d*\.?\d*$/.test(processedAnswer)) {
                    const botMessage = document.createElement('div');
                    botMessage.textContent = '❗ Vui lòng nhập một số hợp lệ.';
                    botMessage.style.marginBottom = '10px';
                    botMessage.style.padding = '10px';
                    botMessage.style.background = '#f8f9fa';
                    botMessage.style.borderRadius = '8px';
                    botMessage.style.maxWidth = '70%';
                    chatMessages.appendChild(botMessage);
                    saveChatMessage(botMessage.textContent, 'BOT');
                    chatMessages.scrollTop = chatMessages.scrollHeight;
                    return;
                }
                processedAnswer = parseFloat(processedAnswer);
            }

            if (question.options && !question.options.includes(processedAnswer.toString())) {
                const botMessage = document.createElement('div');
                botMessage.textContent = `❗ Vui lòng nhập một trong các giá trị: ${question.options.join(' hoặc ')}.`;
                botMessage.style.marginBottom = '10px';
                botMessage.style.padding = '10px';
                botMessage.style.background = '#f8f9fa';
                botMessage.style.borderRadius = '8px';
                botMessage.style.maxWidth = '70%';
                chatMessages.appendChild(botMessage);
                saveChatMessage(botMessage.textContent, 'BOT');
                chatMessages.scrollTop = chatMessages.scrollHeight;
                return;
            }

            userData[question.field] = processedAnswer;
            currentQuestionIndex++;
            askQuestion();
        }

        function predictStroke() {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 10000);

            console.log("Sending data to API:", userData);

            fetch('https://e497-104-196-113-10.ngrok-free.app/predict', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData),
                signal: controller.signal
            })
            .then(response => {
                clearTimeout(timeoutId);
                console.log("Response status:", response.status);
                return response.json();
            })
            .then(data => {
                console.log("Response data:", data);
                const botMessage = document.createElement('div');
                if (data.error) {
                    botMessage.textContent = '❗ Có lỗi xảy ra: ' + data.error;
                } else {
                    botMessage.textContent = data.result;
                }
                botMessage.style.marginBottom = '10px';
                botMessage.style.padding = '10px';
                botMessage.style.background = '#f8f9fa';
                botMessage.style.borderRadius = '8px';
                botMessage.style.maxWidth = '70%';
                chatMessages.appendChild(botMessage);
                saveChatMessage(botMessage.textContent, 'BOT');
                isCollectingData = false;
                chatMessages.scrollTop = chatMessages.scrollHeight;
            })
            .catch(error => {
                clearTimeout(timeoutId);
                console.error("Error connecting to API:", error);
                const botMessage = document.createElement('div');
                botMessage.textContent = '❗ Lỗi khi kết nối đến API: ' + error;
                botMessage.style.marginBottom = '10px';
                botMessage.style.padding = '10px';
                botMessage.style.background = '#f8f9fa';
                botMessage.style.borderRadius = '8px';
                botMessage.style.maxWidth = '70%';
                chatMessages.appendChild(botMessage);
                saveChatMessage(botMessage.textContent, 'BOT');
                isCollectingData = false;
                chatMessages.scrollTop = chatMessages.scrollHeight;
            });
        }

        function saveChatMessage(message, sender) {
            console.log('Saving message with sessionId:', currentChatSessionId);
            fetch('/Hackaithon/SaveChatServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    message: message,
                    sender: sender,
                    chatSessionId: currentChatSessionId
                })
            })
            .then(response => response.json())
            .then(data => {
                console.log('Message saved:', data);
                if (!data.success) {
                    console.error('Lỗi khi lưu tin nhắn:', data.error);
                }
            })
            .catch(error => {
                console.error('Lỗi:', error);
            });
        }

        newChatBtn.addEventListener('click', () => {
            chatMessages.innerHTML = '';
            currentChatSessionId = 'chat_' + new Date().getTime();
            isCollectingData = false;
            currentQuestionIndex = 0;
            userData = {};
            addChatSession(currentChatSessionId);
            displayWelcomeMessage();
        });