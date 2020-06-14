package com.example.zzpj.game;

import com.example.zzpj.service.GameService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("games")
public class GameController {

    private GameService gameService;

    public GameController(@Autowired GameService gameService) {
        this.gameService = gameService;
    }

    @PutMapping("/import")
    public void importAllGames() throws IOException, ParseException {
        gameService.importAllGamesFromSteam();
    }

    @GetMapping("/user")
    public List<Long> getUserGamesFromSteam(@RequestParam String steamId) throws IOException, ParseException {
        return gameService.getUserGamesFromSteam(steamId);
    }
}