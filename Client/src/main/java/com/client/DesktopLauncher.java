package com.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.client.ui.LoginUI;
import com.utils.net.NetworkUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class DesktopLauncher {
    
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        // Set config settings here

        // Connect to the server
        String serverHost = "127.0.0.1"; // Replace with your server's host address
        int serverPort = 12345; // Replace with your server's port
        ChannelFuture channelFuture = NetworkUtils.connectToServer(serverHost, serverPort);
        channelFuture.syncUninterruptibly();
        Channel channel = channelFuture.channel();

        Runnable onLoginSuccess = () -> System.out.println("Logged in successfully");

        // Start the application
        new Lwjgl3Application((ApplicationListener) new LoginUI(channel, onLoginSuccess), config);
    }
}
