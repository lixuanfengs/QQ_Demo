package com.cactus.qqclient.service;

import com.cactus.qqcommon.Message;
import com.cactus.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 实现用户发送消息类
 */
public class MessageClientService {

    /**
     *
     * @param content 群发内容
     * @param senderId 发送者的 id
     */
    public void sendMessageToAll(String content, String senderId) {
        // 构建 message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_COMM_MES);
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())); // 发送时间
        System.out.println(message.getSendTime()+ ": " + senderId + " 对大家说 " + content);
        // 发送 message 到服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread
                    .getClientConnectServerThread(senderId)
                    .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param content 输入的聊天内容
     * @param senderId 发送用户的 id
     * @param getterId 接收用户的 id
     */
    public void sendMessageToOne(String content,String senderId,String getterId) {
        // 构建 message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())); // 发送时间
        System.out.println(message.getSendTime()+ ": " + senderId + " 对 " + getterId + " 说 " + content);
        // 发送 message 到服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread
                    .getClientConnectServerThread(senderId)
                    .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
