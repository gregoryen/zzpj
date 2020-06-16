package com.example.zzpj.security;

import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import com.example.zzpj.users.exceptions.LoginAlreadyUsedException;
import com.example.zzpj.users.exceptions.SteamIdAlreadyUsedException;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    static User testUser;
    static UserSignUpPOJO accountDetails;
    @BeforeAll
    static void setUp(@Autowired UserService userService){
        accountDetails = new UserSignUpPOJO();
        accountDetails.setPassword("testtest12345678910");
        accountDetails.setLogin("testtest12345678910");
        accountDetails.setSteamId(76561198253700224L);
        testUser = userService.registerNewUserAccount(accountDetails);
    }
    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository){
        userRepository.delete(testUser);
    }
    @Test
    void shouldThrowLoginAlreadyUsedException(){
        accountDetails.setLogin("testtest12345678910");
        Assert.assertThrows(LoginAlreadyUsedException.class,()->{
            userService.registerNewUserAccount(accountDetails);
        });
    }
    @Test
    void shouldThrowSteamIdAlreadyUsedException(){
        accountDetails.setLogin("innyLoginForTest");
        Assert.assertThrows(SteamIdAlreadyUsedException.class,()->{
            userService.registerNewUserAccount(accountDetails);
        });
    }
}