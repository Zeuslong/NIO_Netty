package com.nio.netty;

import com.utils.Calculator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/1/26.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf in = (ByteBuf) msg;
        byte[] req = new byte[in.readableBytes()];
        in.readBytes(req);
        String body = new String(req,"utf-8");
        System.out.println("收到客户端消息:"+body);
        String calrResult = null;
        try{
            calrResult = Calculator.Instance.cal(body).toString();
        }catch(Exception e){
            calrResult = "错误的表达式：" + e.getMessage();
        }
        //将服务器应答写入  通道读中。
        ctx.write(Unpooled.copiedBuffer(calrResult.getBytes()));
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//刷新通道的读--->客户端可以通过 通道读 获取客户端回复的信息
    }
    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();//关闭通道处理器文本
    }
}
