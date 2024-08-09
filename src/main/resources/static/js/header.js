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

      if (data.isLoggedIn && data.nickname && data.userSeq) {
        loginItem.style.display = "none";
        signupItem.style.display = "none";

        nickname.innerText = data.nickname;
        nickname.href = '/user/' + data.userSeq;
        nicknameItem.style.display = "block";
        bellItem.style.display = "block";
        chatItem.style.display = "block";
        logoutItem.style.display = "block";

        chatIcon.addEventListener('click', function() {
          const sender = data.nickname;
          const receiver = "";
          openChatWindow(sender, receiver);
        });

        connectWebSocket(data.nickname);

        // 벨 아이콘 클릭 시 읽지 않은 메시지 모달 표시
        document.getElementById('bell-icon').addEventListener('click', function() {
          fetchUnreadMessages();
        });

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

function connectWebSocket(nickname) {
  var socket = new SockJS('/ws');
  var stompClient = Stomp.over(socket);

  stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/notifications/' + nickname, function(message) {
      var chatMessage = JSON.parse(message.body);
      showNotification(chatMessage);
    });

    // 구독하여 읽지 않은 메시지 개수 업데이트
    stompClient.subscribe('/topic/unread-count/' + nickname, function(message) {
      var unreadCount = JSON.parse(message.body).unreadCount;
      console.log('Unread Count:', unreadCount); // 콘솔에 읽지 않은 메시지 개수 출력
      updateUnreadCount(unreadCount);
    });

    // 초기 읽지 않은 메시지 개수 요청
    stompClient.send("/app/getUnreadCount", {}, JSON.stringify({ 'nickname': nickname }));
  });
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

function fetchUnreadMessages() {
  fetch('/chat/unreadMessages')
    .then(response => response.json())
    .then(data => {
      var unreadMessagesList = document.getElementById('unread-messages-list');
      unreadMessagesList.innerHTML = ''; // 기존 목록 초기화

      data.forEach(function(message) {
        var listItem = document.createElement('li');
        listItem.textContent = message.content;
        unreadMessagesList.appendChild(listItem);
      });

      var modal = document.getElementById('unread-messages-modal');
      modal.style.display = 'block';

      var closeButton = document.querySelector('#unread-messages-modal .close');
      closeButton.addEventListener('click', function() {
        modal.style.display = 'none';
      });

      window.addEventListener('click', function(event) {
        if (event.target == modal) {
          modal.style.display = 'none';
        }
      });
    })
    .catch(error => console.error('Error:', error));
}

function showNotification(chatMessage) {
  // 알림 메시지를 표시하는 로직을 추가합니다.
  alert(`새 메시지가 도착했습니다: ${chatMessage.content}`);
}

function saveChatWindowSize(width, height) {
  localStorage.setItem('chatWindowWidth', width);
  localStorage.setItem('chatWindowHeight', height);
}

function openChatWindow(sender, receiver) {
  let width = localStorage.getItem('chatWindowWidth') || 800;
  let height = localStorage.getItem('chatWindowHeight') || 600;

  const chatWindow = window.open(`/chat?sender=${encodeURIComponent(sender)}&receiver=${encodeURIComponent(receiver)}`, 'chatWindow', `width=${width},height=${height},resizable=yes`);

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
