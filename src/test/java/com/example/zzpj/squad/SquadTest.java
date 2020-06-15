package com.example.zzpj.squad;

import com.example.zzpj.game.GameRepository;
import com.example.zzpj.security.UserService;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SquadTest {

    @Autowired
    GameRepository gameRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

//    @Test
//    void addUser() {
//        UserSignUpPOJO accountDetails = new UserSignUpPOJO();
//        accountDetails.setPassword("testtest12345678910");
//        accountDetails.setLogin("testtest12345678910");
//        accountDetails.setSteamId(76561198105857198L);
//        User testUser = userService.registerNewUserAccount(accountDetails);
//        Squad squad = new Squad();
//        squad.setGame(gameRepository.getByAppid(730L));
//        squad.setId(1000L);
//        squad.setLevel("1000");
//        squad.addUser(testUser);
//
//    }

    @Test
    void pies(){
        userRepository.delete(userRepository.getByLogin("testtest12345678910"));
    }
}