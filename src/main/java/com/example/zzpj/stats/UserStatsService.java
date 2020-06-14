package com.example.zzpj.stats;

import com.example.zzpj.game.GameRepository;
import com.example.zzpj.service.GameService;
import com.example.zzpj.service.GameStats;
import com.example.zzpj.users.UserRepository;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service("userStatsService")
public class UserStatsService {
    GameService gameService;
    GameRepository gameRepository;
    UserRepository userRepository;

    public UserStatsService(@Autowired GameService gameService, @Autowired GameRepository gameRepository, @Autowired UserRepository userRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public UserStats getUserStats(String login) throws IOException, ParseException {
        long steamID = userRepository.findByLogin(login).get().getSteamId();
        List<GameStats> gameStats = gameService.getUserGameStats(steamID);

        int games = gameStats.size();
        long playtime = gameStats.stream().mapToLong(GameStats::getPlaytimeForever).sum();
        long playtime2Weeks = gameStats.stream().mapToLong(GameStats::getPlaytime2Weeks).sum();
        GameStats mostPlayed = gameStats.stream().max(Comparator.comparingLong(GameStats::getPlaytimeForever)).orElse(null);
        GameStats mostPlayed2Weeks = gameStats.stream().max(Comparator.comparingLong(GameStats::getPlaytime2Weeks)).orElse(null);

        String mostPlayedGame = mostPlayed != null ? gameRepository.getByAppid(mostPlayed.getAppid()).getName() : "-";
        String mostPlayedGame2Weeks = mostPlayed2Weeks != null ?  gameRepository.getByAppid(mostPlayed2Weeks.getAppid()).getName() : "-";

        return UserStats.builder()
                .login(login)
                .games(games)
                .playtime(playtime)
                .playtime2Weeks(playtime2Weeks)
                .mostPlayedGame(mostPlayedGame)
                .mostPlayedGame2Weeks(mostPlayedGame2Weeks)
                .build();
    }

}
