package com.cactus.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 该类用于管理和客户端通信的线程
 */
public class ManageClientThread {

    private static ConcurrentHashMap<String, ServerConnectClientThread> hm = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    // 添加线程对象到 hm 集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    // 根据 userId 返回 ServerConnectClientThread 线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }

    // 查看管理的线程
    public static void eachAllServerConnectClientThread() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("目前管理的线程：");
        hm.forEach((key,value) -> {
            System.out.println( key+ ":" + value);
        });
    }

    public static void removeServerConnectClientThread(String userId) {
        hm.remove(userId);
    }

    // 返回在线用户列表
    public static String getOnlineUser() {
        // 集合遍历 hashMap 的 key
        Iterator<String> iterator = hm.keySet().iterator();
        String onlienUser = "";
        while (iterator.hasNext()) {
           onlienUser += iterator.next().toString() + " ";
        }
        return onlienUser;
    }

}
