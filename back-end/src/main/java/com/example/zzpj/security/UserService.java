package com.example.zzpj.security;


import com.example.zzpj.users.*;
import com.example.zzpj.users.exceptions.LoginAlreadyUsedException;
import com.example.zzpj.users.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserSignUpPOJOToUserTransformer transformer;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(PasswordEncoder encoder, UserRepository userRepository, UserSignUpPOJOToUserTransformer transformer) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.transformer = transformer;
    }

    @Override
    public User registerNewUserAccount(UserSignUpPOJO account) throws UserException {
        if (emailAlreadyExists(account)) {
            throw new LoginAlreadyUsedException("There is an account with that login: " + account.getLogin());
        }

        account.setPassword(encoder.encode(account.getPassword()));
        User user = transformer.transform(account);

        return userRepository.save(user);
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
}