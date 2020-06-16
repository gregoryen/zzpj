package com.example.zzpj.game;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("games")
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PutMapping("/import")
    public void importAllGames() throws IOException, ParseException {
        gameService.importAllGamesFromSteam();
    }

    //TODO nie wiem czy nie powinnismy robic jak w komentarzu ze te get i posty sa od tokenu zalezne
    // bo wtedy kazdy zalogowany moze podgladac innego w tych postach wystarcyz ze w param wpisze nie swoje dane

    @GetMapping("/user")
    public List<Long> getUserGamesFromSteam(@RequestParam Long steamId) throws IOException, ParseException {
        //return gameService.getUserGamesFromSteam(SecurityContextHolder.getContext().getAuthentication().getName());
        return gameService.getUserGamesFromSteam(steamId);
    }
}
