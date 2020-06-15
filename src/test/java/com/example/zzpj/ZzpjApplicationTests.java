package com.example.zzpj;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.configuration.CustomUserDetailsService;
import com.example.zzpj.security.jwt.JwtUtil;
import com.example.zzpj.service.GameService;
import com.example.zzpj.users.User;
import com.example.zzpj.users.UserRepository;
import com.example.zzpj.users.UserSignUpPOJO;
import com.example.zzpj.users.UserTokenInformation;
import com.example.zzpj.users.exceptions.LoginAlreadyUsedException;
import com.example.zzpj.users.exceptions.SteamIdAlreadyUsedException;
import com.example.zzpj.users.exceptions.UserException;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ZzpjApplicationTests {

	@Autowired
	GameService gameService;
	@Autowired
	UserService userService;
	@Autowired
	GameRepository gameRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	protected MockMvc mvc;

	static User testUser;
	@BeforeAll
	static void createTestAccount(@Autowired UserService userService, @Autowired JwtUtil jwtUtil, @Autowired
			WebApplicationContext webApplicationContext){
		UserSignUpPOJO accountDetails = new UserSignUpPOJO();
		accountDetails.setPassword("testtest12345678910");
		accountDetails.setLogin("testtest12345678910");
		accountDetails.setSteamId(76561198105857198L);
		testUser = userService.registerNewUserAccount(accountDetails);
	}
	@Test
	void importAllGames() throws Exception{
		gameService.importAllGamesFromSteam();
		Assert.assertTrue(gameRepository.findAll().size() >= 97420);
	}
	@Test
	void getUserGames() throws Exception{
		List<Long> gameList = gameService.getUserGamesFromSteam(Long.toString(testUser.getSteamId()));
		Assert.assertTrue(gameList.size() > 19);
		Assert.assertTrue(gameList.stream().filter(aLong -> aLong.equals(730L)).findAny().isPresent());
		Assert.assertTrue(gameList.stream().filter(aLong-> aLong.equals(205790L)).findAny().isPresent());
		Assert.assertThrows(Exception.class,()->{
			gameService.getUserGamesFromSteam("abcd");
		});
	}
	@Test
	@Transactional
	void insertUserGamesToDb() {
		User user = userRepository.getBySteamId(testUser.getSteamId());
		user.getGames().removeAll(user.getGames());
		userRepository.save(user);
		user = userRepository.getBySteamId(testUser.getSteamId());
		Assert.assertEquals(user.getGames().size(), 0);
		gameService.insertUserGamesToDb(Long.toString(testUser.getSteamId()));
		user = userRepository.getBySteamId(testUser.getSteamId());
		Assert.assertTrue(user.getGames().size() > 19);
	}

	@Test
	@Transactional
	void userService() {
		UserSignUpPOJO accountDetails = new UserSignUpPOJO();
		accountDetails.setPassword("testtesttesttesttesttest");
		accountDetails.setLogin("testtesttesttesttesttest");
		accountDetails.setSteamId(99999999999999999L);
		userService.registerNewUserAccount(accountDetails);
		UserTokenInformation uti = userService.getUserDetailsForToken("testtesttesttesttesttest");
		User user = userRepository.getBySteamId(99999999999999999L);

		Assert.assertEquals((long)uti.getSteamId(), 99999999999999999L);
		Assert.assertNotNull(user);
		Assert.assertThrows(LoginAlreadyUsedException.class,()->{
			userService.registerNewUserAccount(accountDetails);
		});
		accountDetails.setLogin("testtesttesttesttesttest12");
		Assert.assertThrows(SteamIdAlreadyUsedException.class,()->{
			userService.registerNewUserAccount(accountDetails);
		});
		Assert.assertEquals(user.getLogin(), "testtesttesttesttesttest");
		Assert.assertThrows(UsernameNotFoundException.class,()->{
			customUserDetailsService.loadUserByUsername("testtesttesttesttesttest1");
		});
		userRepository.delete(user);
	}
	@AfterAll
	static void cleanAfterTest(@Autowired UserRepository userRepository){
		userRepository.delete(userRepository.getByLogin("testtest12345678910"));
	}




}
