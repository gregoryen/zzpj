package com.example.zzpj.steam_api;

import com.example.zzpj.game.Game;
import com.example.zzpj.stats.GameStats;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Parser {
    public List<GameStats> parseUserGames(String jsonString) throws ParseException {
        List<GameStats> games = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jobj = (JSONObject)parser.parse(jsonString);
        JSONObject applist = (JSONObject) jobj.get("response");

        JSONArray apps = (JSONArray) applist.get("games");
        if(apps != null) {
            for (Object app : apps) {
                JSONObject gameJson = (JSONObject) app;
                long appid = (long) gameJson.get("appid");
                long playtimeForever = (long) gameJson.get("playtime_forever");
                long playtimeWindows = (long) gameJson.get("playtime_windows_forever");
                long playtimeMac = (long) gameJson.get("playtime_mac_forever");
                long playtimeLinux = (long) gameJson.get("playtime_linux_forever");
                Object playtime = gameJson.get("playtime_2weeks");
                long playtime2Weeks = playtime == null ? 0 : (long) playtime;
                games.add(new GameStats(appid, playtime2Weeks, playtimeForever, playtimeWindows, playtimeMac, playtimeLinux));
            }
        }
        return games;
    }

  /*  public List<Long> parseGamesId(String jsonString) throws ParseException {
        List<Long> gamesId = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jobj = (JSONObject)parser.parse(jsonString);
        JSONObject applist = (JSONObject) jobj.get("response");
        JSONArray apps = (JSONArray) applist.get("games");
        if(apps != null)
            for (Object app : apps) {
                JSONObject gameJson = (JSONObject) app;
                gamesId.add((Long) gameJson.get("appid"));
            }

        return gamesId;
    }*/

    public List<Game> parseAllGames(String jsonString) throws ParseException {
        List<Game> games = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jobj = (JSONObject)parser.parse(jsonString);
        JSONObject applist = (JSONObject) jobj.get("applist");

        JSONArray apps = (JSONArray) applist.get("apps");

        for (Object app : apps) {
            JSONObject gameJson = (JSONObject) app;
            Long id = (Long) gameJson.get("appid");
            Game game = new Game();
            game.setAppid(id);
            game.setName((String)gameJson.get("name"));
            games.add(game);
        }

        return games;
    }
}
