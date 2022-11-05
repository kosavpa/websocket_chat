(function() {
    var btnConnect    = null;
    var btnDisconnect = null;
    var btnSend       = null;
    var txtMessage    = null;
    var txtMessages   = null;
    var txtUsers      = null;
    var txtChat       = null;
    var txtUser       = null;
    
    var ws            = null;
    function init() {
        txtChat      = document.getElementById('chat'         );
        txtUser      = document.getElementById('user'         );
        btnConnect   = document.getElementById('btnConnect'   );
        btnDisconnect= document.getElementById('btnDisconnect');
        btnSend      = document.getElementById('btnSend'      );
    
        txtMessage   = document.getElementById('message'      );
        txtMessages  = document.getElementById('messages'     );
        txtUsers     = document.getElementById('users'        );
    
        btnConnect   .addEventListener('click',connect    ,false);
        btnDisconnect.addEventListener('click',disconnect ,false);
        btnSend      .addEventListener('click',sendMessage,false);
        lockFields(true);
    }

    function lockFields(mode) { 
        txtChat      .disabled =  mode;
        txtUser      .disabled =  mode;
        btnConnect   .disabled =  mode;
        btnDisconnect.disabled = !mode;
        btnSend      .disabled = !mode;
        txtMessage   .disabled = !mode;
    }

    function connect() {
        let chat = $("#chat").val();
        let user = $("#user").val();
        let host     = document.location.host + "/";
        let pathname = document.location.pathname;
        let data     = pathname.split('/');

        if (data.length > 2)
            pathname = "/" + data[1] + "/websocket";
        else
            pathname = "/websocket";
            
        let uri = null;
        if (window.location.protocol == 'http:')
            uri = 'ws://' + host + 
                            pathname + "/" + chat + "_" + user; 
        else
            uri = 'wss://' + host + ":9090" + 
                                pathname + "/" + chat + "_" + user;
        ws = new WebSocket(uri);
        ws.onmessage = function(event) {
            let msg = JSON.parse(event.data);

            switch(msg.type) {
                case "connection" :
                    onConnect(msg);
                    break;
                case "userlist" :
                    userList(msg.users);
                    break;
                case "message" :
                    addMessage2List(msg);
                    break;
                default :
                    console.log("Unknown message received : " 
                                                    + msg.type);
            }
        };
    }

    function onConnect(msg) {
        if (msg.result) {
            lockFields(false);
            addUser2List(msg.username);
        } else
            alert ('Ошибка подключения к чату');
    }

    function userList(users) {
        while (txtUsers.firstChild)
        txtUsers.removeChild(txtUsers.firstChild);

        users.forEach(function(username) {
            addUser2List(username);
        });
    }

    function addUser2List(userName) { 
        let p = document.createElement("p");
        p.setAttribute('style', 'margin:0px');
        if (userName === $("#user").val())
            p.innerHTML = "<b>" + userName + "</b><br />";
        else
            p.innerHTML = userName + "<br />";
        txtUsers.appendChild(p);
    }
    function disconnect() {
        clearComponents();
        lockFields(true);
        ws.close();
    }
    function clearComponents() {
        while (txtUsers.firstChild)
            txtUsers.removeChild(txtUsers.firstChild);
        while (txtMessages.firstChild)
            txtMessages.removeChild(txtMessages.firstChild);
        $("#message").val("");
    }
    function sendMessage() {
        let msg = {
                from : $("#user").val(),
                chat : $("#chat").val(), 
                type : "message",
                content : $("#message").val()
        };
        let msgJSON = JSON.stringify(msg);
        ws.send(msgJSON);
    }
    function addMessage2List(msg) {
        let from = null;
        let time = new Date();
        let align  = "";
        let margin = "";

        if (msg.from === $("#user").val()) {
            align  = "text-align:right";
            margin = 'margin:0px 0px 0px 130px;';
            from   = time.toLocaleTimeString() + "<br />";
        } else {
            align  = "text-align:left";
            margin = 'margin:0px;';
            from   = time.toLocaleTimeString() + 
                    ' ' + msg.from + "<br />";
        }

        let style = 'width:320px;' + margin + align;
        let span  = document.createElement("p");
        span.setAttribute('style', style);
        span.innerHTML = from + msg.content + "<br />";
        txtMessages.appendChild(span);
    }
       window.addEventListener('load', init, false);
    })();