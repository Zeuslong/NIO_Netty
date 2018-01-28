package com.nio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Administrator on 2018/1/26.
 */
public class Server {
    private int port;
    public Server(int port) {
        this.port = port;
    }
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();//创建处理 连接的线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup();//创建处理所有 事件的线程池
        try {
            ServerBootstrap b = new ServerBootstrap();//设定  启动辅助类
            b.group(bossGroup, workerGroup)//【设置线程池】【先连接上了，再进行工作】先绑定 bossGroup，再绑定workerGroup
                    .channel(NioServerSocketChannel.class)//【socket工厂】指定连接该服务器的channel类型
                    .option(ChannelOption.SO_BACKLOG, 1024)//【设置bossGroup相关参数】
                    //【BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理
                    // 线程全满时，用于临时存放已经完成 三次握手 的请求的队列的最大长度。
                    // 未设置，或者所设置的值小于1，Java默认值为 50】
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//【设置workerGroup相关参数】
                    // 【是否启用心跳保活机制。
                    // 双方TCP套接字建立连接后（都进入ESTABLISHED状态）并且在两个小时左右上层没有任何
                    // 数据传输的情况下，就会激活该机制】
                    .childHandler(new ChannelInitializer<SocketChannel>(){//选择执行handler
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //添加各种功能handler消息加解密，消息规范检测，构建返回码
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();//阻塞等待服务器 完全启动
            System.out.println("服务器开启："+port);
            f.channel().closeFuture().sync();//同步等待服务器关闭信息
        } finally {
            //关闭最初开辟的两个 线程池
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 9090;
        }
        new Server(port).run();
    }
}
