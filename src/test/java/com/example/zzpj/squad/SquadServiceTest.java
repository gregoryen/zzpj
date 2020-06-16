package com.example.zzpj.squad;

import com.example.zzpj.InitTestObjects;
import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.steam_api.SteamApi;
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
import java.util.List;

@SpringBootTest
class SquadServiceTest {
    @Autowired
    SteamApi steamApi;
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
    static Game testGame;
    static User testUser2;

    static UserSignUpPOJO accountDetails;
    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired UserRepository userRepository, @Autowired GameRepository gameRepository){
        testUser = InitTestObjects.initUser();
        userRepository.save(testUser);
        testUser2 = InitTestObjects.initUser();
        testUser2.setLogin("test2");
        testUser2.setSteamId(76561198191481099L);
        userRepository.save(testUser2);
        testGame = InitTestObjects.initGame();
        gameRepository.save(testGame);

    }

    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository,@Autowired GameRepository gameRepository) {
        userRepository.delete(testUser);
        userRepository.delete(testUser2);
        gameRepository.delete(testGame);
    }

    //TODO

    @Test
    @Transactional
    void shouldCreateAndRemoveSquad() {

       squadService.createSquad("1","1",testGame.getAppid(), testUser.getLogin());
       squadService.createSquad("2","2",testGame.getAppid(),testUser.getLogin());
        Assert.assertEquals(squadRepository.getAllByGame(testGame).size(),2);
        List<Squad> squads = squadRepository.findAll();
        Assert.assertEquals("1",squads.get(0).getName());
        Assert.assertEquals("1",squads.get(0).getLevel());
        Assert.assertEquals(testGame.getAppid(),squads.get(0).getGame().getAppid());
        System.out.println(squads.get(0).getId());
        squadService.removeSquad(squads.get(0).getId());
        squadService.removeSquad(squads.get(1).getId());
        Assert.assertEquals(squadRepository.getAllByGame(testGame).size(),0);
        Assert.assertThrows(SquadNotExistException.class, ()->{
            squadService.removeSquad(squads.get(0).getId()+1);
        });
    }
    @Test
    @Transactional
    void assignUser() {
        squadService.createSquad("1","1",testGame.getAppid(),testUser.getLogin());
        Squad squad1 = squadRepository.findAll().get(0);
        squadService.assignUser(squad1.getId(), testUser2.getId(),testUser.getLogin());
        squadRepository.save(squad1);
        squad1 = squadRepository.getOne(squad1.getId());
        Assert.assertEquals(squad1.getUsers().get(0).getLogin(), testUser.getLogin());
        Assert.assertEquals(squad1.getUsers().get(1).getLogin(), testUser2.getLogin());
        Assert.assertEquals(squad1.getUsers().size(),2);
        squadService.removeSquad(squad1.getId());
    }

}