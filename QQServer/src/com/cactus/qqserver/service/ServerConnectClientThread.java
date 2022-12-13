package com.cactus.qqserver.service;

import com.cactus.qqcommon.Message;
import com.cactus.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * qq 服务端通信线程类
 */
public class ServerConnectClientThread extends Thread {

    private Socket socket;
    private String userId; // 连接到服务器端的用户id

    //

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() { // 这里线程处于 run 状态，可以发送/接收消息
       while (true) {
           try {
               System.out.println("服务端和客户端 "+ userId +" 保持通信，读取数据...");
               ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
               Message message = (Message) ois.readObject();

               // 后面会使用 message, 根据 message 的类型,做相应的业务处理
               if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                   // 客户端在线用户列表
                   System.out.println(message.getSender() + " 要在线用户列表");
                   String onlineUser = ManageClientThread.getOnlineUser();
                   // 返回一个 Message 对象，返回给客户端
                   Message messageOnlienUser = new Message();
                   messageOnlienUser.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                   messageOnlienUser.setContent(onlineUser);
                   messageOnlienUser.setGetter(messageOnlienUser.getSender());
                   // 写入用户列表发送给客户端
                   ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                   oos.writeObject(messageOnlienUser);

               } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_COMM_MES)) {
                   // 遍历管理线程的集合，把所有线程的 socket 得到，然后把 message 进行转发
                   ManageClientThread.getHm().entrySet().stream().forEach( m -> {
                        // 取出在线用户的 id
                       String onlineUserId = m.getKey();
                       if (!onlineUserId.equals(message.getSender())) {// 排除群发消息的这个用户
                           try {
                               ObjectOutputStream oos = new ObjectOutputStream(ManageClientThread
                                       .getServerConnectClientThread(onlineUserId)
                                       .getSocket()
                                       .getOutputStream());
                               oos.writeObject(message);
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   });

               } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {

                   // 根据 message 获取 getterId, 然后在得到对应线程 转发文件
                   // 根据 message 获取 getterId, 然后在得到对应线程
                   // 得到对应的 socket 对象输出流，将 message 对象转发给指定客户端
                   ServerConnectClientThread scct = ManageClientThread
                           .getServerConnectClientThread(message.getGetter());
                   if (scct != null) {
                       ObjectOutputStream oos = new ObjectOutputStream(scct
                               .getSocket()
                               .getOutputStream());
                       oos.writeObject(message);
                   } else {
                       // 消息存放到离线消息集合里
                       OffLineMessageService.saveOffLineMessage(message);
                       Message cloneMessage = message.clone();
                       cloneMessage.setContent("用户不线，消息已存离线，待用户上线后即可看到您的消息");
                       cloneMessage.setFileBytes("".getBytes());
                       cloneMessage.setMesType(MessageType.MESSAGE_COMM_MES);
                       ObjectOutputStream oos = new ObjectOutputStream(ManageClientThread
                               .getServerConnectClientThread(message.getSender())
                               .getSocket()
                               .getOutputStream());
                       oos.writeObject(cloneMessage);
                   }

               } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                   // 根据 message 获取 getterId, 然后在得到对应线程
                   // 得到对应的 socket 对象输出流，将 message 对象转发给指定客户端
                   ServerConnectClientThread scct = ManageClientThread
                           .getServerConnectClientThread(message.getGetter());
                   if (scct != null) {
                       ObjectOutputStream oos = new ObjectOutputStream(scct
                               .getSocket()
                               .getOutputStream());
                       oos.writeObject(message);
                   } else {
                       // 消息存放到离线消息集合里
                       OffLineMessageService.saveOffLineMessage(message);
                       Message cloneMessage = message.clone();
                       cloneMessage.setContent("用户不线，消息已存离线，待用户上线后即可看到您的消息");
                       ObjectOutputStream oos = new ObjectOutputStream(ManageClientThread
                               .getServerConnectClientThread(message.getSender())
                               .getSocket()
                               .getOutputStream());
                       oos.writeObject(cloneMessage);
                   }

               } else if (message.getMesType().equals(MessageType.MESSAGE_GET_OFF_LINE_MES)) {
                   // 个根据用户id 返回离线集合中关于登陆用户的离线消息
                   String senderId = message.getSender();
                   List<Message> offLineMessages = OffLineMessageService.getOffLineMessage(senderId);
                   if (offLineMessages !=null && offLineMessages.size() > 0) {
                       ObjectOutputStream oos = new ObjectOutputStream(ManageClientThread
                               .getServerConnectClientThread(senderId)
                               .getSocket()
                               .getOutputStream());
                       Message offLibemessage = new Message();
                       offLibemessage.setMesType(MessageType.MESSAGE_RET_OFF_LINE_MES);
                       offLibemessage.setMessageList(offLineMessages);
                       oos.writeObject(offLibemessage);
                   }
                   // 然后在删除离线消息集合中关于登录用户的离线信息
                   OffLineMessageService.removeOffLineMessage(senderId);

               } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {

                   System.out.println(message.getSender() + " 退出~");
                   // 将这个客户端对应线程，从集合删除
                   ManageClientThread.removeServerConnectClientThread(userId);
                   socket.close(); // 关闭socket

                   break; // 退出 while循环 结束此线程
               } else {
                   System.out.println("其他类型的 message, 暂时不做处理");
               }

           } catch (Exception e) {
              e.printStackTrace();
           }
       }
    }

    // 方便获取 socket
    public Socket getSocket() {
        return socket;
    }
}
