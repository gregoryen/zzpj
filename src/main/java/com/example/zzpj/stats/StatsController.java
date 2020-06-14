package com.example.zzpj.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user_stats")
public class StatsController {
    UserStatsService userStatsService;

    public StatsController(@Autowired UserStatsService userStatsService) {
        this.userStatsService = userStatsService;
    }

    @GetMapping
    public ResponseEntity<UserStats> getUserStats(@RequestParam String login){
        UserStats stats;
        try {
            stats = userStatsService.getUserStats(login);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(stats);
    }
}
