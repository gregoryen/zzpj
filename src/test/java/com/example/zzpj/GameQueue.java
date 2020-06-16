package com.example.zzpj;

import com.example.zzpj.game.GameRepository;
import com.example.zzpj.queue.GameQueueService;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.steamApi.SteamApi;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import com.example.zzpj.users.UserTokenInformation;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
public class GameQueue {
    @Autowired
    SteamApi steamApi;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameQueueService gameQueueService;

    static User testUser;
    static String jwtToken;
//TODO duzo i jeszcze wiecej testow choc nie wiem czy dobrze je pisze tutaj po prostu wywoluje metode w
    // innym pliku mam to bardziej na poszczegolne linijki z metody rozbite
    @BeforeAll
    static void createTestAccount(@Autowired UserService userService, @Autowired JwtUtil jwtUtil){
        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198105857198L);
        testUser = userService.registerNewUserAccount(accountDetails);
        UserTokenInformation uti = userService.getUserDetailsForToken(testUser.getLogin());
        jwtToken = jwtUtil.generateToken(uti,"none");
    }
    @AfterAll
    static void cleanAfterTest(@Autowired UserRepository userRepository){
        userRepository.delete(userRepository.getByLogin("testtest12345678910"));
    }

    @Test
    @Transactional
    void createNewQueue(){
      gameQueueService.addPlayerToQueue(testUser.getLogin(),"Magicka");
      Assert.assertTrue(gameQueueService.findAllGameQueue().stream().filter(gameQueue -> gameQueue.getGameName().equals("Magicka")).findAny().isPresent());
        Assert.assertTrue(gameQueueService.findAllGameQueue().stream().filter(gameQueue -> gameQueue.getPlayersInQueue().stream().filter(user -> user.getLogin().equals(testUser.getLogin())).findAny().isPresent()).findAny().isPresent());

    }


}
