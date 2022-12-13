package com.cactus.qqcommon;

/**
 * Created by cactusli on 2022/11/11 14:49
 * 表示消息类型
 */
public interface MessageType {
    //1.在接口中定义一些常量
    //2.不同的常量值，表示不同的消息类型
    String MESSAGE_LOGIN_SUCCEED = "1"; //表示登陆成功
    String MESSAGE_LOGIN_FAIL = "2"; //表示登录失败
    String MESSAGE_COMM_MES = "3"; // 普通信息包
    String MESSAGE_GET_ONLINE_FRIEND = "4"; // 要求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5"; // 要求返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6"; // 客户端请求退出
    String MESSAGE_TO_ALL_COMM_MES = "7"; // 群发信息包
    String MESSAGE_FILE_MES = "8"; // 发送文件信息
    String MESSAGE_GET_OFF_LINE_MES = "9"; // 获取返回离线信息
    String MESSAGE_RET_OFF_LINE_MES = "10"; // 接收离线信息
    String MESSAGE_OFF_LINE_FILE_MES = "11"; // 接收离线文件



}
