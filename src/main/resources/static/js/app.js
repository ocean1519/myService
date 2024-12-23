// 创建一个新的 EventSource 对象，指向你的 SSE 端点
const eventSource = new EventSource('http://192.168.83.51:8080/connect/' + Date.now());

// 当成功建立连接时触发
eventSource.onopen = function(event) {
    console.log('Connection to server opened.');
};

// 接收消息时触发
eventSource.onmessage = function(event) {
    const data = JSON.parse(event.data);
    displayEvent(data);
};

// 处理特定类型的事件 (如果服务器发送了带有 type 的事件)
eventSource.addEventListener('custom-event', function(event) {
    const data = JSON.parse(event.data);
    console.log('Received custom event:', data);
    // 根据需要处理特定类型的消息
});

// 如果连接关闭或出现错误时触发
eventSource.onerror = function(error) {
    console.error('EventSource failed:', error);
    // 可选择在此处尝试重新连接
};

// 显示接收到的事件
function displayEvent(data) {
    console.log(data)
    const ul = document.getElementById('event-list');
    const li = document.createElement('li');
    li.textContent = `Message at ` + JSON.stringify((data));
    ul.insertBefore(li, ul.firstChild);
}

document.addEventListener('DOMContentLoaded', (event) => {
    const button = document.getElementById('requestButton');

    if (button) {
        button.addEventListener('click', function() {
            console.log('Button clicked, initiating fetch...');

            fetch('http://192.168.83.51:8080/redis')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok ' + response.statusText);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Data received:', data);
                    displayEvent(data);
                })
                .catch(error => {
                    console.error('Error occurred during fetch:', error);
                });
        });
    } else {
        console.error('Button with id "requestButton" not found.');
    }
});