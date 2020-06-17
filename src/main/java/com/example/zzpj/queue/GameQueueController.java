package com.example.zzpj.queue;

import com.example.zzpj.queue.exception.GameQueueNotExistException;
import com.example.zzpj.ranking.RateService;
import com.example.zzpj.security.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;

@RestController
@RequestMapping("queue")
public class GameQueueController {

    GameQueueService gameQueueService;
    UserService userService;
    RateService rateService;

    @Autowired
    public GameQueueController(GameQueueService gameQueueService, UserService userService, RateService rateService) {
        this.gameQueueService = gameQueueService;
        this.userService = userService;
        this.rateService = rateService;
    }


    @PostMapping("/addPlayer")
    public ResponseEntity addPlayerToQueue(@RequestParam String login, @RequestParam String gameName){
        try {
            gameQueueService.addPlayerToQueue(login, gameName);
            return ResponseEntity.ok("User "+login+" added to " + gameName + " queue");
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }

    @DeleteMapping("/removePlayer")
    public ResponseEntity removePlayerFormQueue(@RequestParam String login, @RequestParam String gameName){
        try {
            gameQueueService.removePlayerFromQueue(login, gameName);
            return ResponseEntity.ok("User "+login+" removed from " + gameName + " queue");
        }
        catch(UsernameNotFoundException e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        }

    @GetMapping()
    @ResponseBody
    public ResponseEntity findAll(){
        try {
            return ResponseEntity.ok(gameQueueService.findAllGameQueue());
        } catch (GameQueueNotExistException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/id")
    public ResponseEntity findByGameName(@RequestParam String gameName){
        try {
            return ResponseEntity.ok(gameQueueService.findGameQueue(gameName));
        } catch (GameQueueNotExistException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
