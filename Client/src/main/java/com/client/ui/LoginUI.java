package com.client.ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.utils.net.NetworkUtils;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoginUI implements ApplicationListener {
    private Channel channel;
    private Runnable onLoginSuccess;
    private Stage stage;
    private List<String> serverList;
    private Skin skin;
    private Texture backgroundTexture;

    public LoginUI(Channel channel, Runnable onLoginSuccess) {
        this.channel = channel;
        this.onLoginSuccess = onLoginSuccess;
    }

    @Override
    public void create() {
        createUI();
        backgroundTexture = new Texture(Gdx.files.internal("login/BG/login.png"));
        scheduleWorldListUpdate();
    }

    private void createUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(); // Load your skin here

        serverList = new List<>(skin);

        stage.addActor(serverList);
    }

    private void scheduleWorldListUpdate() {
        Timer timer = new Timer(true);
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    Map<String, Boolean> worldStatus = NetworkUtils.getWorldStatusFromLoginServer();
                    updateWorldList(worldStatus);
                });
            }
        };
        timer.scheduleAtFixedRate(updateTask, 60000, 60000);
    }

    public void updateWorldList(Map<String, Boolean> worldStatus) {
        String[] items = worldStatus.entrySet().stream()
                .map(entry -> entry.getKey() + " - " + (entry.getValue() ? "Online" : "Offline"))
                .toArray(String[]::new);

        serverList.setItems(items);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void show() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
