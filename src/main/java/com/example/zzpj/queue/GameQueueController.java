package com.example.zzpj.queue;

import com.example.zzpj.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void addPlayerToQueue(@RequestParam String login, @RequestParam String gameName){
        try {
            gameQueueService.addPlayerToQueue(login, gameName);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @DeleteMapping("/removePlayer")
    public void removePlayerFormQueue(@RequestParam String login, @RequestParam String gameName){
        gameQueueService.removePlayerFromQueue(login, gameName);
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
