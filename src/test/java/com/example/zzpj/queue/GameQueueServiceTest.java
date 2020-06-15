package com.example.zzpj.queue;

import com.example.zzpj.game.GameRepository;
import com.example.zzpj.queue.exception.GameNotFoundInUserCollectionException;
import com.example.zzpj.queue.exception.GameQueueNotExistException;
import com.example.zzpj.queue.exception.UserAlreadyInQueueException;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.game.GameService;
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
import java.util.Optional;
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

    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired JwtUtil jwtUtil, @Autowired GameService gameService) {

        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198105857198L);
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
        gameQueueService.addPlayerToQueue(testUser.getLogin(), gameName);
        Assert.assertThrows(UserAlreadyInQueueException.class, ()->{gameQueueService.addPlayerToQueue(testUser.getLogin(),gameName);});
        Assert.assertThrows(GameNotFoundInUserCollectionException.class, ()->{gameQueueService.addPlayerToQueue(testUser2.getLogin(),"Atari: 80 Classic Games in One!");});
        Assert.assertThrows(UsernameNotFoundException.class, ()->{gameQueueService.addPlayerToQueue("notExistingName","Atari: 80 Classic Games in One!");});
        gameQueueService.addPlayerToQueue(testUser2.getLogin(),gameName);
        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
        Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 2);
        gameQueueService.removeQueue(gameName);

    }
    @Test
    @Transactional
    @SneakyThrows
    void shouldRemovePlayerFromQueue(){
        gameQueueService.addPlayerToQueue(testUser.getLogin(), gameName);
        gameQueueService.addPlayerToQueue(testUser2.getLogin(),gameName);
        Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(gameName);
        Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 2);
        gameQueueService.removePlayerFromQueue(testUser2.getLogin(),gameName);
        Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 1);
        Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().get(0).getLogin(),testUser.getLogin());
        gameQueueService.removePlayerFromQueue(testUser2.getLogin(),gameName);
        gameQueueService.removeQueue(gameName);
        Assert.assertTrue(gameQueueRepository.findByGameName(gameName).isEmpty());
    }
    //
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