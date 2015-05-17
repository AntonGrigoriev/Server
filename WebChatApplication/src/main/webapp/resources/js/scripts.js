var place = null;
var server = true;
var isShift = false;
var clientId;
var mesbox;
var Login;
var textField;
var Id = function () {
    var date = new Date().getTime();
    var random = Math.random() * Math.random();
    return Math.floor(date * random).toString();
};
var mStruct = function (name, text, time, id, cId, info) {
    return {
        cId: cId,
        id: id,
        time: time,
        name: name,
        message: text,
        info: info
    };
};
var mainUrl = 'chat';
var token = 'TE11EN';

function run() {
    clientId = Id();
    document.getElementsByClassName('infobar1')[0].value = 'Server: ON';
    Login = document.getElementsByClassName('login')[0];
    Login.value = localStorage.getItem('login');
    if (Login.value === '') {
        Login.value = 'User';
    }
    mesbox = document.getElementsByClassName('messageslist')[0];
    textField = document.getElementById('todoMes');
    var appContainer = document.getElementsByClassName('wrapper')[0];
    var loginForm = document.getElementById('login');

    appContainer.addEventListener('click', delegateEvent);
    textField.addEventListener('keydown', onKeyClick);
    textField.addEventListener('keyup', onKeyUnClick);
    loginForm.addEventListener('keydown', saveLogin);

    clientSwitch(true);
    restoreHistory();
}

function delegateEvent(evtObj) {
    if (server) {
        if (evtObj.type === 'click' && evtObj.target.classList.contains('sender')) {
            onSendButtonClick(evtObj);
        }
        if (evtObj.type === 'click' && evtObj.target.classList.contains('lsavebut')) {
            saveLogin(evtObj);
        }
        if (evtObj.type === 'click' && evtObj.target.classList.contains('leditbut')) {
            editLogin(evtObj);
        }
        if (evtObj.type === 'click' && evtObj.target.classList.contains('delete')) {
            onDeleteButtonClick(evtObj);
        }
        if (evtObj.type === 'click' && evtObj.target.classList.contains('edit')) {
            onEditButtonClick(evtObj);
        }
    }
}

function editLogin() {
    document.getElementById('login').value = Login.value;
    document.getElementById('login').focus();
}

function saveLogin(evtObj) {
    if (evtObj.keyCode === 13 || evtObj.keyCode === 0) {
        var logIn = document.getElementById('login');
        if (!logIn.value) {
            return;
        }
        if (Login.value !== logIn.value) {
            sendSystemMessage(Login.value + " change name to " + logIn.value);
            Login.value = logIn.value;
        }
        logIn.value = '';
        mesbox.scrollTop = mesbox.scrollHeight;
    }
    localStorage.setItem('login', Login.value);
}

function onSendButtonClick() {
    if (textField.value === '') {
        if (place !== null) {
            place.classList.remove('sys');
            place = null;
        }
        return;
    }
    var msg = mStruct(Login.value, normalizeText(textField.value), '', Id(), clientId, '');
    if (place === null) {
        doPost(mainUrl, msg);
        textField.value = '';
        textField.focus();
    } else {
        msg.info = 'edit';
        msg.id = place.attributes['message-id'].value;
        doPut(mainUrl, msg);
        textField.value = '';
        textField.focus();
        place.classList.remove('sys');
        place = null;
    }
}

function sendMessage(msg, isMine, isHistory) {
    var item = createItem(msg, isMine);
    if (isHistory) {
        item.childNodes[1].classList.add('sys');
    }
    mesbox.appendChild(item);
    mesbox.scrollTop = mesbox.scrollHeight;
}

function createItem(msg, isMine) {
    var message = document.createElement('div');
    message.classList.add('message');
    var string = '<table><tr>';
    if (isMine) {
        string += '<td class=\'itd\'><img src=\'resources/img/pencil.png\' class=\'edit\'></td>';
        string += '<td class=\'itd\'><img src=\'resources/img/trashcan.png\' class=\'delete\'></td>';
    }
    string += '<td class=\'time\'>' + msg.time + '</td>';
    string += '<td class=\'ntd\'>' + msg.name + ': </td>';
    string += '<td><textarea class=\'minfo\' readonly>' + msg.info + '</textarea></td></tr></table>';
    string += '<textarea class=\'mes\' message-id=\'' + msg.id + '\' readonly>' + msg.message + '</textarea>';
    message.innerHTML = string;
    return message;
}

function changeMessage(msg, isMine) {
    var mes;
    var messages = document.getElementsByClassName('mes');
    for (var i = 0; i < messages.length; i++) {
        if (messages[i].attributes['message-id'].value === msg.id) {
            mes = messages[i];
            break;
        }
    }
    if (msg.message !== mes.value) {
        var index = 2;
        if (isMine) {
            index = 4;
        }
        var info = '<edited at ' + msg.time + '>';
        mes.value = msg.message;
        mes.parentNode.firstChild.firstChild.firstChild.childNodes[index].firstChild.value = info;
    }
    mes.style.height = '0px';
    mes.style.height = mes.scrollHeight + 'px';
}

function onEditButtonClick(evtObj) {
    if (place !== null) {
        place.classList.remove('sys');
    }
    var mes = evtObj.target.parentNode.parentNode.parentNode.parentNode.parentNode.childNodes[1];
    if (mes !== place) {
        mes.classList.add('sys');
        textField.value = mes.value;
        place = mes;
    } else {
        textField.value = '';
        mes.classList.remove('sys');
        place = null;
    }
    textField.focus();
}

function onDeleteButtonClick(evtObj) {
    var delPlace = evtObj.target.parentNode.parentNode.parentNode.parentNode.parentNode;
    var msg = mStruct(Login.value, '', '', delPlace.childNodes[1].attributes['message-id'].value, clientId, 'delete');
    doDelete(mainUrl, msg);
}

function deleteMessage(msg, isMine) {
    var mes;
    var messages = document.getElementsByClassName('mes');
    for (var i = 0; i < messages.length; i++) {
        if (messages[i].attributes['message-id'].value === msg.id) {
            mes = messages[i].parentNode;
            break;
        }
    }
    var info = '<deleted at ' + msg.time + '>';
    if (isMine) {
        mes.firstChild.firstChild.firstChild.removeChild(mes.firstChild.firstChild.firstChild.firstChild);
        mes.firstChild.firstChild.firstChild.removeChild(mes.firstChild.firstChild.firstChild.firstChild);
        textField.focus();
    }
    mes.removeChild(mes.childNodes[1]);
    mes.firstChild.firstChild.firstChild.childNodes[2].firstChild.value = info;
}

function sendSystemMessage(text) {
    var msg = mStruct(Login.value, text, '', '', clientId, 'system');
    doPost(mainUrl, msg);
}

function printSystemMessage(text) {
    var mes = document.createElement('div');
    var txt = document.createTextNode(text);
    mes.classList.add('system');
    mes.appendChild(txt);
    mesbox.appendChild(mes);
    mesbox.scrollTop = mesbox.scrollHeight;
}

function clientSwitch(isStart) {
    infobar = document.getElementsByClassName('infobar1')[0];
    document.getElementsByClassName('msg')[0].disabled = server;
    document.getElementsByClassName('login-form')[0].disabled = server;
    if (server) {
        infobar.value = 'Server: OFF';
        if (!isStart)
            printSystemMessage('Server disconnected', false);
    } else {
        infobar.value = 'Server: ON';
        if (!isStart)
            printSystemMessage('Server connected', false);
        textField.focus();
    }
    server = !server;
    mesbox.scrollTop = mesbox.scrollHeight;
}

function onKeyClick(evtObj) {
    if (evtObj.keyCode === 13) {
        if (!isShift) {
            onSendButtonClick(evtObj);
        } else {
            if (textField.value !== '') {
                textField.value = textField.value + '\r\n';
            }
            textField.scrollTop = textField.scrollHeight;
        }
        evtObj.preventDefault();
    }
    if (evtObj.keyCode === 16) {
        isShift = true;
    }
}

function onKeyUnClick(evtObj) {
    if (evtObj.keyCode === 16) {
        isShift = false;
    }
}

function normalizeText(text) {
    while (text !== text.replace("\\n", "\n")) {
        text = text.replace("\\n", "\n");
    }
    var arr = text.split('\n');
    text = arr[0];
    for (var i = 1; i < arr.length; i++) {
        if (arr[i] !== '') {
            text += '\n' + arr[i];
        }
    }
    return text;
}

function restoreHistory() {
    var url = mainUrl + '?token=' + token;
    doGet(url, function (responseText) {
        if (!server) {
            clientSwitch();
        }
        var response = JSON.parse(responseText);
        token = response.token;
        addMessages(response.messages, true);
        setTimeout(listenMessages, 1000);
    }, function (error) {
        if (error !== "Server disconnected") {
            if (!server) {
                clientSwitch();
            }
        }
        defaultErrorHandler(error);
        setTimeout(listenMessages, 1000);
    });
}

function listenMessages() {
    function loop() {
        var url = mainUrl + '?token=' + token;
        doGet(url, function (responseText) {
            if (!server) {
                clientSwitch();
            }
            if (responseText !== "") {
                var response = JSON.parse(responseText);
                token = response.token;
                addMessages(response.messages, false);
            }
            setTimeout(loop, 1000);
        }, function (error) {
            if (error === "Server disconnected") {
                if (server) {
                    clientSwitch();
                }
            } else {
                if (!server) {
                    clientSwitch();
                }
            }
            defaultErrorHandler(error);
            setTimeout(loop, 1000);
        });
    }

    loop();
}

function doGet(url, continueWith, continueWithError) {
    ajax('GET', url, null, continueWith, continueWithError);
}

function doPost(url, data, continueWith, continueWithError) {
    while (data.message !== data.message.replace("\n", "\\n")) {
        data.message = data.message.replace("\n", "\\n");
    }
    ajax('POST', url, JSON.stringify(data), continueWith, continueWithError);
}

function doPut(url, data, continueWith, continueWithError) {
    while (data.message !== data.message.replace("\n", "\\n")) {
        data.message = data.message.replace("\n", "\\n");
    }
    ajax('PUT', url, JSON.stringify(data), continueWith, continueWithError);
}

function doDelete(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, JSON.stringify(data), continueWith, continueWithError);
}

function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();
    continueWith = continueWith || function () {
    };
    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);
    xhr.onload = function () {
        if (xhr.readyState !== 4) {
            return;
        }
        if (xhr.status !== 200 && xhr.status !== 304) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }
        if (isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }
        console.log("Server response: " + xhr.status + " " + xhr.responseText);
        continueWith(xhr.responseText);
    };
    xhr.ontimeout = function () {
        continueWithError('Server timed out!');
    };
    xhr.onerror = function (e) {
        continueWithError('Server disconnected');
    };
    xhr.send(data);
}

function defaultErrorHandler(message) {
    console.log("Error: " + message);
}

function addMessages(messages, isHistory) {
    for (var i = 0; i < messages.length; i++) {
        pushMessage(messages[i], isHistory);
    }
    if (isHistory) {
        mesbox.appendChild(document.createElement('hr'));
        mesbox.scrollTop = mesbox.scrollHeight;
    }
}

function pushMessage(mes, isHistory) {
    mes.message = normalizeText(mes.message);
    var isMine = (mes.clientId === clientId);
    switch (mes.info) {
        case "edit":
            changeMessage(mes, isMine);
            break;
        case "delete":
            deleteMessage(mes, isMine);
            break;
        case "system":
            printSystemMessage(mes.message);
            break;
        default:
            sendMessage(mes, isMine, isHistory);
            break;
    }
}

function isError(text) {
    if (text === "")
        return false;
    try {
        var obj = JSON.parse(text);
    } catch (ex) {
        return true;
    }
    return !!obj.error;
}

window.onerror = function (err) {
    defaultErrorHandler(err.toString());
};