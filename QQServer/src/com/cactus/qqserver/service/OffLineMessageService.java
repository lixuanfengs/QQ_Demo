package com.cactus.qqserver.service;

import com.cactus.qqcommon.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放离线消息集合
 */
public class OffLineMessageService {

    // 存放离线消息的集合
    private static ConcurrentHashMap<String, List<Message>> offLineMessage = new ConcurrentHashMap<>();



    // 新增离线消息
    public static void addOffLineMessage(String userId, List<Message> messages) {
        offLineMessage.put(userId, messages);
    }

    // 根据 userId 删除离线消息
    public static void removeOffLineMessage(String userId) {
        offLineMessage.remove(userId);
    }

    // 根据 userId 获取离线信息
    public static List<Message> getOffLineMessage(String userId) {
        List<Message> messages = offLineMessage.get(userId);
        return messages;
    }

    // 存放离线消息的业务逻辑实现
    public static void saveOffLineMessage(Message message) throws CloneNotSupportedException {

        List<Message> offLineMessage = OffLineMessageService.getOffLineMessage(message.getGetter());
        if (offLineMessage !=null && offLineMessage.size() > 0) {
            offLineMessage.add(message);
        } else {
            offLineMessage = new ArrayList<>();
            offLineMessage.add(message);
        }
        OffLineMessageService.addOffLineMessage(message.getGetter(),offLineMessage);

    }
}
