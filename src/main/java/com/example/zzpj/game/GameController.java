package com.example.zzpj.game;

import com.example.zzpj.steam_api.SteamApi;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("games")
public class GameController {

    private SteamApi steamApi;
    private GameRepository gameRepository;


    @Autowired
    public GameController(SteamApi steamApi, GameRepository gameRepository) {
        this.steamApi = steamApi;
        this.gameRepository = gameRepository;

    }

    @PutMapping("/import")
    public void importAllGames() throws IOException, ParseException {
        gameRepository.saveAll(steamApi.importAllGamesFromSteam());
    }


    @GetMapping("/user")
    public List<Long> getUserGamesFromSteam(@RequestParam long steamId) throws IOException, ParseException {
        //return gameService.getUserGamesFromSteam(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(steamApi.getUserGamesFromSteam(steamId));
        return steamApi.getUserGamesFromSteam(steamId);
    }
}
