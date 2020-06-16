package com.example.zzpj.squad;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.game.GameService;
import com.example.zzpj.queue.GameQueueRepository;
import com.example.zzpj.queue.GameQueueService;
import com.example.zzpj.security.UserService;
import com.example.zzpj.squad.exceptions.SquadNotExistException;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SquadServiceTest {
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
        userRepository.delete(testUser);
    }

    @Test
    void shouldCreateAndRemoveSquad() {
       Squad squad1 = squadService.createSquad("1","1",game.getAppid());
       Squad squad2 = squadService.createSquad("2","2",game.getAppid());
        Assert.assertEquals(squadRepository.getAllByGame(game).size(),2);
        squadService.removeSquad(squad1.getId());
        squadService.removeSquad(squad2.getId());
        Assert.assertEquals(squadRepository.getAllByGame(game).size(),0);
        Assert.assertThrows(SquadNotExistException.class, ()->{
            squadService.removeSquad(squad1.getId()+1);
        });
    }
    @Test
    @Transactional
    void assignUser() {
        Squad squad1 = squadService.createSquad("1","1",game.getAppid());
        squadService.assignUser(squad1.getId(), testUser.getId());
        squadRepository.save(squad1);
        squad1 = squadRepository.getOne(squad1.getId());
        Assert.assertEquals(squad1.getUsers().get(0).getLogin(), testUser.getLogin());
        Assert.assertEquals(squad1.getUsers().size(),1);
        squadService.removeSquad(squad1.getId());
    }

}