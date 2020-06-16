package com.example.zzpj.ranking;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.game.GameService;
import com.example.zzpj.queue.GameQueueRepository;
import com.example.zzpj.queue.GameQueueService;
import com.example.zzpj.security.UserService;
import com.example.zzpj.squad.Squad;
import com.example.zzpj.squad.SquadRepository;
import com.example.zzpj.squad.SquadService;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RateServiceTest {

    @Autowired
    GameService gameService;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameQueueService gameQueueService;
    @Autowired
    GameQueueRepository gameQueueRepository;
    @Autowired
    SquadService squadService;
    @Autowired
    RateService rateService;
    @Autowired
    RateRepository rateRepository;
    @Autowired
    SquadRepository squadRepository;
    static User testUser;
    static Game game;
    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired GameRepository gameRepository) {

        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198036881526L);
        testUser = userService.registerNewUserAccount(accountDetails);
        game = gameRepository.getByAppid(730L);
    }

    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository) {
        userRepository.delete(userRepository.getByLogin("testtest12345678910"));
    }
    @Transactional
    @Test
    void rateUser() {
        Squad squad1 = squadService.createSquad("1","1",game.getAppid());
    squadService.assignUser(squad1.getId(),testUser.getId());
        Rate rate = new Rate();
        rate.setRateValue(1.0);
    rate.setFkSquadId(squad1.getId());
    rate.setFkUserId(testUser.getId());
        Assert.assertEquals( rateService.rateUser(rate), "");
        Assert.assertEquals(rateRepository.getOne(rate.getId()).getRateValue(),1.0, 0.000001);
        Assert.assertEquals(rateRepository.getOne(rate.getId()).getFkUserId(),testUser.getId());
        Assert.assertEquals(rateRepository.getOne(rate.getId()).getFkSquadId(),(long)squad1.getId());
    squadService.removeSquad(squad1.getId());
    }


    @Test
    void getRankingBySquadId() {
        //TODO
    }


}