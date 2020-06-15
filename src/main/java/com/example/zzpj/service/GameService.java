package com.example.zzpj.service;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    GameRepository gameRepository;
    UserRepository userRepository;
    private final String STEAM_KEY;

    @Autowired
    public GameService(@Value("${steamKey}") String privateKeyString, UserRepository userRepository, GameRepository gameRepository){
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        STEAM_KEY = privateKeyString;
    }

    public void importAllGamesFromSteam() throws IOException, ParseException {
        String url = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
        String response = this.getResponse(url);

        List<Game> games = this.parseAllGames(response);
        this.saveAllGames(games);
    }

    public List<Long> getUserGamesFromSteam(String steamId) throws IOException, ParseException {
        String url = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key="+ STEAM_KEY +"&steamid="+ steamId +"/&format=json";
        String response = this.getResponse(url);

        return this.parseGamesId(response);
    }

    public List<GameStats> getUserGameStats(long steamId) throws IOException, ParseException {
        String url = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key="+ STEAM_KEY +"&steamid="+ steamId +"/&format=json";
        String response = this.getResponse(url);

        return this.parseUserGames(response);
    }

    public boolean insertUserGamesToDb(String steamId){
        try {
            List<Long> userGames = getUserGamesFromSteam(steamId);
            User user = userRepository.getBySteamId(Long.parseLong(steamId));
            user.setGames(new ArrayList<>());
            for(Long id : userGames){
                Game game =gameRepository.getByAppid(id);
                user.getGames().add(game);
            }
            userRepository.save(user);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    private String getResponse(String urlString) throws IOException {
        HttpURLConnection con;
        URL url = new URL(urlString);
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
        con.disconnect();

        return content.toString();
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
            Game game = new Game();
            game.setAppid(id);
            game.setName((String)gameJson.get("name"));
            games.add(game);
        }

        return games;
    }

    private void saveAllGames(List<Game> games){
        gameRepository.saveAll(games);
    }

    public List<Long> parseGamesId(String jsonString) throws ParseException {
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
    }

    private List<GameStats> parseUserGames(String jsonString) throws ParseException {
        List<GameStats> games = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jobj = (JSONObject)parser.parse(jsonString);
        JSONObject applist = (JSONObject) jobj.get("response");

        JSONArray apps = (JSONArray) applist.get("games");

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

        return games;
    }
}
