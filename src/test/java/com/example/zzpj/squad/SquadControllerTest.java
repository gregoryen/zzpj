package com.example.zzpj.squad;

import com.example.zzpj.InitTestObjects;
import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.queue.GameQueueRepository;
import com.example.zzpj.queue.GameQueueService;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.configuration.CustomUserDetailsService;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.steam_api.SteamApi;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import com.example.zzpj.users.UserTokenInformation;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@WithUserDetails("test1")
class SquadControllerTest {
    static MockMvc mvc;
    static User testUser;
    static String jwtToken;
    static Game testGame;
    static User testUser2;
    static String jwtToken2;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    SteamApi steamApi;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    GameQueueRepository gameQueueRepository;
    @Autowired
    SquadRepository squadRepository;
    @Autowired
    GameQueueService gameQueueService;
    @Autowired
    SquadService squadService;


    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired JwtUtil jwtUtil, @Autowired WebApplicationContext webApplicationContext, @Autowired GameRepository gameRepository, @Autowired UserRepository userRepository, @Autowired AuthenticationManager authenticationManager){
        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("1234");
        accountDetails.setLogin("test1");
        accountDetails.setSteamId(76561198191481099L);
        testUser = userService.registerNewUserAccount(accountDetails);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(testUser.getLogin(), "1234"));
        UserTokenInformation uti = userService.getUserDetailsForToken(testUser.getLogin());
        jwtToken = jwtUtil.generateToken(uti,"none");
        accountDetails.setLogin("test2");
        accountDetails.setSteamId(76561198253700224L);
        testUser2 = userService.registerNewUserAccount(accountDetails);
        UserTokenInformation uti2 = userService.getUserDetailsForToken(testUser2.getLogin());
        jwtToken2 = jwtUtil.generateToken(uti2,"none");

        testUser.setGames(new ArrayList<>());
        testUser2.setGames(new ArrayList<>());
        testGame = InitTestObjects.initGame();
        testUser.getGames().add(testGame);
        testUser2.getGames().add(testGame);
        gameRepository.save(testGame);
        userRepository.saveAll(Arrays.asList(testUser,testUser2));

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository, @Autowired GameRepository gameRepository){
        testGame.getUsers().removeAll(Arrays.asList(testUser,testUser2));
        testUser.getGames().remove(testGame);
        testUser2.getGames().remove(testGame);
        userRepository.saveAll(Arrays.asList(testUser,testUser2));
        userRepository.deleteAll(Arrays.asList(testUser,testUser2));
        gameRepository.delete(testGame);

    }
    @Test
    @Transactional
    @SneakyThrows
    void createSquad() {
        String uri = "/squad/create";
        System.out.println(jwtToken+ " \n"+ "TOKEN");
        MvcResult goodResult = mvc.perform(MockMvcRequestBuilders.post(uri).header("Authorization","Bearer "+jwtToken)
                .param("name","1")
                .param("level","1")
                .param("gameId","730").header("Content-Type","application/json")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        MvcResult wrongResult = mvc.perform(MockMvcRequestBuilders.post(uri).header("Authorization","Bearer "+jwtToken)
                .param("name","1")
                .param("level","1")
                .param("gameId","1111").header("Content-Type","application/json")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        System.out.println(goodResult.getResponse().getContentAsString());
        Assert.assertEquals(200,goodResult.getResponse().getStatus());
        Assert.assertEquals(400,wrongResult.getResponse().getStatus());
        Assert.assertEquals(squadRepository.getAllByGame(testGame).size(), 1);

        userRepository.getByLogin(testUser.getLogin()).getSquads().removeAll(squadRepository.findAll());
        userRepository.save(userRepository.getByLogin(testUser.getLogin()));
        squadRepository.deleteAll();
    }
    @Test
    @Transactional
    @SneakyThrows
    void assignUser() {
        String uri = "/squad/assign";
        squadService.createSquad("1","1",730L,testUser.getLogin());
        Squad squad = squadRepository.findAll().get(0);
        MvcResult goodResult = mvc.perform(MockMvcRequestBuilders.put(uri).header("Authorization","Bearer "+jwtToken)
                .param("squadId",squad.getId().toString())
                .param("userId",Long.toString(testUser2.getId()))
                .param("login",testUser.getLogin()).header("Content-Type","application/json")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        MvcResult wrongResult1 = mvc.perform(MockMvcRequestBuilders.put(uri).header("Authorization","Bearer "+jwtToken)
                .param("squadId",squad.getId().toString())
                .param("userId",Long.toString(testUser.getId()))
                .param("login",testUser.getLogin()).header("Content-Type","application/json")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        MvcResult wrongResult2 = mvc.perform(MockMvcRequestBuilders.put(uri).header("Authorization","Bearer "+jwtToken)
                .param("squadId","12333")
                .param("userId",Long.toString(testUser2.getId()))
                .param("login",testUser.getLogin()).header("Content-Type","application/json")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        Assert.assertEquals(goodResult.getResponse().getStatus(),200);
        Assert.assertEquals(wrongResult1.getResponse().getStatus(),400);
        Assert.assertEquals(wrongResult2.getResponse().getStatus(),400);

        userRepository.getByLogin(testUser.getLogin()).getSquads().removeAll(squadRepository.findAll());
        userRepository.getByLogin(testUser2.getLogin()).getSquads().removeAll(squadRepository.findAll());
        userRepository.save(userRepository.getByLogin(testUser.getLogin()));
        userRepository.save(userRepository.getByLogin(testUser2.getLogin()));
        squadRepository.deleteAll();
    }


}
