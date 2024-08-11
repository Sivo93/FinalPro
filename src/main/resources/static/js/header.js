document.addEventListener('DOMContentLoaded', function() {
  // 모든 링크를 절대 경로로 설정
  var links = document.querySelectorAll('#ftco-navbar .nav-link');
  links.forEach(function(link) {
    if (!link.href.startsWith(window.location.origin)) {
      link.href = window.location.origin + link.getAttribute('href');
    }
  });

  // 로그인 상태 확인
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

      if (data.isLoggedIn && data.nickname && data.userSeq) {
        // 로그인 상태
        loginItem.style.display = "none";
        signupItem.style.display = "none";

        nickname.innerText = data.nickname;
        nickname.href = '/user/' + data.userSeq;  // 사용자 상세 페이지 링크 설정
        nicknameItem.style.display = "block";
        bellItem.style.display = "block";
        chatItem.style.display = "block";
        logoutItem.style.display = "block";

        // 채팅 창 열기 함수 호출
        chatIcon.addEventListener('click', function() {
          const sender = data.nickname;
          const receiver = ""; // receiver는 필요에 따라 설정
          openChatWindow(sender, receiver);
        });

      } else {
        // 비로그인 상태
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

// 창 크기 저장 함수
function saveChatWindowSize(width, height) {
  localStorage.setItem('chatWindowWidth', width);
  localStorage.setItem('chatWindowHeight', height);
}

// 채팅 창 열기 함수
function openChatWindow(sender, receiver) {
  // 로컬 스토리지에서 저장된 창 크기를 불러옴
  let width = localStorage.getItem('chatWindowWidth') || 800;
  let height = localStorage.getItem('chatWindowHeight') || 600;

  // 새로운 창 열기
  const chatWindow = window.open(`/chat?sender=${encodeURIComponent(sender)}&receiver=${encodeURIComponent(receiver)}`, 'chatWindow', `width=${width},height=${height},resizable=yes`);

  // 최소 창 크기 설정
  const minWidth = 400; // 최소 너비 (픽셀 단위)
  const minHeight = 600; // 최소 높이 (픽셀 단위)

  // 창 크기가 변경될 때 최소 크기를 유지하도록 조정
  chatWindow.addEventListener('resize', () => {
    let newWidth = chatWindow.outerWidth;
    let newHeight = chatWindow.outerHeight;

    // 최소 크기보다 작아질 경우, 최소 크기로 재조정
    if (newWidth < minWidth || newHeight < minHeight) {
      newWidth = Math.max(newWidth, minWidth);
      newHeight = Math.max(newHeight, minHeight);
      chatWindow.resizeTo(newWidth, newHeight);
    }

    // 현재 크기 로컬 스토리지에 저장
    saveChatWindowSize(newWidth, newHeight);
  });
}