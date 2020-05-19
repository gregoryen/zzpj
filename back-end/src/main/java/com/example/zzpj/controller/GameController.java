package com.example.zzpj.controller;

import com.example.zzpj.service.GameService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("games")
public class GameController {
    @Autowired
    private GameService gameService;

    @PutMapping("/import")
    public void importAllGames() throws IOException, ParseException {
        gameService.importAllGamesFromSteam();
    }
}
