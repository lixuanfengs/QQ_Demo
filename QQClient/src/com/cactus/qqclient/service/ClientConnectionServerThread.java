package com.cactus.qqclient.service;

import com.cactus.qqcommon.Message;
import com.cactus.qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cactusli on 2022/11/21 11:15
 */
public class ClientConnectionServerThread extends Thread {

    private Socket socket;

    // 构造器可以接受一个 Socket 对象
    public ClientConnectionServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        // 因为Thread 需要在后台服务器通信，因此我们 while 循环
        while (true) {
            try {
                System.out.println("客户端线程，等待读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务器没有发送Message 对象，线程会阻塞在这里
                Message message = (Message)ois.readObject();
                // 判断这个 message 类型，然后做相应的业务处理
                // 如果是读取到的是 服务端返回的在线用户列表
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {

                    // 取出在线列表信息 显示 数据形势规定用空格分隔
                    String[] onlienUsers = message.getContent().split(" ");
                    System.out.println("\n ======当前在线用户列表======");
                    Arrays.stream(onlienUsers).forEach( user -> {
                        System.out.println("用户：" + user);
                    });

                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_COMM_MES)) {

                    // 把从服务器转发的消息，显示控制台即可
                    System.out.println("\n" + message.getSendTime()+ ": " +
                            message.getSender() + " 对大家说: " + message.getContent());

                } else if (message.getMesType().equals(MessageType.MESSAGE_RET_OFF_LINE_MES)) {

                    // 把从服务器转发的消息，显示控制台即可
                    List<Message> messageList = message.getMessageList();
                    if (messageList !=null && messageList.size() > 0) {
                        messageList.stream().forEach( p -> {
                            if (p.getContent() == null) {
                                System.out.println("\n" + "离线消息*：" + p.getSendTime() + " : " + p.getSender() + " 给 " + p.getGetter() +
                                        " 发送文件: " + p.getSrc() + " 到本机磁盘目录： " + p.getDest());
                                // 取出 message 里的字节数组，通过文件输出流写入到磁盘
                                try {
                                    FileOutputStream out = new FileOutputStream(p.getDest());
                                    out.write(p.getFileBytes());
                                    out.close();
                                    System.out.println("\n" + " 文件保存成功！！~~");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("\n"  + " 离线消息*："+ p.getSendTime()+ ": " +
                                        p.getSender() + " 对 " + p.getGetter()
                                        + " 说: " + p.getContent());
                            }

                        });
                    }

                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {

                    // 把从服务器转发的消息，显示控制台即可
                    System.out.println("\n" + message.getSendTime()+ ": " +
                            message.getSender() + " 对 " + message.getGetter()
                    + " 说: " + message.getContent());

                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {

                    System.out.println("\n" + message.getSendTime() + " : " + message.getSender() + " 给 " + message.getGetter() +
                            " 发送文件: " + message.getSrc() + " 到本机磁盘目录： " + message.getDest());

                    // 取出 message 里的字节数组，通过文件输出流写入到磁盘
                    FileOutputStream out = new FileOutputStream(message.getDest());
                    out.write(message.getFileBytes());
                    out.close();
                    System.out.println("\n" + " 文件保存成功！！~~");

                } else {
                    System.out.println("是其他类型的 message, 暂时不处理...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // 为了更方便的得到Socket
    public Socket getSocket() {
        return socket;
    }
}
