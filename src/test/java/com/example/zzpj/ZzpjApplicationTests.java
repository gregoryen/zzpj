package com.example.zzpj;

import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.security.UserService;
import com.example.zzpj.security.configuration.CustomUserDetailsService;
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
	@Test
	void contextLoads() {
	}
	@Test
	void importAllGames() throws Exception{
		gameService.importAllGamesFromSteam();
		Assert.assertTrue(gameRepository.findAll().size() >= 97420);
	}
	@Test
	void getUserGames() throws Exception{
		List<Long> gameList = gameService.getUserGamesFromSteam("76561198105857198");
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
		User user = userRepository.getBySteamId(76561198105857198L);
		user.getGames().removeAll(user.getGames());
		userRepository.save(user);
		user = userRepository.getBySteamId(76561198105857198L);
		Assert.assertEquals(user.getGames().size(), 0);
		gameService.insertUserGamesToDb("76561198105857198");
		user = userRepository.getBySteamId(76561198105857198L);
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




}
