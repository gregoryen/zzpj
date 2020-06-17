package com.example.zzpj.queue;

import com.example.zzpj.InitTestObjects;
import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.queue.exception.GameNotFoundInUserCollectionException;
import com.example.zzpj.queue.exception.GameQueueNotExistException;
import com.example.zzpj.queue.exception.UserAlreadyInQueueException;
import com.example.zzpj.security.UserService;
import com.example.zzpj.steam_api.SteamApi;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;

@SpringBootTest
class GameQueueServiceTest {
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
    static User testUser;
    static Game testGame;
    static User testUser2;
    @BeforeAll
    static void setUp(@Autowired UserService userService, @Autowired UserRepository userRepository, @Autowired GameRepository gameRepository){
        testUser = InitTestObjects.initUser();
        testUser2 = InitTestObjects.initUser();
        testUser2.setLogin("test2");
        testUser2.setSteamId(76561198191481099L);
        testGame = InitTestObjects.initGame();

        testUser.getGames().add(testGame);
        testUser2.getGames().add(testGame);
        testGame.getUsers().addAll(Arrays.asList(testUser,testUser2));
        gameRepository.save(testGame);
        userRepository.saveAll(Arrays.asList(testUser,testUser2));

    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository, @Autowired GameRepository gameRepository){
        testGame.getUsers().removeAll(Arrays.asList(testUser,testUser2));
        testUser.getGames().remove(testGame);
        testUser2.getGames().remove(testGame);
        userRepository.saveAll(Arrays.asList(testUser,testUser2));
        userRepository.delete(testUser);
        userRepository.delete(testUser2);
        gameRepository.delete(testGame);
    }
    @Test
    @Transactional
    @SneakyThrows
    void shouldAddPlayerToQueue() {
            gameQueueService.addPlayerToQueue(testUser.getLogin(), testGame.getName());
            Assert.assertThrows(UserAlreadyInQueueException.class, () -> {
                gameQueueService.addPlayerToQueue(testUser.getLogin(), testGame.getName());
            });
            Assert.assertThrows(GameNotFoundInUserCollectionException.class, () -> {
                gameQueueService.addPlayerToQueue(testUser2.getLogin(), "Atari: 80 Classic Games in One!");
            });
            Assert.assertThrows(UsernameNotFoundException.class, () -> {
                gameQueueService.addPlayerToQueue("notExistingName", "Atari: 80 Classic Games in One!");
            });
            gameQueueService.addPlayerToQueue(testUser2.getLogin(), testGame.getName());
            Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(testGame.getName());
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 2);
            gameQueueService.removeQueue(testGame.getName());
    }
    @Test
    @Transactional
    @SneakyThrows
    void shouldRemovePlayerFromQueue(){
            gameQueueService.addPlayerToQueue(testUser.getLogin(), testGame.getName());
            gameQueueService.addPlayerToQueue(testUser2.getLogin(), testGame.getName());
            Optional<GameQueue> optionalGameQueue = gameQueueRepository.findByGameName(testGame.getName());
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 2);

            gameQueueService.removePlayerFromQueue(testUser2.getLogin(), testGame.getName());
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().size(), 1);
            Assert.assertEquals(optionalGameQueue.get().getPlayersInQueue().get(0).getLogin(), testUser.getLogin());

            gameQueueService.removePlayerFromQueue(testUser2.getLogin(), testGame.getName());
            gameQueueService.removeQueue(testGame.getName());
            Assert.assertTrue(gameQueueRepository.findByGameName(testGame.getName()).isEmpty());
    }
    @Test
    @Transactional
    @SneakyThrows
    void shouldRemoveQueue(){
        gameQueueService.addPlayerToQueue(testUser.getLogin(), testGame.getName());
        Assert.assertNotNull(gameQueueService.findGameQueue(testGame.getName()));
        gameQueueService.removeQueue(testGame.getName());
//        Assert.assertThrows(GameQueueNotExistException.class, ()->{
//            gameQueueService.findGameQueue(testGame.getName());
//        });
//        Assert.assertThrows(GameQueueNotExistException.class, ()->{
//            gameQueueService.removeQueue(testGame.getName());
//        });
    }
}