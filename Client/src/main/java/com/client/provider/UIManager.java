package com.client.provider;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.netty.channel.Channel;

public class UIManager {

    private Stage stage;
    private Skin skin;

    // UI components
    private TextField usernameField;
    private TextField passwordField;
    private TextButton loginButton;

    public UIManager() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("path/to/UI.ss"));

        // Initialize UI components
        usernameField = new TextField("", skin);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        loginButton = new TextButton("Login", skin);

        // Add components to the stage
        stage.addActor(usernameField);
        stage.addActor(passwordField);
        stage.addActor(loginButton);

        // Set up positions
        usernameField.setPosition(UIPositions.USERNAME_X, UIPositions.USERNAME_Y);
        passwordField.setPosition(UIPositions.PASSWORD_X, UIPositions.PASSWORD_Y);
        loginButton.setPosition(UIPositions.LOGIN_BUTTON_X, UIPositions.LOGIN_BUTTON_Y);
    }

    public void setLoginClickListener(ClickListener listener) {
        loginButton.addListener(listener);
    }

    public void addToStage(Stage stage) {
        stage.addActor(usernameField);
        stage.addActor(passwordField);
        stage.addActor(loginButton);
    }

    public void draw() {
        stage.act();
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public void sendAuthenticationMessage(Channel channel) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        AuthServerMessage message = new AuthServerMessage(AuthServerMessageType.LOGIN_REQUEST, username + ":" + password);
        channel.writeAndFlush(message);
    }
}