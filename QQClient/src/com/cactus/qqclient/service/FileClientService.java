package com.cactus.qqclient.service;

import com.cactus.qqcommon.Message;
import com.cactus.qqcommon.MessageType;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 该类/对象完成文件传输服务
 */
public class FileClientService {

    /**
     * @param src 源文件
     * @param dest 把该文件传输到对方的那个磁盘路径下
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId) {
        // 读取 src 文件 --》 message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSendTime(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())); // 发送时间
        message.setSrc(src);
        message.setDest(dest);

        // 需要将文件读取到客户端程序
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭文件流
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 提示信息
        System.out.println("\n" + message.getSendTime() + " : "+ senderId + " 给 " + getterId + " 发送文件： "+ src
                + " 到对方的电脑磁盘目录 " + dest);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread
                    .getClientConnectServerThread(senderId)
                    .getSocket()
                    .getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
