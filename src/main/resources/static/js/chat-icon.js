document.addEventListener('DOMContentLoaded', function() {
    var chatIconContainer = document.getElementById("chat-icon-container");

    if (chatIconContainer) {
        enableDrag(chatIconContainer);
        loadChatIconPosition();
    }

    var chatIcon = document.getElementById("chat-icon");
    if (chatIcon) {
        chatIcon.addEventListener('click', function() {
            openChatWindow();
        });
    } else {
        console.error('Chat icon not found');
    }
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
        document.body.style.userSelect = 'none';
    });

    document.addEventListener('mousemove', (e) => {
        if (isDragging) {
            let deltaX = e.clientX - startX;
            let deltaY = e.clientY - startY;

            let newLeft = Math.max(0, Math.min(initialX + deltaX, window.innerWidth - element.offsetWidth));
            let newTop = Math.max(0, Math.min(initialY + deltaY, window.innerHeight - element.offsetHeight));

            element.style.left = newLeft + 'px';
            element.style.top = newTop + 'px';
        }
    });

    document.addEventListener('mouseup', () => {
        if (isDragging) {
            isDragging = false;
            element.style.cursor = 'grab';
            saveChatIconPosition(element.style.left, element.style.top);
            document.body.style.userSelect = '';
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

function openChatWindow() {
    // 여기에 채팅 창을 여는 로직을 추가하세요
}
