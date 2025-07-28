package com.example.websocket;

import com.example.ssh.SshConnerction;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    SshConnerction sshConnerction;


    private ConcurrentHashMap<String, Session> sAnds = new ConcurrentHashMap<>();
    private ConcurrentHashMap<WebSocketSession, Session> wAnds = new ConcurrentHashMap<>();
    private ConcurrentHashMap<WebSocketSession, Channel>  wAndc= new ConcurrentHashMap<>();
    private ConcurrentHashMap<WebSocketSession, InputStream>  wAndb= new ConcurrentHashMap<>();
    // 连接建立时触发
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // 获取连接的clientId
        String substring = session.getUri().getQuery().substring(3);
        // 连接到服务器
        Session ses=sshConnerction.sshCannnection(substring);
        //创建通道
        Channel channel = ses.openChannel("shell");
        channel.connect();
        //添加到线程安全 方便复用
        wAnds.put(session, ses);
        wAndc.put(session,channel);

        InputStream commandOutput = channel.getInputStream();
        //添加到线程安全 方便复用
        wAndb.put(session,commandOutput);
        //一直往 session写数据
        sshConnerction.readOutput(session,commandOutput);
    }

    // 连接关闭时触发
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //关闭资源
        Session ses=wAnds.get(session);
        ses.disconnect();
        Channel channel=wAndc.get(session);
        channel.disconnect();
        InputStream reader=wAndb.remove(session);
        reader.close();
        //移除资源
        wAnds.remove(session);
        wAndc.remove(session);
        wAndb.remove(session);
        super.afterConnectionClosed(session, status);
    }

    // 收到消息时触发
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //获取当前websocket绑定的 通道
        Channel channel=wAndc.get(session) ;
        //向Session发送指令
        sshConnerction.order(channel,message.getPayload());

    }
}