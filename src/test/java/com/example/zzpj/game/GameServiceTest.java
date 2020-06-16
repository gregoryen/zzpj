package com.example.zzpj.game;

import com.example.zzpj.InitTestObjects;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.configuration.CustomUserDetailsService;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class GameServiceTest {
    @Autowired
    GameService gameService;
    @Autowired
    UserService userService;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    static User testUser;
    static Game testGame;
    static UserSignUpPOJO accountDetails;
    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired UserRepository userRepository, @Autowired GameRepository gameRepository){
        testUser = InitTestObjects.initUser();
        userRepository.save(testUser);
        testGame = InitTestObjects.initGame();
        gameRepository.save(testGame);
    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository, @Autowired GameRepository gameRepository){
        userRepository.delete(testUser);
        gameRepository.delete(testGame);
    }

    @Test
    void shouldImportAllGames() throws Exception{
        gameService.importAllGamesFromSteam();
        Assert.assertTrue(gameRepository.findAll().size() >= 97420);
        gameRepository.deleteAll();
    }
    @Test
    void shouldReturnUserGames() throws Exception{
        List<Long> gameList = gameService.getUserGamesFromSteam(testUser.getSteamId());
        Assert.assertTrue(gameList.size() >= 30);
        Assert.assertTrue(gameList.stream().filter(aLong -> aLong.equals(730L)).findAny().isPresent());
        Assert.assertTrue(gameList.stream().filter(aLong-> aLong.equals(8190L)).findAny().isPresent());
        Assert.assertThrows(Exception.class,()->{
            gameService.getUserGamesFromSteam(-1L);
        });
    }
    @Test
    @Transactional
    void shouldInsertUserGamesToDb() {
        testUser.getGames().removeAll(testUser.getGames());
        userRepository.save(testUser);
        Assert.assertEquals(testUser.getGames().size(), 0);
        gameService.insertUserGamesToDb(testUser.getSteamId());
        testUser = userRepository.getBySteamId(testUser.getSteamId());
        Assert.assertTrue(testUser.getGames().size() >= 30);
    }
}