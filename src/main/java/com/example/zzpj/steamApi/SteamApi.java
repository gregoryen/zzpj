package com.example.zzpj.steamApi;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.stats.GameStats;
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
import java.util.stream.Collectors;

@Service
public class SteamApi {

    GameRepository gameRepository;
    UserRepository userRepository;
    Parser parser;
    private final String STEAM_KEY;

    @Autowired
    public SteamApi(@Value("${steamKey}") String privateKeyString, UserRepository userRepository, GameRepository gameRepository, Parser parser){
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.parser = parser;
        STEAM_KEY = privateKeyString;
    }

    public List<Game> importAllGamesFromSteam() throws IOException, ParseException {
        String url = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
        String response = this.getResponse(url);

        return this.parser.parseAllGames(response);
    }

    public List<Long> getUserGamesFromSteam(String steamId) throws IOException, ParseException {
        String url = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key="+ STEAM_KEY +"&steamid="+ steamId +"/&format=json";
        String response = this.getResponse(url);

        return this.parser.parseUserGames(response).stream().map(GameStats::getAppid).collect(Collectors.toList());
    }

    public List<GameStats> getUserGameStats(long steamId) throws IOException, ParseException {
        String url = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key="+ STEAM_KEY +"&steamid="+ steamId +"/&format=json";
        String response = this.getResponse(url);

        return this.parser.parseUserGames(response);
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

}
