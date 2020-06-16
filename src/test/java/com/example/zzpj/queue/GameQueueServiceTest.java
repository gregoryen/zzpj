package com.example.zzpj.queue;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.queue.exception.GameNotFoundInUserCollectionException;
import com.example.zzpj.queue.exception.GameQueueNotExistException;
import com.example.zzpj.queue.exception.UserAlreadyInQueueException;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.game.GameService;
import com.example.zzpj.stats.UserStats;
import com.example.zzpj.stats.UserStatsService;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class GameQueueServiceTest {
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

    static User testUser;
    static User testUser2;
    static String jwtToken;
    static String gameName = "Counter-Strike: Global Offensive";
    static Game game;
    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired JwtUtil jwtUtil, @Autowired GameService gameService) {
        game = new Game();
        game.setAppid(730L);
        game.setName(gameName);
        game.setSquads(new HashSet<>());
        game.setUsers(new ArrayList<>());
        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198036881526L);
        testUser = userService.registerNewUserAccount(accountDetails);

        accountDetails.setLogin("testtest123456789102");
        accountDetails.setSteamId(76561198191481099L);
        testUser2 = userService.registerNewUserAccount(accountDetails);
        UserTokenInformation uti = userService.getUserDetailsForToken(testUser.getLogin());
        jwtToken = jwtUtil.generateToken(uti, "none");
    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository) {
        userRepository.delete(userRepository.getByLogin("testtest12345678910"));
        userRepository.delete(userRepository.getByLogin("testtest123456789102"));
    }
    @Test
    @Transactional
    @SneakyThrows
    void shouldAddPlayerToQueue() {
//                                //given
//                                User user = User.builder().login("login").steamId(123).password("password").build();
//                                Game game1 = new Game(0,"cs:go ",null,null);
//                                Game game2 = new Game(2,"StarCraft",null,null);
//                                when(userRepository.findByLogin(any())).thenReturn(java.util.Optional.ofNullable(user));
//                                when(gameService.getUserGameStats(123)).thenReturn(gameStats);
//                                when(gameRepository.getByAppid(0L)).thenReturn(game1);
//                                when(gameRepository.getByAppid(2L)).thenReturn(game2);
//                                userStatsService = new UserStatsService(gameService, gameRepository, userRepository, squadRepository);
//                                //when
//                                UserStats gameStats = userStatsService.getUserStats("login");
//                                //then
//                                assertEquals(gameStats.getGames(), 3);
//                                assertEquals(gameStats.getLogin(), "login");
//                                assertEquals(gameStats.getMostPlayedGame(), game2.getName());
//                                assertEquals(gameStats.getMostPlayedGame2Weeks(), game1.getName());
//                                assertEquals(gameStats.getPlaytime(), 750);
//                                assertEquals(gameStats.getPlaytime2Weeks(), 211);
            gameQueueService.addPlayerToQueue(testUser.getLogin(), gameName);
            Assert.assertThrows(UserAlreadyInQueueException.class, () -> {
                gameQueueService.addPlayerToQueue(testUser.getLogin(), gameName);
            });
            Assert.assertThrows(GameNotFoundInUserCollectionException.class, () -> {
                gameQueueService.addPlayerToQueue(testUser2.getLogin(), "Atari: 80 Classic Games in One!");
            });
            Assert.assertThrows(UsernameNotFoundException.class, () -> {
                gameQueueService.addPlayerToQueue("notExistingName", "Atari: 80 Classic Games in One!");
            });
            gameQueueService.addPlayerToQueue(testUser2.getLogin(), gameName);
            Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 2);
            gameQueueService.removeQueue(gameName);
    }
    @Test
    @Transactional
    @SneakyThrows
    void shouldRemovePlayerFromQueue(){
            gameQueueService.addPlayerToQueue(testUser.getLogin(), gameName);
            gameQueueService.addPlayerToQueue(testUser2.getLogin(), gameName);
            Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 2);
            gameQueueService.removePlayerFromQueue(testUser2.getLogin(), gameName);
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 1);
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().get(0).getLogin(), testUser.getLogin());
            gameQueueService.removePlayerFromQueue(testUser2.getLogin(), gameName);
            gameQueueService.removeQueue(gameName);
            Assert.assertTrue(gameQueueRepository.findByGameName(gameName).isEmpty());
    }
    @Test
    @Transactional
    @SneakyThrows
    void shouldRemoveQueue(){
        gameQueueService.addPlayerToQueue(testUser.getLogin(), gameName);
        Assert.assertNotNull(gameQueueService.findGameQueue(gameName));
        gameQueueService.removeQueue(gameName);
        Assert.assertNull(gameQueueService.findGameQueue(gameName));
        Assert.assertThrows(GameQueueNotExistException.class, ()->{
            gameQueueService.removeQueue(gameName);
        });
    }
}