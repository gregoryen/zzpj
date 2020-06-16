package com.example.zzpj.game;

import com.example.zzpj.security.UserService;
import com.example.zzpj.security.jwt.JwtUtil;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class GameControllerTest {

    static MockMvc mvc;
    static User testUser;
    static String jwtToken;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired GameService gameService;
    @Autowired GameRepository gameRepository;


    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired JwtUtil jwtUtil, @Autowired WebApplicationContext webApplicationContext){
        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198191481099L);
        testUser = userService.registerNewUserAccount(accountDetails);
        UserTokenInformation uti = userService.getUserDetailsForToken(testUser.getLogin());
        jwtToken = jwtUtil.generateToken(uti,"none");
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository){
        userRepository.delete(userRepository.getByLogin("testtest12345678910"));
    }

    @Test
    void    shouldImportGames() throws Exception {
        String uri = "/games/import";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).header("Authorization","Bearer "+jwtToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        Assert.assertTrue(gameRepository.findAll().size() >= 97420);
        gameRepository.deleteAll();

    }
    @Test
    void shouldReturnAllUserGames() throws Exception{
        String uri = "/games/user";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).header("Authorization","Bearer "+jwtToken).param("steamId","99999999999999999")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        Assert.assertEquals(mvcResult.getResponse().getContentAsString(),"[]");
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).header("Authorization","Bearer "+jwtToken).param("steamId",Long.toString(testUser.getSteamId()))
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        System.out.println(Arrays.asList(mvcResult.getResponse().getContentAsString().replace("[","").replace("]","").split(",")).size());
        Assert.assertTrue(Arrays.asList(mvcResult.getResponse().getContentAsString().replace("[","").replace("]","").split(",")).size()>=209);

    }

}