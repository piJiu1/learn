package com.example.redisdemo.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;

//@WebListener
//public class WebSocketInitializer implements ServletContextListener {
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//        try{
//            ServerContainer container = (ServerContainer) sce.getServletContext().getAttribute("jakarta.websocket.server.ServerContainer");
//            container.addEndpoint(ServerEndpointConfig.Builder.create(ServerEndpointConfig.class, "/ws/manual").build());
//        }catch (Exception e){
//            throw new RuntimeException("Failed to register WebSocket endpoint", e);
//        }
//    }
//}
