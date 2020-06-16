package com.example.zzpj.game;

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
    static UserSignUpPOJO accountDetails;
    @BeforeAll
    static void setUp(@Autowired UserService userService){
        accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198036881526L);
        testUser = userService.registerNewUserAccount(accountDetails);
    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository){
        userRepository.delete(userRepository.getByLogin("testtest12345678910"));
    }

    @Test
    void shouldImportAllGames() throws Exception{
        gameService.importAllGamesFromSteam();
        Assert.assertTrue(gameRepository.findAll().size() >= 97420);
    }
    @Test
    void shouldReturnUserGames() throws Exception{
        List<Long> gameList = gameService.getUserGamesFromSteam(testUser.getSteamId());
        Assert.assertTrue(gameList.size() > 19);
        Assert.assertTrue(gameList.stream().filter(aLong -> aLong.equals(730L)).findAny().isPresent());
        Assert.assertTrue(gameList.stream().filter(aLong-> aLong.equals(205790L)).findAny().isPresent());
        Assert.assertThrows(Exception.class,()->{
            gameService.getUserGamesFromSteam(-1L);
        });
    }
    @Test
    @Transactional
    void shouldInsertUserGamesToDb() {
        User user = userRepository.getBySteamId(testUser.getSteamId());
        user.getGames().removeAll(user.getGames());
        userRepository.save(user);
        user = userRepository.getBySteamId(testUser.getSteamId());
        Assert.assertEquals(user.getGames().size(), 0);
        gameService.insertUserGamesToDb(testUser.getSteamId());
        user = userRepository.getBySteamId(testUser.getSteamId());
        Assert.assertTrue(user.getGames().size() > 19);
    }
}