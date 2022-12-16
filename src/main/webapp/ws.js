var btnConnect = null;
var btnDisconnect = null;
var btnSend = null;
var username = null;
var chatArea = null;
var userList = null;
var messageText = null;
var webSocket = null;

document.addEventListener('DOMContentLoaded', function(){
  init();
});

function init() {
  btnConnect = document.getElementById('connect_button');
  btnDisconnect = document.getElementById('disconnect_button');
  btnSend = document.getElementById('send_button');
  username = document.getElementById('carrent_user');
  chatArea = document.getElementById('chat_area');
  userList = document.getElementById('user_list');
  messageText = document.getElementById('message_text');

  btnConnect.addEventListener('click', connect, false);
  btnDisconnect.addEventListener('click', disconnect, false);
  btnSend.addEventListener('click', sendMessage, false);

  lockFields(true);
}

function lockFields(mode) {
  btnConnect.disabled = !mode;
  username.disabled = !mode;
  btnDisconnect.disabled = mode;
  btnSend.disabled = mode;
  chatArea.disabled = mode;
  userList.disabled = mode;
  messageText.disabled = mode;
}

function connect() {
  let usernameForURL = username.value;
  let port = window.location.port;
  let protocol = window.location.protocol == 'https' ? 'wss' : 'ws';
  let hostname = window.location.hostname;
  let wsURL = protocol + '://' + hostname + ':' + port + '/websocket_chat/' + usernameForURL;
  console.log(wsURL);

  webSocket = new WebSocket(wsURL);

  webSocket.onmessage = function(event) {
    let message = JSON.parse(event.data);

    switch(message.type) {
        case 'CONNECTION' : onConnect(message); break;
        case 'USER_LIST' : updateUserList(message.userList); break;
        case 'MESSAGE' : updateChatArea(message); break;
        default : console.log('Unknown message received : ' + msg.type);
    }
  }

  webSocket.onclose = function(){
    alert('You are exit from chat, for back connect input username and press connect.');
  }
}

function onConnect(message) {
  if (message.resultConnectStatus) {
    lockFields(false);
    alert (message.content);
  } else {
    alert (message.content);
  }
}

function updateUserList(userListFromMessage) {
  while (userList.firstChild)
    userList.removeChild(userList.firstChild);
  
    userListFromMessage.forEach(function(username) {
      let div = document.createElement('div');

      if (username == username.value)
        div.innerHTML = '<b>' + username + '</b></br>';
      else
        div.innerHTML = username + '</br>';
    
      userList.appendChild(div);
  });
}

function disconnect() {
  clearAll();
  lockFields(true);
  webSocket.close();
}

function clearAll() {
  while (userList.firstChild)
    userList.removeChild(userList.firstChild);

  while (chatArea.firstChild)
    chatArea.removeChild(chatArea.firstChild);

  messageArea.value = '';
}

function sendMessage() {
  let message = {
    from : username.value,
    type : 'MESSAGE',
    content : messageText.value
  };

  let msgJSON = JSON.stringify(message);
  webSocket.send(msgJSON);
  messageText.value = '';
}

function updateChatArea(message) {
  let from = null;
  let align  = '';
  let margin = '';

  if (message.from == username.value) {
    align = 'text-align:right';
	  margin = 'margin:10px 20px 0px 20px;';
    from = message.from + ':' + '</br>';
  } else {
    align  = 'text-align:left';
  	margin = 'margin:10px 20px 0px 20px;';
  	from   = message.from + ':' + '</br>';
  }

  let style = margin + align;
    
  let div = document.createElement('div');
  div.setAttribute('style', style);
    
  div.innerHTML = from + message.content;

  chatArea.appendChild(div);
}