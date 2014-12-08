package be.teamsheepy.sheepherder.screens;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.SheepHerder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends Screen {

    private Stage stage = new Stage();
    private Skin skin = new Skin();

    public LoginScreen() {
        SheepHerder.analytics.trackPageView("login");

        skin.add(
                "background",
                new NinePatch(Assets.textfield, 30, 30, 20, 20));
        skin.add(
                "background-wrong",
                new NinePatch(Assets.textfieldWrong, 30, 30, 20, 20));
        skin.add(
                "button-up",
                new NinePatch(Assets.buttonUp, 30, 30, 20, 20));
        skin.add(
                "button-down",
                new NinePatch(Assets.buttonDown, 30, 30, 20, 20));
        skin.add("cursor", new NinePatch(Assets.cursor, 0, 0, 0, 0));

        Gdx.input.setInputProcessor(stage);

        Table table = new Table(skin);

        table.setFillParent(true);
        table.center();


        final TextField.TextFieldStyle tStyle = new TextField.TextFieldStyle();
        tStyle.font = Assets.font22; //here i get the font
        tStyle.fontColor = Color.BLACK;
        tStyle.background = skin.getDrawable("background");
        tStyle.cursor = skin.getDrawable("cursor");
        tStyle.cursor.setMinWidth(2f); //set cursor width
        tStyle.selection = skin.newDrawable("cursor", 0.5f, 0.5f, 0.5f, 0.5f);
        final TextField.TextFieldStyle twStyle = new TextField.TextFieldStyle();
        twStyle.font = Assets.font22; //here i get the font
        twStyle.fontColor = Color.BLACK;
        twStyle.cursor = skin.getDrawable("cursor");
        twStyle.cursor.setMinWidth(2f); //set cursor width
        twStyle.selection = skin.newDrawable("cursor", 0.5f, 0.5f, 0.5f, 0.5f);
        twStyle.background = skin.getDrawable("background-wrong");
        
        final Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = Assets.font28;
        lStyle.fontColor = Color.BLACK;
       // lStyle.background = skin.getDrawable("background");
        
        Label loginLabel = new Label("Login", lStyle);

        final TextField userField = new TextField("", tStyle);
        userField.setText(SavedData.userName);

        final TextField passField = new TextField("", tStyle);
        passField.setMessageText("password");
        passField.setPasswordCharacter('*');
        passField.setPasswordMode(true);

        userField.setTextFieldListener(new TextField.TextFieldListener() {
            public void keyTyped (TextField textField, char key) {
                if (key == '\n') textField.getOnscreenKeyboard().show(false);
            }
        });

        TextButton.TextButtonStyle bStyle = new TextButton.TextButtonStyle();
        bStyle.font = Assets.font22;
        bStyle.fontColor = Color.BLACK;
        bStyle.up = skin.getDrawable("button-up");
        bStyle.down = skin.getDrawable("button-down");

        final TextButton loginButton = new TextButton("Login", bStyle);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userField.getText().isEmpty()) {
                    userField.setStyle(twStyle);
                    return;
                }
                if (passField.getText().isEmpty()) {
                    passField.setStyle(twStyle);
                    return;
                }

                loginButton.setText("Verifying..");

                final String user = userField.getText();
                final String password = passField.getText();
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("username", user);
                parameters.put("password", password);
                parameters.put("grant_type", "password");
                parameters.put("client_id", userField.getText());
                Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
                final String url = "https://sheepherder-highscore.herokuapp.com/oauth2/access_token/";
                request.setUrl(url);
                request.setHeader("Content-Type", "application/x-www-form-urlencoded");
                request.setHeader("Accept", "application/json");
                request.setContent(HttpParametersUtils.convertHttpParameters(parameters));

                Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        int statusCode = httpResponse.getStatus().getStatusCode();
                        if(statusCode != HttpStatus.SC_OK) {
                            userField.setStyle(twStyle);
                            passField.setStyle(twStyle);
                            loginButton.setText("Login");
                            System.out.println("Request Failed: getToken");
                            return;
                        }

                        String result = httpResponse.getResultAsString();
                        try {
                            JSONObject json = new JSONObject(result);
                            if(json.has("access_token")) {
                                SavedData.setUser(user, password);
                                ScreenService.getInstance().add(new LeaderboardScreen());
                            } else {
                                userField.setStyle(twStyle);
                                passField.setStyle(twStyle);
                                loginButton.setText("Login");
                            }
                        }  catch(JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                    @Override
                    public void failed(Throwable t) {
                        userField.setStyle(twStyle);
                        userField.setText("Connection error");
                        passField.setStyle(twStyle);
                        loginButton.setText("Login");
                        Gdx.app.error("Failed ", t.getMessage());
                    }
                    @Override
                    public void cancelled() {
                        userField.setStyle(twStyle);
                        passField.setStyle(twStyle);
                        loginButton.setText("Login");
                        Gdx.app.error("Failed", "request cancelled");
                    }
                });
            }
        });

        
        table.row().height(50);
        table.add(loginLabel).width(300).padTop(50f);
        table.row().height(50);
        table.add(userField).center().width(300).pad(5f);
        table.row().height(50);
        table.add(passField).center().width(300).pad(5f);
        table.row().height(50);
        table.add(loginButton).right().width(200).pad(5f);
        stage.addActor(table);
    }

    @Override
    protected boolean isOverlay() {
        return true;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void draw() {
        SheepHerder.batch.begin();
        SheepHerder.batch.draw(Assets.login, 25, 231, 446, 399);
        SheepHerder.batch.end();

        stage.act();
        stage.draw();

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            SheepHerder.camera.unproject(touchPos);
            if (touchPos.x > 410 && touchPos.x < 410 + 60
                    && touchPos.y > 570 && touchPos.y < 570 + 60) {
                // close login window
                ScreenService.getInstance().removeOverlay(true);
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
