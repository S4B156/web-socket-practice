'use strict';

const chatPage = document.querySelector('#chat-page');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let stompClient = null;
let username = null;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
const reconnectDelay = 3000;

const colors = [
    '#4a90e2', '#50c878', '#20c997', '#e63946',
    '#f4a261', '#f48fb1', '#f1c40f', '#2b95a8'
];

function connect() {
    username = document.getElementById('data')?.getAttribute('data-user');
    if (!username) {
        showConnectionError('Username not found. Please log in again.');
        return;
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    reconnectAttempts = 0;
    stompClient.subscribe('/topic/public', onMessageReceived);
    connectingElement.classList.add('hidden');
    connectingElement.textContent = '';
    console.log('WebSocket connected for user:', username);
}

function onError(error) {
    console.error('WebSocket error:', error);
    if (reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        connectingElement.textContent = `Connection lost. Reconnecting... (Attempt ${reconnectAttempts}/${maxReconnectAttempts})`;
        connectingElement.style.color = '#f4a261';
        setTimeout(connect, reconnectDelay);
    } else {
        showConnectionError('Could not connect after multiple attempts. Please refresh to try again.');
    }
}

function showConnectionError(message) {
    connectingElement.textContent = message;
    connectingElement.style.color = '#e63946';
    connectingElement.classList.remove('hidden');
}

function sanitizeInput(input) {
    const div = document.createElement('div');
    div.textContent = input.trim();
    return div.innerHTML.replace(/[<>]/g, '');
}

function sendMessage(event) {
    event.preventDefault();
    const messageContent = messageInput.value.trim();
    if (!messageContent || !stompClient || !stompClient.connected) {
        return;
    }

    const chatMessage = {
        sender: username,
        content: sanitizeInput(messageContent),
        type: 'CHAT'
    };
    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
    messageInput.value = '';
}

function onMessageReceived(payload) {
    console.log('Received message:', payload.body); // Debug incoming messages
    let message;
    try {
        message = JSON.parse(payload.body);
    } catch (e) {
        console.error('Failed to parse message:', payload.body, e);
        return;
    }

    const messageElement = document.createElement('li');

    if (message.type === 'JOIN' || message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        messageElement.style.color = message.type === 'JOIN' ? '#50c878' : '#e63946';
        messageElement.style.fontStyle = 'italic';
        message.content = message.type === 'JOIN'
            ? `${message.sender} joined the chat`
            : `${message.sender} left the chat`;
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('i');
        avatarElement.textContent = message.sender[0];
        avatarElement.style.backgroundColor = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('span');
        usernameElement.textContent = message.sender;
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('p');
    textElement.textContent = message.content;
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    const index = Math.abs(hash % colors.length);
    return colors[index];
}

function disconnect() {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect();
    }
    showConnectionError('Disconnected. Please refresh to reconnect.');
}

connect();
messageForm.addEventListener('submit', sendMessage);
window.addEventListener('beforeunload', disconnect);