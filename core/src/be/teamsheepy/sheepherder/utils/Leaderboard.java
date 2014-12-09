package be.teamsheepy.sheepherder.utils;

import be.teamsheepy.sheepherder.SavedData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Leaderboard {

    private String token;
    public int rank;
    public int page;
    public int totalPages;

    public ArrayList<LeaderboardEntry> leaderBoardPage = new ArrayList<LeaderboardEntry>();
    // no 0 -> busy 1 -> failed 2 /success 3
    public final static int NO = 0;
    public final static int BUSY = 1;
    public final static int FAILED = 2;
    public final static int SUCCESS = 3;
    public int status = NO;
    public String error;

    public class LeaderboardEntry{
        public int rank;
        public String playerName;
        public int score;

        public LeaderboardEntry(int rank, String playerName, int score) {
            this.rank = rank;
            this.playerName = playerName;
            this.score = score;
        }
    }

    public Leaderboard(String user, String password) {
        status = BUSY;
        getToken(user, password);
    }

    public Leaderboard(int page) {
        this.page = page;
        status = BUSY;
        getPage(page);
    }

    private void getTotalPages() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        final String url = "https://sheepherder-highscore.herokuapp.com/highscores/";
        request.setUrl(url);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    System.out.println("Request Failed: getPage");
                    return;
                }

                String result = httpResponse.getResultAsString();
                try {
                    JsonReader reader = new JsonReader();
                    JsonValue json = reader.parse(result);
                    totalPages = json.getInt("pages");
                    status = SUCCESS;
                }  catch(Exception exception) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    exception.printStackTrace();
                }
            }

            public void failed(Throwable t) {
                status = FAILED;
                error = "No internet connection";
                Gdx.app.error("Failed ", t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.error("Failed", "request cancelled");
            }
        });
    }

    private void getPage(final int page) {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        final String url = "https://sheepherder-highscore.herokuapp.com/highscores/" + page + "/";
        request.setUrl(url);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    System.out.println("Request Failed: getPage");
                    return;
                }

                String result = httpResponse.getResultAsString();
                try {
                    JsonReader reader = new JsonReader();
                    JsonValue json = reader.parse(result);
                    int i = 1;
                    for(JsonValue e : json){
                        int rank = page * 10 + i;
                        LeaderboardEntry entry = new LeaderboardEntry(rank, e.getString("player_name"), e.getInt("score"));
                        leaderBoardPage.add(entry);
                        i++;
                    }
                    getTotalPages();
                }  catch(Exception exception) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    exception.printStackTrace();
                }
            }

            public void failed(Throwable t) {
                status = FAILED;
                error = "No internet connection";
                Gdx.app.error("Failed ", t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.error("Failed", "request cancelled");
            }
        });
    }

    public void getToken(String user, String password) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", user);
        parameters.put("password", password);
        parameters.put("grant_type", "password");
        parameters.put("client_id", user);
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
                    status = FAILED;
                    error = "Error downloading highscores";
                    System.out.println("Request Failed: getToken");
                    return;
                }

                String result = httpResponse.getResultAsString();
                try {
                    JsonReader reader = new JsonReader();
                    JsonValue json = reader.parse(result);
                    token = json.getString("access_token");
                    updateScore(SavedData.highscore);
                }  catch(Exception exception) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    exception.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable t) {
                status = FAILED;
                error = "No internet connection";
                Gdx.app.error("Failed ", t.getMessage());
            }
            @Override
            public void cancelled() {
                Gdx.app.error("Failed", "request cancelled");
            }
        });
    }

    public void updateScore(final int score) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("score", score + "");
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        final String url = "https://sheepherder-highscore.herokuapp.com/user/matches/";
        request.setUrl(url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Accept", "application/json");
        request.setHeader("Authorization", "bearer " + token);
        request.setContent(HttpParametersUtils.convertHttpParameters(parameters));

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                getRank();
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_CREATED) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    System.out.println("Request Failed: updateScore");
                    return;
                }
            }
            @Override
            public void failed(Throwable t) {
                status = FAILED;
                error = "No internet connection";
                Gdx.app.error("Failed ", t.getMessage());
            }
            @Override
            public void cancelled() {
                Gdx.app.error("Failed", "request cancelled");
            }
        });
    }

    public void getRank() {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        final String url = "https://sheepherder-highscore.herokuapp.com/user/highscore/";
        request.setUrl(url);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Accept", "application/json");
        request.setHeader("Authorization", "bearer " + token);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    System.out.println("Request Failed: getRank");
                    return;
                }

                String result = httpResponse.getResultAsString();
                try {
                    JsonReader reader = new JsonReader();
                    JsonValue json = reader.parse(result);
                    rank =  json.getInt("rank");
                    int score = json.getInt("score");
                    if (score > SavedData.highscore)
                        SavedData.newHighscore(score);
                    page = (rank - 1) /10;
                    getPage(page);
                }  catch(Exception exception) {
                    status = FAILED;
                    error = "Error downloading highscores";
                    exception.printStackTrace();
                }
            }
            @Override
            public void failed(Throwable t) {
                status = FAILED;
                error = "No internet connection";
                Gdx.app.error("Failed ", t.getMessage());
            }
            @Override
            public void cancelled() {
                Gdx.app.error("Failed", "request cancelled");
            }
        });
    }
}
