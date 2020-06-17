package com.example.zzpj.queue;

import com.example.zzpj.InitTestObjects;
import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.steam_api.SteamApi;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import com.example.zzpj.users.UserTokenInformation;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class GameQueueControllerTest {


    static MockMvc mvc;
    static User testUser;
    static String jwtToken;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    SteamApi steamApi;
    @Autowired
    GameRepository gameRepository;
    @Autowired GameQueueRepository gameQueueRepository;
    @Autowired GameQueueService gameQueueService;
    static Game testGame;

    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired JwtUtil jwtUtil, @Autowired WebApplicationContext webApplicationContext, @Autowired GameRepository gameRepository, @Autowired UserRepository userRepository){
        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("1234");
        accountDetails.setLogin("test1");
        accountDetails.setSteamId(76561198253700224L);
        testUser = userService.registerNewUserAccount(accountDetails);
        UserTokenInformation uti = userService.getUserDetailsForToken(testUser.getLogin());
        jwtToken = jwtUtil.generateToken(uti,"none");
        testGame = InitTestObjects.initGame();
        testUser.setGames(new ArrayList<>());
        testUser.setQueues(new ArrayList<>());
        testUser.getGames().add(testGame);
        testGame.getUsers().add(testUser);
        gameRepository.save(testGame);
        userRepository.save(testUser);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
@AfterAll
static void tearDown(@Autowired UserRepository userRepository, @Autowired GameRepository gameRepository){
    testGame.getUsers().remove(testUser);
    testUser.getGames().remove(testGame);
    userRepository.saveAll(Arrays.asList(testUser));
    userRepository.delete(testUser);
    gameRepository.delete(testGame);

}
    @Test
    @Transactional
    @SneakyThrows
    void addPlayerToQueue() {
        String uri = "/queue/addPlayer";
        MvcResult goodResult = mvc.perform(MockMvcRequestBuilders.post(uri).header("Authorization","Bearer "+jwtToken)
                .param("login",testUser.getLogin())
                .param("gameName",testGame.getName())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        MvcResult wrongResult = mvc.perform(MockMvcRequestBuilders.post(uri).header("Authorization","Bearer "+jwtToken)
                .param("login","wrongLogin")
                .param("gameName",testGame.getName())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = goodResult.getResponse().getStatus();
        User user = userRepository.getByLogin(testUser.getLogin());
        Assert.assertEquals(200, status);
        Assert.assertEquals(gameQueueRepository.findAll().size(), 1);
        Assert.assertEquals(user.getQueues().get(0).getGameName(),testGame.getName());
        Assert.assertEquals(user.getQueues().get(0).getPlayersInQueue().get(0).getLogin(),user.getLogin());
        Assert.assertEquals(400,wrongResult.getResponse().getStatus());
        gameRepository.delete(testGame);
        gameQueueRepository.delete(gameQueueRepository.findByGameName(testGame.getName()).get());

    }
    @Test
    @Transactional
    @SneakyThrows
    void removePlayerFromQueue() {
        String uri = "/queue/removePlayer";
        gameQueueService.addPlayerToQueue(testUser.getLogin(),testGame.getName());
        MvcResult goodResult = mvc.perform(MockMvcRequestBuilders.delete(uri).header("Authorization","Bearer "+jwtToken)
                .param("login",testUser.getLogin())
                .param("gameName",testGame.getName())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        MvcResult wrongResult = mvc.perform(MockMvcRequestBuilders.delete(uri).header("Authorization","Bearer "+jwtToken)
                .param("login","wrongLogin")
                .param("gameName",testGame.getName())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        User user = userRepository.getByLogin(testUser.getLogin());
        Assert.assertEquals(200, goodResult.getResponse().getStatus());
        Assert.assertEquals(400, wrongResult.getResponse().getStatus());
        Assert.assertEquals(gameQueueRepository.findAll().size(), 0);
        gameQueueRepository.deleteGameQueueByGameName(testGame.getName());

    }


}
