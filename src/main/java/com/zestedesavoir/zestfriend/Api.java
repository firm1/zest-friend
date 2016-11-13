package com.zestedesavoir.zestfriend;

import okhttp3.*;

import javax.json.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Api {
    private String clienId;
    private String clientSecret;
    private OkHttpClient clientHttp;
    private Credential credential;
    private static final String authUrl = "https://zestedesavoir.com/oauth2/token/";
    private static final String mpListUrl = "https://zestedesavoir.com/api/mps/";
    private static final String memberListUrl = "https://zestedesavoir.com/api/membres/";

    public Api(String clienId, String clientSecret) {
        this.clienId = clienId;
        this.clientSecret = clientSecret;
        this.clientHttp = new OkHttpClient();
    }

    public boolean authenticate(String login, String password) {
        JsonObject json = Json.createObjectBuilder()
                .add("client_id", this.clienId)
                .add("client_secret", this.clientSecret)
                .add("grant_type", "password")
                .add("username", login)
                .add("password", password)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(authUrl)
                .post(body)
                .build();
        try {
            Response response = clientHttp.newCall(request).execute();

            if(response.code() == 200) {
                String jsonResponse = response.body().string();
                JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
                JsonObject objJson = jsonReader.readObject();
                jsonReader.close();
                credential = new Credential(
                        objJson.getString("access_token"),
                        objJson.getString("token_type"),
                        objJson.getInt("expires_in"),
                        objJson.getString("refresh_token"),
                        objJson.getString("scope"));
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> getPrivateMessages() {
        List<Message> messages = new ArrayList<>();
        String url = mpListUrl+"?page_size=100";

        do {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", credential.getAccessToken())
                    .build();

            url = "";

            try {
                Response response = clientHttp.newCall(request).execute();
                if (response.code() == 200) {
                    String jsonResponse = response.body().string();
                    JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
                    JsonObject objJson = jsonReader.readObject();
                    if(!objJson.isNull("next")) {
                        url = objJson.getString("next");
                    }
                    JsonArray arrayJson = objJson.getJsonArray("results");

                    for (JsonValue jsonValue : arrayJson) {
                        JsonObject v = Json.createReader(new StringReader(jsonValue.toString())).readObject();
                        JsonArray participantsArray = v.getJsonArray("participants");
                        List<Member> participants = participantsArray.stream()
                                .map(val -> getMemberFromId(Integer.parseInt(val.toString())))
                                .collect(Collectors.toList());

                        messages.add(new Message(
                                v.getInt("id"),
                                getMemberFromId(v.getInt("author")),
                                participants,
                                v.getString("title"),
                                v.getString("subtitle")
                        ));
                    }

                    jsonReader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } while(!url.equals(""));

        return messages;
    }

    public int getIdFromUsername(String username) {
        Request request = new Request.Builder()
                .url(memberListUrl+"?search="+username)
                .addHeader("Authorization", credential.getAccessToken())
                .build();
        try {
            Response response = clientHttp.newCall(request).execute();
            if (response.code() == 200) {
                String jsonResponse = response.body().string();
                JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
                JsonObject objJson = jsonReader.readObject();
                JsonArray arrayJson = objJson.getJsonArray("results");

                for (JsonValue jsonValue : arrayJson) {
                    JsonObject v = Json.createReader(new StringReader(jsonValue.toString())).readObject();
                    if(v.getString("username").equals(username)) {
                        return v.getInt("id");
                    }
                }
                jsonReader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Member getMemberFromId(int id) {
        List<Member> searchs = Member.members.stream()
                .filter(m -> m.getId() == id)
                .collect(Collectors.toList());
        if(searchs.size() == 1) {
            return searchs.get(0);
        }

        Request request = new Request.Builder()
                .url(memberListUrl+id+"/")
                .addHeader("Authorization", credential.getAccessToken())
                .build();
        try {
            Response response = clientHttp.newCall(request).execute();
            if(response.code() == 200) {
                String jsonResponse = response.body().string();
                JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
                JsonObject objJson = jsonReader.readObject();
                jsonReader.close();
                Member m = new Member(
                        objJson.getInt("id"),
                        objJson.getString("username"),
                        objJson.getString("avatar_url"),
                        objJson.getBoolean("is_active"),
                        objJson.getString("last_visit", ""),
                        objJson.getString("date_joined")
                );
                Member.members.add(m);
                return m;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Post> getPrivatePosts(Message message) {
        List<Post> posts = new ArrayList<>();
        String url = mpListUrl+message.getId()+"/messages/?page_size=150";

        do {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", credential.getAccessToken())
                    .build();

            url = "";

            try {
                Response response = clientHttp.newCall(request).execute();
                if (response.code() == 200) {
                    String jsonResponse = response.body().string();
                    JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse));
                    JsonObject objJson = jsonReader.readObject();
                    if(!objJson.isNull("next")) {
                        url = objJson.getString("next");
                    }
                    JsonArray arrayJson = objJson.getJsonArray("results");

                    for (JsonValue jsonValue : arrayJson) {
                        JsonObject v = Json.createReader(new StringReader(jsonValue.toString())).readObject();
                        posts.add(new Post(
                                v.getInt("id"),
                                v.getString("text"),
                                getMemberFromId(v.getInt("author"))
                        ));
                    }

                    jsonReader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } while(!url.equals(""));

        return posts;
    }


}
