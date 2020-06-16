package com.example.zzpj.game;

import com.example.zzpj.InitTestObjects;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.configuration.CustomUserDetailsService;
import com.example.zzpj.steam_api.SteamApi;
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

@SpringBootTest
class SteamApiTest {
    @Autowired
    SteamApi steamApi;
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

    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository){
        userRepository.delete(testUser);
    }

    @Test
    void shouldImportAllGames() throws Exception{
        Assert.assertTrue(steamApi.importAllGamesFromSteam().size() >= 97420);
        gameRepository.deleteAll();
    }
    @Test
    void shouldReturnUserGames() throws Exception{
        List<Long> gameList = steamApi.getUserGamesFromSteam(testUser.getSteamId());
        Assert.assertTrue(gameList.size() >= 30);
        Assert.assertTrue(gameList.stream().filter(aLong -> aLong.equals(730L)).findAny().isPresent());
        Assert.assertTrue(gameList.stream().filter(aLong-> aLong.equals(8190L)).findAny().isPresent());
        Assert.assertThrows(Exception.class,()->{
            steamApi.getUserGamesFromSteam(-1L);
        });
    }
    @Test
    @Transactional
    void shouldInsertUserGamesToDb() {
        testUser.getGames().removeAll(testUser.getGames());
        userRepository.save(testUser);
        Assert.assertEquals(testUser.getGames().size(), 0);
        userService.insertUserGamesToDb(testUser.getSteamId());
        testUser = userRepository.getBySteamId(testUser.getSteamId());
        Assert.assertTrue(testUser.getGames().size() >= 30);
    }
}