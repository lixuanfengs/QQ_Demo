package com.cactus.qqclient.service;

import java.util.HashMap;

/**
 * 该客户端连接到服务器的线程的类
 */
public class ManageClientConnectServerThread {

     // 我们把多个线程放入一个 HashMap 集合， key 就是用户id， value 就是线程
     private static HashMap<String, ClientConnectionServerThread> hm = new HashMap<>();

     // 将某个线程加入到集合
     public static void addClientConnectServerThread(String userId, ClientConnectionServerThread clientConnectionServerThread) {
        hm.put(userId, clientConnectionServerThread) ;
     }

     // 根据id 删除某个线程
     public static void removeServerConnectClientThread(String userId) {
        hm.remove(userId);
     }

     //通过 userId 可以得到对应线程
     public static ClientConnectionServerThread getClientConnectServerThread(String userId) {
         return hm.get(userId);
     }

}
