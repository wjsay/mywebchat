
var websocket = null;
var useritem = "<li class=\"list-group-item\">username</li>";
var mymsgitem =  '<div class="right"><p class="message-item">content</p></div>';
var frimsgitem = '<div class="left"><p class="message-item"><a>name</a>：content</p></div>';
$(document).ready(function(){
	initConnection();
    loadGroupChatHistory();
});

function initConnection() {
	if ("WebSocket" in window) {
		websocket = new WebSocket(url);
	} else {
		alert("您的浏览器不支持WebSocket！");
		return;
	}
	websocket.onerror = function() {
		alert("WebSocket Error");
	};
	websocket.onopen = function() {

	};
	websocket.onclose = function() {
        alert('您当前已经被注销！请重新登录');
	};
	window.onbeforeunload = function(){  // 卸载页面前
		backup();
	};
	websocket.onmessage = function(event) {
		if (event.data instanceof ArrayBuffer) {
			// 二进制数据
			byte = event.data;
			return;
		}
		if (event.data instanceof Blob) {
            byte = event.data;
            var reader = new FileReader();
            reader.readAsDataURL(byte);
            var time = new Date();
            reader.onload = function(event) {
            	var msg = event.target.result;
            	alert(msg);
            }
            return;
		}
		var msg = JSON.parse(event.data);
		switch (msg.type) {
            case -1:
                removeUser(msg.content);
                break;
            case 1:
                addUser(msg.content);
                break;
            case 2:
                break;
            case 3:
                addMessageItem(msg);
                break;
        }
	}
}
function backup() {
    let html = $("#message-frame").html();
    window.localStorage.setItem("_Group", html);
}
function setOnlineCount(n) {
	$("#onlineCount").html(n);
}
function addUser(user) {
    if (user) {
        let html = document.getElementById("userList").innerHTML;
        if (html) {
            document.getElementById("userList").innerHTML +=  useritem.replace("username", user.username);
        } else {
            document.getElementById("userList").innerHTML = useritem.replace("username", user.username);
        }
        setOnlineCount(parseInt(document.getElementById("onlineCount").innerText) + 1);
    }
}
function removeUser(user) {
    let lis = document.getElementById("userList").querySelectorAll("li");
    for (let i = 0; i < lis.length; ++i) {
        if (lis[i].innerText === user.username) {
            lis[i].remove();
            setOnlineCount(parseInt(document.getElementById("onlineCount").innerText) - 1);
            break;
        }
    }
}
function loadGroupChatHistory() {
    let html = window.localStorage.getItem("_Group");
    $("#message-frame").html(html);
}
function logout() {
    $.ajax({
        url: '/logout',
        type: 'POST',
        success: function (data) {
            if (data.status === 0) {
                window.location.href = "/";
            } else {
                alert(data.message);
            }
        },
        error: function () {
            alert("请求出错");
        }
    });
}
function refresh() {
    $.ajax({
       url: "/refresh",
       type: 'GET',
       success: function (data) {
            if (data.status === 0) {
                users = data.data;
                setOnlineCount(data.data.length);
                let lis = "";
                data.data.forEach(username=>{
                    lis += useritem.replace("username", username);
                });
                document.getElementById("userList").innerHTML = lis;
            } else {
                alert(data.message);
            }
       },
       error: function (data) {
            alert("请求失败");
       }
    });
}
function keydown(event) {
    if (event.keyCode == 13) {
        sendMessage();
    }
}
function sendMessage() {
    let input = document.getElementById("input-text");
    let text = input.value;
    if (text == null || text.length <= 0) {
        return;
    }
    let msg = {
        from: user.username,
        content: text
    };
    if (websocket == null) {
        alert("您已下线，请退出本页重新登陆");
        return;
    }
    addMessageItem(msg);
    input.value = '';
    document.getElementById('message-bottom').scrollIntoView(false);
    websocket.send(JSON.stringify(msg));
}
function addMessageItem(msg) {
    msg.time = new Date();
    if (msg.from === user.username) {
        document.getElementById("message-frame").innerHTML += mymsgitem.replace('content', msg.content);
    } else {
        document.getElementById("message-frame").innerHTML +=
            frimsgitem.replace('name', msg.from,).replace('content', msg.content);
    }
}