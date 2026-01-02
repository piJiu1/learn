package com.example.redisdemo.config;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint("/chat")
public class ChatServer {
    @OnOpen
    public void onOpen(Session session){
        System.out.println("客户端已连接:  " + session.getId());
    }
    @OnMessage
    public String onMessage(String message,Session session) throws IOException {
        System.out.println("收到消息："+message);
        session.getBasicRemote().sendText("服务器回显:"+message);
        return "服务器收到："+message;
    }
    @OnClose
    public void onClose(Session session){
        System.out.println("客户端断开连接："+session.getId());
    }
    @OnError
    public void onError(Throwable error){
        error.printStackTrace();
    }
}
