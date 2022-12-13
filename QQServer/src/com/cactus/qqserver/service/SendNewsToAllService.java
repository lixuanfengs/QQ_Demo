package com.cactus.qqserver.service;

import com.cactus.qqcommon.Message;
import com.cactus.qqcommon.MessageType;
import com.cactus.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 服务端推送新闻类
 */
public class SendNewsToAllService implements Runnable {

    @Override
    public void run() {

        while (true) {
            System.out.println("请输入服务器要推送的新闻/消息 或者 【输入 `exit` 退出新闻推送！】");
            String news = Utility.readString(100);
            if ("exit".equals(news)) {
                break;
            }
            // 构建一个消息，群发消息
            Message message = new Message();
            message.setMesType(MessageType.MESSAGE_TO_ALL_COMM_MES);
            message.setSendTime(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
            message.setSender("服务器");
            message.setContent(news);
            System.out.println("服务器推送消息给所有人 说： " + news);

            // 遍历当前所有的通信线程，得到 socket, 并发送 message
            if (!ManageClientThread.getHm().isEmpty()) {
                ManageClientThread.getHm().entrySet().stream().forEach(m -> {
                    String onlineUserId = m.getKey();
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(ManageClientThread
                                .getServerConnectClientThread(onlineUserId)
                                .getSocket()
                                .getOutputStream());
                        oos.writeObject(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                System.out.println("目前没有在线的用户！");
            }

        }

    }
}
