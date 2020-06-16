package com.example.zzpj.security;


import com.example.zzpj.game.Game;
import com.example.zzpj.game.GameRepository;
import com.example.zzpj.steam_api.SteamApi;
import com.example.zzpj.users.*;
import com.example.zzpj.users.exceptions.LoginAlreadyUsedException;
import com.example.zzpj.users.exceptions.SteamIdAlreadyUsedException;
import com.example.zzpj.users.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userService")
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final UserSignUpPOJOToUserTransformer transformer;
    private final PasswordEncoder encoder;
    private final SteamApi steamApi;


    @Autowired
    public UserService(PasswordEncoder encoder, UserRepository userRepository, GameRepository gameRepository, UserSignUpPOJOToUserTransformer transformer, SteamApi steamApi) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.transformer = transformer;
        this.steamApi = steamApi;
    }

    @Override
    public User registerNewUserAccount(UserSignUpPOJO account) throws UserException {
        if (emailAlreadyExists(account)) {
            throw new LoginAlreadyUsedException("There is an account with that login: " + account.getLogin());
        }
        if(steamIdAlreadyExists(account)){
            throw new SteamIdAlreadyUsedException("There is an account with that steamId: " + account.getSteamId());
        }

        account.setPassword(encoder.encode(account.getPassword()));
        User user = transformer.transform(account);
        user = userRepository.save(user);
        insertUserGamesToDb(user.getSteamId());
        System.out.println(user.getGames());
        return user;
    }

    public boolean insertUserGamesToDb(Long steamId){
        try {
            List<Long> userGames = steamApi.getUserGamesFromSteam(steamId);
            User user = userRepository.getBySteamId(steamId);
            user.setGames(new ArrayList<>());
            for(Long id : userGames){
                Game game =gameRepository.getByAppid(id);
                user.getGames().add(game);
            }
            userRepository.save(user);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public UserTokenInformation getUserDetailsForToken(String login) {
        User user = userRepository.getByLogin(login);
        UserToUserTokenInformationTransformer transformer = new UserToUserTokenInformationTransformer();
        return transformer.transform(user);
    }


    private boolean emailAlreadyExists(UserSignUpPOJO account) {
        User alreadyRegistered = userRepository.getByLogin(account.getLogin());

        return alreadyRegistered != null;
    }
    private boolean steamIdAlreadyExists(UserSignUpPOJO account) {
        User alreadyRegistered = userRepository.getBySteamId(account.getSteamId());

        return alreadyRegistered != null;
    }
}