document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/check-login')
        .then(response => response.json())
        .then(data => {
            var loginItem = document.getElementById("login-item");
            var signupItem = document.getElementById("signup-item");
            var nicknameItem = document.getElementById("nickname-item");
            var bellItem = document.getElementById("bell-item");
            var chatItem = document.getElementById("chat-item");
            var logoutItem = document.getElementById("logout-item");
            var nickname = document.getElementById("nickname");
            var chatIcon = document.getElementById("chat-icon");
            var chatIconContainer = document.getElementById("chat-icon-container");
            var unreadCountSpan = document.getElementById("unread-count");

            if (data.isLoggedIn && data.nickname && data.userSeq) {
                loggedInUserNickname = data.nickname; // 글로벌 변수에 닉네임 할당

                loginItem.style.display = "none";
                signupItem.style.display = "none";

                nickname.innerText = data.nickname;
                nickname.href = '/user/detail'; // 닉네임 클릭 시 내 정보 페이지로 이동하도록 설정
                nicknameItem.style.display = "block";
                bellItem.style.display = "block";
                chatItem.style.display = "block";
                logoutItem.style.display = "block";

                chatIcon.addEventListener('click', function() {
                    const sender = loggedInUserNickname;
                    const receiver = ""; // 실제 필요한 receiver 정보를 넣으세요.
                    openChatWindow(sender, receiver);
                });

                connectWebSocket(loggedInUserNickname);  // WebSocket 연결 설정
                fetchUnreadMessagesCount(loggedInUserNickname);  // 페이지 로드 시 읽지 않은 메시지 개수를 가져옴

                enableDrag(chatIconContainer);

                // 이전 위치 로드
                loadChatIconPosition();
            } else {
                loginItem.style.display = "block";
                signupItem.style.display = "block";
                nicknameItem.style.display = "none";
                bellItem.style.display = "none";
                chatItem.style.display = "none";
                logoutItem.style.display = "none";
            }
        })
        .catch(error => console.error('Error:', error));
});
document.addEventListener("DOMContentLoaded", function() {
    // 현재 페이지의 URL 경로를 가져옵니다.
    const currentPath = window.location.pathname;

    // 각 메뉴 항목의 ID와 경로를 매핑합니다.
    const menuItems = {
        '/': 'home',
        '/noticeboard/paging': 'sokdak',
        '/freeboard/paging': 'sokdak',
        '/biticboard/paging': 'sokdak',
        '/shop/shoplist': 'shop',
        '/outside': 'outside'
    };

    // 현재 경로에 해당하는 메뉴 항목에 'active' 클래스를 추가합니다.
    if (menuItems[currentPath]) {
        document.getElementById(menuItems[currentPath]).classList.add('active');
    }
});
function connectWebSocket(nickname) {
    var socket = new SockJS('/ws');  // '/ws'는 WebSocket 엔드포인트
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // 서버로부터 특정 사용자의 알림을 구독
        stompClient.subscribe('/topic/notifications/' + nickname, function(message) {
            var chatMessage = JSON.parse(message.body);
            showNotification(chatMessage);  // 알림을 표시하는 함수 호출
        });

        // 서버로부터 읽지 않은 메시지 개수를 구독
        stompClient.subscribe('/topic/unread-count/' + nickname, function(message) {
            var unreadCount = JSON.parse(message.body).unreadCount;
            updateUnreadCount(unreadCount);  // 읽지 않은 메시지 개수 업데이트
        });

        // 초기 읽지 않은 메시지 개수 요청
        stompClient.send("/app/getUnreadCount", {}, JSON.stringify({ 'nickname': nickname }));
    });
}


// 읽지 않은 메시지 개수 가져오기
function fetchUnreadMessagesCount() {
    fetch('/api/unread-messages-count')
        .then(response => response.json())
        .then(count => {
            var unreadCountSpan = document.getElementById("unread-count");
            if (count > 0) {
                unreadCountSpan.innerText = count;
                unreadCountSpan.style.display = "inline-block";
            } else {
                unreadCountSpan.style.display = "none";
            }
        })
        .catch(error => console.error('Error fetching unread messages count:', error));
}

// 알림 목록을 클릭했을 때 표시
$('#bell-icon').click(function() {
    console.log("Bell icon clicked");
    fetchUnreadMessages();  // 알림 목록을 가져와 표시
    $('#dropdown-notifications').toggle(); // 알림 목록 표시/숨기기
});
function fetchUnreadMessagesCount(nickname) {
    fetch(`/api/unread-messages-count?nickname=${nickname}`)
        .then(response => response.json())
        .then(count => {
            var unreadCountSpan = document.getElementById("unread-count");
            if (count > 0) {
                unreadCountSpan.innerText = count;
                unreadCountSpan.style.display = "inline-block";
            } else {
                unreadCountSpan.style.display = "none";
            }
        })
        .catch(error => console.error('Error fetching unread messages count:', error));
}
function fetchUnreadMessages() {
    console.log("Fetching unread messages...");
    fetch('/api/notifications')
        .then(response => response.json())
        .then(data => {
            console.log("Received data:", data);
            var notificationList = $('#dropdown-notifications');
            notificationList.empty();

            if (data.notifications.length === 0) {
                notificationList.append('<a class="dropdown-item">새로운 알림이 없습니다.</a>');
            } else {
                data.notifications.forEach(function(notification) {
                    var listItem = $('<a class="dropdown-item"></a>');

                    // 메시지를 보낸 사람의 닉네임
                    var senderNickname = notification.sender.nickname;

                    // 알림 항목을 클릭했을 때 채팅창을 열도록 이벤트 설정
                    listItem.click(function() {
                        var receiverNickname = loggedInUserNickname; // 글로벌 변수 사용

                        // 알림 클릭 시 메시지를 보낸 사람(senderNickname)과 현재 사용자(receiverNickname) 간의 채팅방을 엽니다.
                        openChatWindow(receiverNickname, senderNickname);
                    });

                    // 읽지 않은 메시지와 읽은 메시지를 구분하여 스타일링
                    if (!notification.read) {  // 읽지 않은 메시지
                        listItem.css('font-weight', 'bold');
                        listItem.text(senderNickname + '님에게 메시지가 도착했습니다.');
                    } else {  // 읽은 메시지
                        listItem.css('color', 'gray');
                        listItem.text(senderNickname + '님에게 읽은 메시지가 있습니다.');
                    }

                    notificationList.append(listItem);
                });
            }
        })
        .catch(error => console.error('Error fetching notifications:', error));
}
/* 드래그 기능
function enableDrag(element) {
    let isDragging = false;
    let startX, startY, initialX, initialY;

    element.addEventListener('mousedown', (e) => {
        isDragging = true;
        startX = e.clientX;
        startY = e.clientY;
        initialX = element.offsetLeft;
        initialY = element.offsetTop;
        element.style.cursor = 'grabbing';
        document.body.style.userSelect = 'none'; // 텍스트 선택 비활성화
    });

    document.addEventListener('mousemove', (e) => {
        if (isDragging) {
            let deltaX = e.clientX - startX;
            let deltaY = e.clientY - startY;
            element.style.left = initialX + deltaX + 'px';
            element.style.top = initialY + deltaY + 'px';
        }
    });

    document.addEventListener('mouseup', () => {
        if (isDragging) {
            isDragging = false;
            element.style.cursor = 'grab';
            saveChatIconPosition(element.style.left, element.style.top);
            document.body.style.userSelect = ''; // 텍스트 선택 활성화
        }
    });
}
*/
function saveChatIconPosition(left, top) {
    localStorage.setItem('chatIconLeft', left);
    localStorage.setItem('chatIconTop', top);
}

function loadChatIconPosition() {
    const left = localStorage.getItem('chatIconLeft');
    const top = localStorage.getItem('chatIconTop');

    if (left !== null && top !== null) {
        const chatIconContainer = document.getElementById('chat-icon-container');
        chatIconContainer.style.left = left;
        chatIconContainer.style.top = top;
    }
}

function showNotification(chatMessage) {
    alert(`새 메시지가 도착했습니다: ${chatMessage.content}`);
}

function updateUnreadCount(unreadCount) {
    var unreadCountSpan = document.getElementById("unread-count");
    if (unreadCount > 0) {
        unreadCountSpan.innerText = unreadCount;
        unreadCountSpan.style.display = "inline-block";
    } else {
        unreadCountSpan.style.display = "none";
    }
}

function openChatWindow(sender, receiver) {
    let width = localStorage.getItem('chatWindowWidth') || 800;
    let height = localStorage.getItem('chatWindowHeight') || 600;

    // 화면의 중앙에 창을 열기 위해 위치 계산
    const left = (window.screen.width / 2) - (width / 2);
    const top = (window.screen.height / 2) - (height / 2);

    // 채팅창을 열 때, sender와 receiver를 URL 파라미터로 전달하고 위치와 크기를 지정
    const chatWindow = window.open(
        `/chat?sender=${encodeURIComponent(sender)}&receiver=${encodeURIComponent(receiver)}`, 
        'chatWindow', 
        `width=${width},height=${height},left=${left},top=${top},resizable=yes`
    );

    const minWidth = 400;
    const minHeight = 600;

    chatWindow.addEventListener('resize', () => {
        let newWidth = chatWindow.outerWidth;
        let newHeight = chatWindow.outerHeight;

        if (newWidth < minWidth || newHeight < minHeight) {
            newWidth = Math.max(newWidth, minWidth);
            newHeight = Math.max(newHeight, minHeight);
            chatWindow.resizeTo(newWidth, newHeight);
        }

        saveChatWindowSize(newWidth, newHeight);
    });
}

function saveChatWindowSize(width, height) {
    localStorage.setItem('chatWindowWidth', width);
    localStorage.setItem('chatWindowHeight', height);
}
function generateRoomId(sender, receiver) {
    return sender.localeCompare(receiver) > 0 ? sender + "_" + receiver : receiver + "_" + sender;
}