package com.example.zzpj.stats;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.steamApi.SteamApi;
import com.example.zzpj.squad.SquadRepository;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import lombok.SneakyThrows;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserStatsServiceTest {

    UserStatsService userStatsService;

    SteamApi steamApi;
    GameRepository gameRepository;
    UserRepository userRepository;
    SquadRepository squadRepository;

    List<GameStats> gameStats;
    List<Game> games;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeEach
    void setUp() {
        steamApi = mock(SteamApi.class);
        gameRepository = mock(GameRepository.class);
        userRepository = mock(UserRepository.class);
        squadRepository = mock(SquadRepository.class);

        gameStats = new ArrayList<>();
        gameStats.add(new GameStats(0,81,200,50,0,0));
        gameStats.add(new GameStats(1,50,250,50,0,0));
        gameStats.add(new GameStats(2,80,300,50,0,0));

        games = new ArrayList<>();
        games.add(new Game(0,"cs:go ",null,null));
        games.add(new Game(1,"Dota2",null,null));
        games.add(new Game(2,"StarCraft",null,null));
    }

    @AfterEach
    void tearDown() {
        gameStats = new ArrayList<>();
    }

    @SneakyThrows
    @Test
    void shouldThrowExceptionWhenUserDontExists(){
        //given
        when(userRepository.findByLogin(any())).thenReturn(null);
        userStatsService = new UserStatsService(steamApi, gameRepository, userRepository, squadRepository);

        //then
        assertThrows(NullPointerException.class, () -> userStatsService.getUserStats("bad_login"));
    }

    @SneakyThrows
    @Test
    void shouldReturnCorrectObject(){
        //given
        User user = User.builder().login("login").steamId(123).password("password").build();
        Game game1 = new Game(0,"cs:go ",null,null);
        Game game2 = new Game(2,"StarCraft",null,null);

        when(userRepository.findByLogin(any())).thenReturn(java.util.Optional.ofNullable(user));
        when(steamApi.getUserGameStats(123)).thenReturn(gameStats);
        when(gameRepository.getByAppid(0L)).thenReturn(game1);
        when(gameRepository.getByAppid(2L)).thenReturn(game2);
        userStatsService = new UserStatsService(steamApi, gameRepository, userRepository, squadRepository);

        //when
        UserStats gameStats = userStatsService.getUserStats("login");

        //then
        assertEquals(gameStats.getGames(), 3);
        assertEquals(gameStats.getLogin(), "login");
        assertEquals(gameStats.getMostPlayedGame(), game2.getName());
        assertEquals(gameStats.getMostPlayedGame2Weeks(), game1.getName());
        assertEquals(gameStats.getPlaytime(), 750);
        assertEquals(gameStats.getPlaytime2Weeks(), 211);
    }
}