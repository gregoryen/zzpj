package com.example.zzpj.queue;

import com.example.zzpj.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("queue")
public class GameQueueController {

    GameQueueService gameQueueService;
    UserService userService;

    @Autowired
    public GameQueueController(GameQueueService gameQueueService, UserService userService) {
        this.gameQueueService = gameQueueService;
        this.userService = userService;
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
    public List<GameQueue> findAll(){
        return gameQueueService.findAllGameQueue();
    }

    @GetMapping("/id")
    @ResponseBody
    public GameQueue findByGameName(@RequestParam String gameName){
        return gameQueueService.findGameQueue(gameName);
    }



}
