package com.cactus.qqclient.service;

import com.cactus.qqcommon.Message;
import com.cactus.qqcommon.MessageType;
import com.cactus.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by cactusli on 2022/11/11 17:36
 * 该类完成用户登录验证和用户注册等功能
 */
public class UserClientService {

    //因为我们可能在其他地方使用 user 信息，因此需要使用此成员属性
    private User u = new User();
    // 因为 socket 在其它地方也可能使用，因此做出属性
    private Socket socket;

    // 根据 userId 和 pwd 到服务器验证用户是否合法
    public boolean checkUser(String userId, String pwd) {
        boolean b = false;
        // 创建 User 对象
        u.setUserId(userId);
        u.setPassword(pwd);

        try{
            // 连接服务器, 发送user 对象
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            // 得到 ObjectOutputStream 对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u); // 发送User 对象

            // 读取从和服务器回复的 Message 对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) { //登录成功
                // 创建一个和服务器端保持通信的线程--> 创建一个类 ClientConnectioServerThread
                ClientConnectionServerThread clientConnectionServerThread = new ClientConnectionServerThread(socket);
                // 启动客户端的线程
                clientConnectionServerThread.start();
                // 这里为了后面客户端的扩展，我们将线程放入集合管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectionServerThread);

                // 打印或接收离线信息
                getOffLineMessage();
                b = true;

            } else {
                // 如果登录失败，我们就不能启动和服务器通信的线程
                socket.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public void getOffLineMessage() {
        // 发送一个 Message,类型 MESSAGE_GET_OFF_LINE_MES
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_OFF_LINE_MES);
        message.setSender(u.getUserId());
        message.setSendTime(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())); // 发送时间;
        try {
            // 得到当前线程 Socket 对应的 ObjectOutputStream
            ClientConnectionServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
            Socket socket1 = clientConnectServerThread.getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket1.getOutputStream());
            oos.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取在线用户列表
    public void onlienFriendList() {
        // 发送一个 Message,类型 MESSAGE_GET_ONLIEN_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        try {
            // 得到当前线程 Socket 对应的 ObjectOutputStream
            ClientConnectionServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
            Socket socket1 = clientConnectServerThread.getSocket();
            ObjectOutputStream oos = new ObjectOutputStream(socket1.getOutputStream());
            oos.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 编写方法，推出客户端，并给服务端发送一个退出系统的 message 对象
    public void logout() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());

        // 发送 message
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread
                    .getClientConnectServerThread(u.getUserId())
                    .getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + "退出系统");
            ManageClientConnectServerThread.removeServerConnectClientThread(u.getUserId());
            oos.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
