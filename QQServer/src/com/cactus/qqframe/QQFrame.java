package com.cactus.qqframe;

import com.cactus.qqserver.service.QQServer;

import java.io.IOException;

/**
 * 启动服务端的主程序
 */
public class QQFrame {

    public static void main(String[] args) throws IOException {
        new QQServer();
    }
}
