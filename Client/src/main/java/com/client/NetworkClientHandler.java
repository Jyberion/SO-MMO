package com.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.client.Message;

public class NetworkClientHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connected to server");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        byte type = message.getType();
        byte[] payload = message.getPayload();

        switch (type) {
            case Message.TYPE_PLAYER_MOVE:
                // Handle player move message
                int x = payload[0];
                int y = payload[1];
                System.out.println("Player moved to: (" + x + ", " + y + ")");
                break;

            case Message.TYPE_PLAYER_ATTACK:
                // Handle player attack message
                int targetId = payload[0];
                System.out.println("Player attacked: " + targetId);
                break;

            case Message.TYPE_CHAT_MESSAGE:
                // Handle chat message
                String messageText = new String(payload);
                System.out.println("Chat message: " + messageText);
                break;

            default:
                System.out.println("Unknown message type: " + type);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Disconnected from server");
    }
}
