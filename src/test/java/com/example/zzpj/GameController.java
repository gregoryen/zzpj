package com.example.zzpj;

import com.example.zzpj.security.UserService;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.steamApi.Parser;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
public class GameController {

    static protected MockMvc mvc;
    static User testUser;
    static String jwtToken;
    @Autowired UserRepository userRepository;
    @Autowired UserService userService;
    @Autowired SteamApi steamApi;
    @Autowired Parser parser;


    @BeforeAll
     static void createTestAccount( @Autowired UserService userService, @Autowired JwtUtil jwtUtil, @Autowired
            WebApplicationContext webApplicationContext){
        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198105857198L);
        testUser = userService.registerNewUserAccount(accountDetails);
        UserTokenInformation uti = userService.getUserDetailsForToken(testUser.getLogin());
        jwtToken = jwtUtil.generateToken(uti,"none");
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void importGames() throws Exception {
        String uri = "/games/import";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).header("Authorization","Bearer "+jwtToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);

    }
    @Test
    void getUserGames() throws Exception{
        String uri = "/games/user";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).header("Authorization","Bearer "+jwtToken).param("steamId","99999999999999999L")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        Assert.assertEquals(parser.parseUserGames(mvcResult.getResponse().getContentAsString()).size(),0);
        //Jest >19 bo jakbym dokupil gre na swoim koncie test by sie wysypal a tak to przejdzie po dokupieniu nowej
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).header("Authorization","Bearer "+jwtToken).param("steamId",Long.toString(testUser.getSteamId()))
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        Assert.assertTrue(parser.parseUserGames(mvcResult.getResponse().getContentAsString()).size()>19);
    }
    @AfterAll
    static void cleanAfterTest(@Autowired UserRepository userRepository){
        userRepository.delete(userRepository.getByLogin("testtest12345678910"));
    }
}
