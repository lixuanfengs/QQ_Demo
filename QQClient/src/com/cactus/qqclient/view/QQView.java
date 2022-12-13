package com.cactus.qqclient.view;

import com.cactus.qqclient.service.FileClientService;
import com.cactus.qqclient.service.MessageClientService;
import com.cactus.qqclient.service.UserClientService;
import com.cactus.qqclient.utils.Utility;
import com.cactus.qqcommon.Message;

/**
 * Created by cactusli on 2022/11/11 15:35
 * 客户端的菜单界面
 */
public class QQView {

    private boolean loop = true; //控制是否显示菜单
    private String key = ""; //接收用户的键盘输入
    private UserClientService userClientService = new UserClientService(); // 对象用于登录服务、注册用户
    private MessageClientService messageClientService = new MessageClientService(); // 对象用发消息

    private FileClientService fileClientService = new FileClientService();  // 对象用户发送文件

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统...");
    }

    //显示主菜单
    private void mainMenu() {
        while (loop) {
            System.out.println("==========欢迎登录网络通讯系统==========");
            System.out.println("\t\t 1 登陆系统");
            System.out.println("\t\t 9 推出系统");
            System.out.println("请输入你的选择：");
            key = Utility.readString(1);
            //根据用户输入来处理不同的逻辑
            switch (key) {
                case "1":
                    System.out.println("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.println("请输入密  码：");
                    String pwd = Utility.readString(50);
                    // 这里还有很多校验代码
                    if (userClientService.checkUser(userId,pwd)) {  //先把逻辑打通
                        System.out.println("==========欢迎（用户 "+userId+" 登录成功）==========");
                        //进入二级菜单
                        while (loop) {
                            System.out.println("\n==========网络通信系统二级菜单（用户"+userId+"）==========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            String key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    System.out.println("显示在线用户列表~~~");
                                    userClientService.onlienFriendList();
                                    break;
                                case "2":
                                    System.out.println("请输入群发内容：");
                                    String contents = Utility.readString(100);
                                    // 调用一个方法，将信息封装成 message 对象，发给服务器端
                                    messageClientService.sendMessageToAll(contents, userId);
                                    break;
                                case "3":
                                    System.out.println("请输入想聊天的用户号（在线）：");
                                    String getterId = Utility.readString(50);
                                    System.out.println("请输入聊天内容：");
                                    String content = Utility.readString(100);
                                    // 编写一个方法，将消息发送给服务器端
                                    messageClientService.sendMessageToOne(content, userId, getterId);
                                    break;
                                case "4":
                                    System.out.println("请输入文件接收的用户号（在线）：");
                                    String getterIdFile = Utility.readString(50);
                                    System.out.println("请输入文件路径：");
                                    String filePath = Utility.readString(1000);
                                    System.out.println("请输入文件存放路径：");
                                    String fileSavePath = Utility.readString(1000);
                                    fileClientService.sendFileToOne(filePath, fileSavePath, userId, getterIdFile);
                                    break;
                                case "9":
                                    // 调用方法，给服务器发送一个退出系统的 message
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    } else { //登陆服务器失败
                        System.out.println("==========登录失败==========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
