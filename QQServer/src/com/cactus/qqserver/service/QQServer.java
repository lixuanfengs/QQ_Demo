package com.cactus.qqserver.service;

import com.cactus.qqcommon.Message;
import com.cactus.qqcommon.MessageType;
import com.cactus.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这是服务器，在监听 9999 ， 等待客户端的连接，并保持通信
 */
public class QQServer {

    private ServerSocket serverSocket = null;

    // 创建一个集合，存放多个用户，如果是这些用户登录，就认为时合法的
    // 这里我们也可以使用 ConcurrentHashMap, 可以处理并发集合，没有线程安全
    // HashMap 没有处理线程安全，因此在多线程情况下是不安全
    // ConcurrentHashMap 处理线程安全，即线程同步处理，在多线程情况下是安全
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static { // 在静态代码块，初始化 validUsers
        validUsers.put("100", new User("100","123456"));
        validUsers.put("200", new User("200","123456"));
        validUsers.put("300", new User("300","123456"));
        validUsers.put("300", new User("300","123456"));
        validUsers.put("仙人球", new User("仙人球","123456"));
        validUsers.put("李喧锋", new User("李喧锋","123456"));
        validUsers.put("王碧清", new User("王碧清","123456"));
        validUsers.put("王翰崧", new User("王翰崧","123456"));
    }

    private boolean checkUser(String userId, String passwd) {

        User user = validUsers.get(userId);
        if (user == null) { // 说明 userId 没有存在 validUsers 的 key 中
            return false;
        }
        if (!user.getPassword().equals(passwd)) { // userId 正确，但是密码错误
            return false;
        }
        return true;
    }
    public QQServer() throws IOException {

        // 启动新闻推送线程
        new Thread(new SendNewsToAllService()).start();
        try {
            // 端口可以写在配置文件里。
            System.out.println("服务端在9999端口监听...");
            serverSocket = new ServerSocket(9999);
            while (true) {
                Socket socket = serverSocket.accept();
                // 得到socket 关联的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                // 得到socket 关联的对象输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                User u = (User)ois.readObject(); // 读取客户端发送的 User 对象
                // 创建一个Message 对象，准备回复客户端
                Message message = new Message();
                // 验证
                if (checkUser(u.getUserId(),u.getPassword())) { // 登录验证通过
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    // 将 message 对象回复给客户端
                    oos.writeObject(message);
                    // 创建一个线程， 和客户端保持通信,该线程需要持有 socket 对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    // 启动该线程
                    serverConnectClientThread.start();
                    // 该线程对象，放入到一个集合中，进行管理
                    ManageClientThread.addClientThread(u.getUserId(), serverConnectClientThread);
                    //ManageClientThread.eachAllServerConnectClientThread();
                } else { // 登录失败
                    System.out.println("用户 id=" + u.getUserId() + " pwd=" + u.getPassword() + " 验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭 socket
                    socket.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 如果服务器退出了 while , 说明服务器端不在监听，因此关闭 serverSocket
            serverSocket.close();
        }
    }
}
