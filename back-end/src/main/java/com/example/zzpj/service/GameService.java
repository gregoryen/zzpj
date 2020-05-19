package com.example.zzpj.service;

import com.example.zzpj.model.Game;
import com.example.zzpj.repository.GameRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;

    public void importAllGamesFromSteam() throws IOException, ParseException {
        HttpURLConnection con;
        URL url = new URL("https://api.steampowered.com/ISteamApps/GetAppList/v2/");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.connect();

        int responsecode = con.getResponseCode();

        StringBuilder content = new StringBuilder();
        if(responsecode == 200){
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
        }

        List<Game> games = this.parseAllGames(content.toString());
        System.out.println(games.size());
        this.saveAllGames(games);
        con.disconnect();
    }

    private List<Game> parseAllGames(String jsonString) throws ParseException {
        List<Game> games = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jobj = (JSONObject)parser.parse(jsonString);
        JSONObject applist = (JSONObject) jobj.get("applist");

        JSONArray apps = (JSONArray) applist.get("apps");

        for (Object app : apps) {
            JSONObject gameJson = (JSONObject) app;
            Long id = (Long) gameJson.get("appid");
            games.add(new Game(id, (String) gameJson.get("name")));
        }

        System.out.println(games.size());
        return games;
    }

    private void saveAllGames(List<Game> games){
        gameRepository.saveAll(games);
    }

}
